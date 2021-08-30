package com.example.quiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader

class QuizActivity : AppCompatActivity() {

    private var question = 0; private var score = 0;
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

    private fun getResult(score: Int): Pair<String, String> {
        // (RESULT) Message and Score(Points)
        val points = "$score/10"
        val message: String = when (score) {
            in 8..10 -> "Great Job!"
            in 5..7 -> "Fantastic!"
            in 4..6 -> "Nice!"
            else -> "Try Again!"
        }; return Pair(points, message)
    }

    private fun setScore() {
        val (points, message) = getResult(score)
        val intent = Intent(this, ResultActivity::class.java)

        intent.putExtra("SCORE", points)
        intent.putExtra("MESSAGE", message)
        startActivity(intent)
    }

    private fun checkOption(choice: Int): Boolean {
        return (choice == 2)
    }

    private fun checkImage(imageSource: String): Int {
        return if (imageSource != "") {
            resources.getIdentifier(imageSource, "drawable", applicationInfo.name)
        } else resources.getIdentifier("quiz_qbg", "drawable", applicationInfo.name)
    }

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

        // (MAIN) Question and Options
        val choices = mutableListOf(2, 3, 4, 5); choices.shuffle()
        val choice1 = choices[0]
        val choice2 = choices[1]
        val choice3 = choices[2]
        val choice4 = choices[3]

//        Log.d("TAG", dataQuestion[questionNumber][1].toString())

        val imageData = dataQuestion[questionNumber][1]
            .replace(".png", "")
            .replace(".xml", "")

        val questionImage = resources.getIdentifier(imageData, "drawable", packageName)

        quizImage.setImageResource(questionImage)
        quizQuestion.text = dataQuestion[questionNumber][0]
        quizOption1.text = dataQuestion[questionNumber][choice1]
        quizOption2.text = dataQuestion[questionNumber][choice2]
        quizOption3.text = dataQuestion[questionNumber][choice3]
        quizOption4.text = dataQuestion[questionNumber][choice4]

        // (MAIN) Score
        val scoreDisplay = "SCORE: $score"
        quizScore.text = scoreDisplay

        // Setting answer to Random
        quizOption1.setOnClickListener {
            if (checkOption(choice1)) score++
            if (question >= 10) setScore() else gameOn()
        }

        quizOption2.setOnClickListener {
            if (checkOption(choice2)) score++
            if (question >= 10) setScore() else gameOn()
        }

        quizOption3.setOnClickListener {
            if (checkOption(choice3)) score++
            if (question >= 10) setScore() else gameOn()
        }

        quizOption4.setOnClickListener {
            if (checkOption(choice4)) score++
            if (question >= 10) setScore() else gameOn()
        }
    }
}
