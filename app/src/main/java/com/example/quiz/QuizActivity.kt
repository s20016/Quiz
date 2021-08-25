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
    private val dataQuestion = mutableListOf<String>()
    private val dataImage = mutableListOf<String>()
    private val dataOption1 = mutableListOf<String>()
    private val dataOption2 = mutableListOf<String>()
    private val dataOption3 = mutableListOf<String>()
    private val dataOption4 = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        supportActionBar?.hide()

        // TODO: Get data from CSV
        val data = BufferedReader(InputStreamReader(assets.open("s20016.csv")))
        var line: String?

        while (data.readLine().also { line = it } != null) {
            val row: MutableList<String> = line!!.split(",") as MutableList<String>

            dataQuestion.add(row[0]); dataImage.add(row[1])
            dataOption1.add(row[2]); dataOption2.add(row[3])
            dataOption3.add(row[4]); dataOption4.add(row[5])
        }

        // Shuffle

        gameOn()
    }

    private fun getResult(score: Int): Pair<String, String> {
        // (RESULT) Message and Score(Points)
        val points = "$score/10"
        val message: String = when (score) {
            in 8..10 -> "Great Job!"
            in 5..7 -> "Fantastic!"
            in 4..6 -> "Nice!"
            else -> "Oh no!"
        }; return Pair(points, message)
    }

    private fun setScore() {
        val (points, message) = getResult(score)
        val intent = Intent(this, ResultActivity::class.java)

        intent.putExtra("SCORE", points)
        intent.putExtra("MESSAGE", message)
        startActivity(intent)
    }

    private fun gameOn() {
        question++

        // QuizActivity ID
        val questionNumber = question
        val quizQuestion = findViewById<TextView>(R.id.quizQuestion)
        val quizImage = findViewById<ImageView>(R.id.quizBG)
        val quizOption1 = findViewById<TextView>(R.id.quizOption1)
        val quizOption2 = findViewById<TextView>(R.id.quizOption2)
        val quizOption3 = findViewById<TextView>(R.id.quizOption3)
        val quizOption4 = findViewById<TextView>(R.id.quizOption4)
        val quizScore = findViewById<TextView>(R.id.quizScore)
        val quizTimer = findViewById<TextView>(R.id.quizTimer)

        // (MAIN) Question and Options
        quizQuestion.text = dataQuestion[questionNumber]
        quizOption1.text = dataOption1[questionNumber]
        quizOption2.text = dataOption2[questionNumber]
        quizOption3.text = dataOption3[questionNumber]
        quizOption4.text = dataOption4[questionNumber]

        // (MAIN) Score and Timer
        val scoreDisplay = "SCORE: $score"
        quizScore.text = scoreDisplay

        // TODO: Setting answer to Option1
        quizOption1.setOnClickListener { score++; if (question >= 10) setScore() else gameOn() }
        quizOption2.setOnClickListener { if (question >= 10) setScore() else gameOn() }
        quizOption3.setOnClickListener { if (question >= 10) setScore() else gameOn() }
        quizOption4.setOnClickListener { if (question >= 10) setScore() else gameOn() }
    }
}