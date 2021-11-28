package jp.ac.it_college.std.s20016.quiz

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
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

    // User variables
//    val userChoicesInt = mutableListOf<Int>()

    private lateinit var binding: ActivityQuizBinding
    private var layoutManager: RecyclerView.LayoutManager? = null
//    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

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
        val q4 = ceil((apiQuestionSize.toDouble() * 1 / 4) * 3).toInt()
        val q3 = ceil((apiQuestionSize.toDouble() * 1 / 4) * 2).toInt()
        val q2 = ceil(apiQuestionSize.toDouble() * 1 / 4).toInt()
        val q1 = ceil(apiQuestionSize.toDouble() * 1 / 8).toInt()
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

    // Check if user choice is correct
    private fun checkChoice(
        userChoices: MutableList<Int>, choiceAnswers: MutableList<Int>): Boolean {
        val user = userChoices.sort()
        val data = choiceAnswers.sort()
        Log.d("CheckChoice: ", "$user, $data")
        return (user == data)
    }

    // TODO: RecycleView
    private fun initiateRVChoices(
        questionChoices: List<String>, questionAnswers: Int): MutableList<Int> {
        var userChoiceInt = mutableListOf<Int>()

        layoutManager = LinearLayoutManager(this)
        binding.rvChoiceItems.layoutManager = layoutManager

        val adapter = RecyclerAdapter(questionChoices, questionAnswers)
        binding.rvChoiceItems.adapter = adapter
        adapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener {
            override fun onItemClick(userChoice: MutableList<Int>) {
                userChoiceInt = userChoice
//                if (position !in userChoicesInt) userChoicesInt.add(position)
//                if (position ) userChoicesInt.clear()
                Log.d("onItemClick: ", userChoiceInt.toString())
            }
        })
        return userChoiceInt
    }


    // Main Game
    private fun gameOn() {
        question++

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
                if (question >= apiQuestionSize) setScore() else gameOn()
            }
        }; timer.start()

        // Question and Choices Variables
        val questionNumber = question - 1
        val questionQuestion = dataQuestion[questionNumber]
        val questionChoices = dataChoices[questionNumber].filter { x: String? -> x != "" } // [インターネット経由の配信, 市場投入までの時間, セキュリティ, ハードウェアに依存しない]
        val questionAnswers = dataAnswers[questionNumber]

//        val mapQC = mutableMapOf<Int, String>() // {0=インターネット経由の配信, 1=市場投入までの時間, 2=セキュリティ, 3=ハードウェアに依存しない}
//        val x = questionChoices.forEachIndexed { i, v -> mapQC[i] = v }
//
//        val shuffledMapQC = mapQC.values.shuffled() // [市場投入までの時間, セキュリティ, インターネット経由の配信, ハードウェアに依存しない]

        val realAnswers = questionChoices.take(questionAnswers) // [インターネット経由の配信]
        val shuffledChoices = questionChoices.shuffled() // [市場投入までの時間, セキュリティ, インターネット経由の配信, ハードウェアに依存しない]
        val shuffledAnswers = mutableListOf<Int>()

        shuffledChoices.forEachIndexed { index, s ->
            if (s in realAnswers) shuffledAnswers.add(index)
        }

//        Log.d("GameON: ", questionChoices.toString())
//        Log.d("GameON: ", realAnswers.toString())
//        Log.d("GameON: ", shuffledChoices.toString())
        Log.d("GameON: ", shuffledAnswers.toString()) // [3, 4]


        // Assigning data to each ID
        binding.quizQuestion.text = questionQuestion

        // RecycleView
        val userChoices = initiateRVChoices(shuffledChoices, questionAnswers)

        // Score
        val scoreDisplay =
            "SCORE: $score  |  Answer(s): $questionAnswers  |  Q: $question/$apiQuestionSize"
        binding.quizScore.text = scoreDisplay

//        Log.d("TEST question: ", question.toString())
//        Log.d("TEST question: ", dataQuestion[questionNumber])

        binding.nextButton.setOnClickListener {
            timer.cancel()
            if (checkChoice(userChoices, shuffledAnswers)) score++
            Log.d("GAMEON: ", checkChoice(userChoices, shuffledAnswers).toString())
            if (question >= apiQuestionSize) setScore() else gameOn()
        }
    }
}