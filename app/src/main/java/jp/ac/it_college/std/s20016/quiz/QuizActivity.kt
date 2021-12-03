package jp.ac.it_college.std.s20016.quiz

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.ac.it_college.std.s20016.quiz.databinding.ActivityQuizBinding
import jp.ac.it_college.std.s20016.quiz.helper.DBHandler
import jp.ac.it_college.std.s20016.quiz.helper.RecyclerAdapter
import kotlin.math.ceil

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private var layoutManager: RecyclerView.LayoutManager? = null
    private val dbHelper = DBHandler(this)
    private var alertDialog: AlertDialog? = null
    private var quitQuiz = false

    // Game variables
    private var apiQuestionSize = 0
    private var question = 0
    private var score = 0

    // Data API variables
    private val dataQuestion = mutableListOf<String>()
    private val dataAnswers = mutableListOf<String>()
    private val dataChoices = mutableListOf<List<String>>()
    private var itemCount = String()
    private var position = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        position = intent.getStringExtra("POSITION").toString()
        binding.btnHome.setOnClickListener { alertDialog?.show() }
        itemCount = position

        generateQuizSet(position)
        gameOn()
    }

    override fun onStop() {
        super.onStop()
        quitQuiz = true
        question = position.toInt()
    }

    private fun backHomeDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Already Quitting?")
        dialogBuilder.setMessage("Are the questions too hard for you, weakling? " +
                "You sure you want to quit?")
        dialogBuilder.setPositiveButton("Quit") { _: DialogInterface, _: Int ->
            Intent (this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
        dialogBuilder.setNegativeButton("Cancel") { _: DialogInterface, _: Int -> }
        alertDialog = dialogBuilder.create()
    }

    override fun onBackPressed() {
        alertDialog?.show()
    }

    private fun generateQuizSet(position: String) {
        val id = dbHelper.readDataId()

        apiQuestionSize = position.toInt()

        val selectedQuestions = id.shuffled().take(apiQuestionSize).toList()
        Log.d("QuizAct", "Randomized Questions: $selectedQuestions")

        selectedQuestions.forEach {
            val data = dbHelper.readValues(it)
            val rows = data[0]
                .replace("[", "")
                .replace("]", "")
                .split(", ")

            dataQuestion.add(rows[1])
            dataAnswers.add(rows[2])
            dataChoices.add(
                listOf(rows[3], rows[4], rows[5], rows[6], rows[7], rows[8])
            )
        }
    }

    // Message and score(points)
    private fun getResult(score: Int): Pair<String, String> {
        val points = "$score/$apiQuestionSize"
        val q4 = ceil((apiQuestionSize.toDouble() * 1 / 4) * 3).toInt()
        val q3 = ceil((apiQuestionSize.toDouble() * 1 / 4) * 2).toInt()
        val q2 = ceil(apiQuestionSize.toDouble() * 1 / 4).toInt()
        val message: String = when {
            score > q4 -> "Great Job!"
            score > q3 -> "Fantastic!"
            score > q2 -> "Nice!"
            else -> "Try Again!"
        }; return Pair(points, message)
    }

    // Passing data (msg, pts) to ResultActivity
    private fun setScore() {
        val (points, message) = getResult(score)
        val intent = Intent(this, ResultActivity::class.java)

        intent.putExtra("SCORE", points)
        intent.putExtra("MESSAGE", message)
        intent.putExtra("ITEM_COUNT", itemCount)
        startActivity(intent)
        finish()
    }

    private fun compareAnswer(
        userAns: MutableList<Int>,
        dataAns: MutableList<Int>): Boolean {
        val ret = userAns.containsAll(dataAns)

        if (ret) binding.quizScore.setTextColor(Color.parseColor("#99ff80"))
        if (!ret) binding.quizScore.setTextColor(Color.parseColor("#ff7373"))
        return ret
    }

    // Main Game
    private fun gameOn() {

        backHomeDialog()
        if (quitQuiz) finish()

        // Question and Choices Variables
        question++

        val questionNumber = question - 1
        val questionQuestion = dataQuestion[questionNumber]
        val questionChoices = dataChoices[questionNumber].filter { x: String? -> x != "NULL" }
        val questionAnswers = dataAnswers[questionNumber]

        val realAnswers = questionChoices.take(questionAnswers.toInt())
        val shuffledChoices = questionChoices.shuffled()
        val shuffledAnswers = mutableListOf<Int>()

        shuffledChoices.forEachIndexed { index, s ->
            if (s in realAnswers) shuffledAnswers.add(index)
        }

        // Log shuffled answers
        Log.d("QuizAct", "Answer Position: $shuffledAnswers")

        // Assigning data to each ID
        binding.quizQuestion.text = questionQuestion

        // RecycleView
        layoutManager = LinearLayoutManager(this)
        binding.rvChoiceItems.layoutManager = layoutManager

        val adapter = RecyclerAdapter(shuffledChoices, questionAnswers.toInt())
        binding.rvChoiceItems.adapter = adapter

        var userChoicesInt = mutableListOf<Int>()
        adapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener {
            override fun onItemClick(userChoice: MutableList<Int>) {
                userChoicesInt = userChoice
            }
        })

        // Score
        val scoreDisplay =
            "SCORE: $score  |  Answer(s): $questionAnswers  |  Q: $question/$apiQuestionSize"
        binding.quizScore.text = scoreDisplay


        // Timer
        var timeLimit = 16000
        if (question <= 1) timeLimit = 16000

        val timer = object : CountDownTimer(timeLimit.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = (millisUntilFinished / 1000).toInt()
                val timeText = String.format("00:%02d", time)
                if (time <= 14) binding.quizScore.setTextColor(Color.parseColor("#FFFFFF"))
                if (time <= 3) binding.quizTimer.setTextColor(Color.parseColor("#FF4C29"))
                if (time >= 4) binding.quizTimer.setTextColor(Color.parseColor("#FFFFFF"))
                binding.quizTimer.text = timeText
            }

            override fun onFinish() {
                if (!quitQuiz) {
                    val userIsCorrect = compareAnswer(userChoicesInt, shuffledAnswers)
                    Log.d("onFinish", "User: $userChoicesInt, Data: " +
                            "$shuffledAnswers, $userIsCorrect")
                    if (userIsCorrect) score++
                    if (question >= apiQuestionSize) setScore() else gameOn()
                }
            }
        }.start()

        binding.nextButton.setOnClickListener {
            val userIsCorrect = compareAnswer(userChoicesInt, shuffledAnswers)
            timer.cancel()
            Log.d("onClick", "User: $userChoicesInt, Data: $shuffledAnswers, $userIsCorrect")

            if (!quitQuiz) {
                if (userIsCorrect) score++
                if (question >= apiQuestionSize) setScore() else gameOn()
            }
        }
    }
}