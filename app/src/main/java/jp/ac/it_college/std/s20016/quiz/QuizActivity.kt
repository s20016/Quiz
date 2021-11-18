package jp.ac.it_college.std.s20016.quiz

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader

class QuizActivity : AppCompatActivity() {

    private var question = 0; private var score = 0
    private val dataQuestion = mutableListOf<List<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        supportActionBar?.hide()

        // TODO: Get data from CSV
        val csv = "s20016.csv"
        val data = BufferedReader(InputStreamReader(assets.open(csv)))
        var line: String?

        while (data.readLine().also { line = it } != null) {
            val row: MutableList<String> = line!!.split(",") as MutableList<String>
            dataQuestion.add(row)
        }

        dataQuestion.removeAt(0)
        dataQuestion.shuffle()

        gameOn()
    }

    // Message and score(points)
    private fun getResult(score: Int): Pair<String, String> {
        val points = "$score/10"
        val message: String = when (score) {
            in 8..10 -> "Great Job!"
            in 5..7 -> "Fantastic!"
            in 4..6 -> "Nice!"
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

    // Check if user option is correct
    private fun checkOption(choice: Int): Boolean {
        return (choice == 2)
    }

    // Main Game
    private fun gameOn() {
        question++

        // QuizActivity ID
        val questionNumber = question - 1
        val quizQuestion = findViewById<TextView>(R.id.quizQuestion)
        val quizImage = findViewById<ImageView>(R.id.quizBG)
        val quizOption1 = findViewById<TextView>(R.id.quizOption1)
        val quizOption2 = findViewById<TextView>(R.id.quizOption2)
        val quizOption3 = findViewById<TextView>(R.id.quizOption3)
        val quizOption4 = findViewById<TextView>(R.id.quizOption4)
        val quizScore = findViewById<TextView>(R.id.quizScore)
        val quizTimer = findViewById<TextView>(R.id.quizTimer)

        // Timer
        val timer = object: CountDownTimer(11000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = (millisUntilFinished / 1000).toInt()
                val timeText = String.format("00:%02d", time)
                if (time <= 3) quizTimer.setTextColor(Color.parseColor("#FF0000"))
                if (time >= 4) quizTimer.setTextColor(Color.parseColor("#000000"))
                quizTimer.text = timeText
            }
            override fun onFinish() { if (question >= 10) setScore() else gameOn() }
        }; timer.start()

        // Question and Options
        val choices = mutableListOf(2, 3, 4, 5); choices.shuffle()
        val choice1 = choices[0]
        val choice2 = choices[1]
        val choice3 = choices[2]
        val choice4 = choices[3]

        // Image Source
        val questionImage: Int; val imageData: String
        val image = dataQuestion[questionNumber][1]

        if (image != "") {
            imageData = image.replace(".png", "").replace(".xml", "")
            questionImage = resources.getIdentifier(imageData, "drawable", packageName)
            quizQuestion.gravity = Gravity.BOTTOM
            quizQuestion.setPaddingRelative(0, 0, 0, 120)
        } else {
            questionImage = resources.getIdentifier("quiz_qbg", "drawable", packageName)
        }

        // Assigning data to each ID
        quizImage.setImageResource(questionImage)
        quizQuestion.text = dataQuestion[questionNumber][0]
        quizOption1.text = dataQuestion[questionNumber][choice1]
        quizOption2.text = dataQuestion[questionNumber][choice2]
        quizOption3.text = dataQuestion[questionNumber][choice3]
        quizOption4.text = dataQuestion[questionNumber][choice4]

        // Score
        val scoreDisplay = "SCORE: $score / 10"
        quizScore.text = scoreDisplay

        // Setting answer to Random
        quizOption1.setOnClickListener {
            timer.cancel()
            if (checkOption(choice1)) score++
            if (question >= 10) setScore() else gameOn()
        }

        quizOption2.setOnClickListener {
            timer.cancel()
            if (checkOption(choice2)) score++
            if (question >= 10) setScore() else gameOn()
        }

        quizOption3.setOnClickListener {
            timer.cancel()
            if (checkOption(choice3)) score++
            if (question >= 10) setScore() else gameOn()
        }

        quizOption4.setOnClickListener {
            timer.cancel()
            if (checkOption(choice4)) score++
            if (question >= 10) setScore() else gameOn()
        }
    }
}
