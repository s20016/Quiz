package jp.ac.it_college.std.s20016.quiz

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import jp.ac.it_college.std.s20016.quiz.databinding.ActivityQuizBinding
import kotlinx.coroutines.*
import kotlin.math.ceil

class QuizActivity : AppCompatActivity() {

    // Game variables
    private var apiQuestionSize = 0
    private var question = 0
    private var score = 0

    // Data API variables
    private val dataQuestion = mutableListOf<String>()
    private val dataId = mutableListOf<List<String>>()
    private val dataAnswers = mutableListOf<Int>()
    private val dataChoices = mutableListOf<List<String>>()
    private var itemCount = ""

    private lateinit var binding: ActivityQuizBinding
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val position = intent.getStringExtra("POSITION").toString()
        itemCount = position

        runBlocking {
            val msg = "Randomizing Questions!"
            Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
            delay(3500L)
            generateQuizSet(position)
        }

        gameOn()
    }

    // Retrieve saved data and randomize
    private fun generateQuizSet(position: String) {
        val dataSharedPref = getSharedPreferences("ApiDataPref", Context.MODE_PRIVATE)
        val apiID = dataSharedPref.all.map { it.key }

        dataId.add(apiID.toList())
        dataId[0].shuffled()

        val pos = when(position) {
            "0" -> 10
            "1" -> 20
            "2" -> 30
            else -> 50
        }

        apiQuestionSize = pos
        val selectedQuestions = dataId[0].asSequence().shuffled().take(apiQuestionSize).toList()

        for (item in selectedQuestions) {
            val rawData = dataSharedPref.getString(item, "")
            val data = Gson().fromJson(rawData, ApiDataItem::class.java)
            dataAnswers.add(data.answers)
            dataQuestion.add(data.question)
            dataChoices.add(data.choices)
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
    }


    // Main Game
    private fun gameOn() {

        question++

        // Question and Choices Variables
        val questionNumber = question - 1
        val questionQuestion = dataQuestion[questionNumber]
        val questionChoices = dataChoices[questionNumber].filter { x: String? -> x != "" }
        val questionAnswers = dataAnswers[questionNumber]

        val realAnswers = questionChoices.take(questionAnswers)
        val shuffledChoices = questionChoices.shuffled()
        val shuffledAnswers = mutableListOf<Int>()

        shuffledChoices.forEachIndexed { index, s ->
            if (s in realAnswers) shuffledAnswers.add(index)
        }

        // Log shuffled answers
        Log.d("TEST: ", "Question Answer $shuffledAnswers")

        // Assigning data to each ID
        binding.quizQuestion.text = questionQuestion

        // RecycleView
        layoutManager = LinearLayoutManager(this)
        binding.rvChoiceItems.layoutManager = layoutManager

        val adapter = RecyclerAdapter(shuffledChoices, questionAnswers)
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

        // Compare userAnswer and dataAnswer
        fun compareAnswers(): Boolean {
            return userChoicesInt.containsAll(shuffledAnswers)
        }

        // Timer
        var timeLimit = 15000
        if (question <= 1) timeLimit = 16000

        val timer = object : CountDownTimer(timeLimit.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = (millisUntilFinished / 1000).toInt()
                val timeText = String.format("00:%02d", time)
                if (time <= 3) binding.quizTimer.setTextColor(Color.parseColor("#FF4C29"))
                if (time >= 4) binding.quizTimer.setTextColor(Color.parseColor("#FFFFFF"))
                binding.quizTimer.text = timeText
            }

            override fun onFinish() {
                Log.d("OnTimeFinish: ", "User: $userChoicesInt, Data: $shuffledAnswers, ${compareAnswers()}")

                if (compareAnswers()) score++
                if (question >= apiQuestionSize) setScore() else gameOn()
            }
        }; timer.start()

        binding.nextButton.setOnClickListener {
            timer.cancel()
            Log.d("OnClick: ", "User: $userChoicesInt, Data: $shuffledAnswers, ${compareAnswers()}")

            if (compareAnswers()) score++
            if (question >= apiQuestionSize) setScore() else gameOn()
        }
    }
}