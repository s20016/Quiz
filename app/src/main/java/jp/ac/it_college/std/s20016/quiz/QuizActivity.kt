package jp.ac.it_college.std.s20016.quiz

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayoutStates
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import jp.ac.it_college.std.s20016.quiz.databinding.ActivityQuizBinding
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

    private lateinit var binding: ActivityQuizBinding
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        generateQuizSet()
        gameOn()

    }

    // Retrieve saved data and randomize
    private fun generateQuizSet() {
        val dataSharedPref = getSharedPreferences("ApiDataPref", Context.MODE_PRIVATE)
        val apiID = dataSharedPref.all.map { it.key }

        dataId.add(apiID.toList())
        dataId[0].shuffled()

        apiQuestionSize = dataId[0].size
        for (item in dataId[0]) {
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
        val q4 = ceil((apiQuestionSize.toDouble() * 1/4) * 3).toInt()
        val q3 = ceil((apiQuestionSize.toDouble() * 1/4) * 2).toInt()
        val q2 = ceil(apiQuestionSize.toDouble() * 1/4).toInt()
        val q1 = ceil(apiQuestionSize.toDouble() * 1/8).toInt()
        val message: String = when (score) {
            in q3..q4 -> "Great Job!"
            in q2..q3 -> "Fantastic!"
            in q1..q2 -> "Nice!"
            else -> "Try Again!"
        }; return Pair(points, message)
    }

    // Passing data (msg, pts) to ResultActivity
    private fun setScore() {
        val (points, message) = getResult(score)
        val intent = Intent(this, ResultActivity::class.java)

        intent.putExtra("SCORE", points)
        intent.putExtra("MESSAGE", message)
        startActivity(intent)
    }

//    // Check if user option is correct
//    private fun checkOption(choice: Int): Boolean {
//        return (choice == 2)
//    }

    // Main Game
    private fun gameOn() {
        question++

        // Timer
//        var timeLimit = 15000
//        if (question <= 1) timeLimit = 16000
//
//        val timer = object : CountDownTimer(timeLimit.toLong(), 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val time = (millisUntilFinished / 1000).toInt()
//                val timeText = String.format("00:%02d", time)
//                if (time <= 3) binding.quizTimer.setTextColor(Color.parseColor("#FF4C29"))
//                if (time >= 4) binding.quizTimer.setTextColor(Color.parseColor("#FFFFFF"))
//                binding.quizTimer.text = timeText
//            }
//
//            override fun onFinish() {
//                if (question >= apiQuestionSize) setScore() else gameOn()
//            }
//        }; timer.start()


        // Question and Choices Variables
        val questionNumber = question - 1
        val questionQuestion = dataQuestion[questionNumber]
        val questionChoices = dataChoices[questionNumber].filter { x: String? -> x != "" }
        val questionAnswers = dataAnswers[questionNumber]

        val choices = mutableListOf(2, 3, 4, 5); choices.shuffle()
        val choice1 = choices[0]
        val choice2 = choices[1]
        val choice3 = choices[2]
        val choice4 = choices[3]


        // TODO: RecycleView
        layoutManager = LinearLayoutManager(this)
        binding.rvChoiceItems.layoutManager = layoutManager

        adapter = RecyclerAdapter(questionChoices, questionAnswers)
        binding.rvChoiceItems.adapter = adapter


        // Assigning data to each ID
        binding.quizQuestion.text = questionQuestion
//        quizOption1.text = dataQuestion[questionNumber][choice1]
//        quizOption2.text = dataQuestion[questionNumber][choice2]
//        quizOption3.text = dataQuestion[questionNumber][choice3]
//        quizOption4.text = dataQuestion[questionNumber][choice4]

        // Score
        val scoreDisplay = "SCORE: $score  |  Answer(s): $questionAnswers  |  Q: $question/$apiQuestionSize"
        binding.quizScore.text = scoreDisplay

        Log.d("TEST apiQuestionSize: ", apiQuestionSize.toString())
        Log.d("TEST question: ", question.toString())

        Log.d("TEST question: ", dataQuestion[questionNumber])
        Log.d("TEST question: ", dataChoices[questionNumber].toString())
        Log.d("TEST question: ", dataAnswers[questionNumber].toString())
        Log.d("TEST question: ", dataId[0][questionNumber])

//        // Setting answer to Random
//        quizOption1.setOnClickListener {
//            timer.cancel()
//            if (checkOption(choice1)) score++
//            if (question >= 10) setScore() else gameOn()
//        }
//
//        quizOption2.setOnClickListener {
//            timer.cancel()
//            if (checkOption(choice2)) score++
//            if (question >= 10) setScore() else gameOn()
//        }
//
//        quizOption3.setOnClickListener {
//            timer.cancel()
//            if (checkOption(choice3)) score++
//            if (question >= 10) setScore() else gameOn()
//        }
//
//        quizOption4.setOnClickListener {
//            timer.cancel()
//            if (checkOption(choice4)) score++
//            if (question >= 10) setScore() else gameOn()
//        }
    }
}