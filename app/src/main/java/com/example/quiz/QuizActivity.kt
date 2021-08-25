package com.example.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader

class QuizActivity : AppCompatActivity() {

    private val dataQuestion = arrayListOf<String>()
    private val dataImage = arrayListOf<String>()
    private val dataOption1 = arrayListOf<String>()
    private val dataOption2 = arrayListOf<String>()
    private val dataOption3 = arrayListOf<String>()
    private val dataOption4 = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        supportActionBar?.hide()

        // val data = getSampleData(resources)
        // Log.d("JSON", data.toString())

        // TODO: Get data from CSV
        val data = BufferedReader(InputStreamReader(assets.open("s20016.csv")))

        var line: String?; var displayData = ""


        while (data.readLine().also { line = it } != null) {
            val row: List<String> = line!!.split(",")
            dataQuestion.add(row[0])
            dataImage.add(row[1])
            dataOption1.add(row[2])
            dataOption2.add(row[3])
            dataOption3.add(row[4])
            dataOption4.add(row[5])
        }

        // TODO: TEST
//        val test = findViewById<TextView>(R.id.test)
//        test.text = dataQuestion.size.toString()

        gameOn()
    }

    private var question = 0; var score = 0;
    private var questionList = (1..11).toMutableList()

    private fun gameOn() {
        question++

        // QuizActivity ID
        val questionNumber = questionList.random()
        val quizQuestion = findViewById<TextView>(R.id.quizQuestion)
        val quizImage = findViewById<ImageView>(R.id.quizBG)
        val quizOption1 = findViewById<TextView>(R.id.quizOption1)
        val quizOption2 = findViewById<TextView>(R.id.quizOption2)
        val quizOption3 = findViewById<TextView>(R.id.quizOption3)
        val quizOption4 = findViewById<TextView>(R.id.quizOption4)
        val quizScore = findViewById<TextView>(R.id.quizScore)
        val quizTimer = findViewById<TextView>(R.id.quizTimer)

        // ResultActivity ID
        val resultMessage = findViewById<TextView>(R.id.resultMessage)
        val resultScore= findViewById<TextView>(R.id.resultScore)

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
        quizOption1.setOnClickListener {
            score++
            if (question >= 9) {
                // (RESULT) Message and Score(Points)
                val points = "$score/ 10"
                val message: String = when (score) {
                    in 8..10 -> "Great Job!"
                    in 5..7 -> "Fantastic!"
                    in 4..6 -> "Nice!"
                    else -> "Oh no!"
                }

                setContentView(R.layout.activity_result)
                resultMessage.text = message
                resultScore.text = points
            } else { gameOn() }
        }

        quizOption2.setOnClickListener{ gameOn() }
        quizOption3.setOnClickListener{ gameOn() }
        quizOption4.setOnClickListener{ gameOn() }

        questionList.remove(questionNumber)

//        if (question >= 10) {
//            // (RESULT) Message and Score(Points)
//            val points = "$score/ 10"
//            val message: String = when (score) {
//                in 8..10 -> "Great Job!"
//                in 5..7 -> "Fantastic!"
//                in 4..6 -> "Nice!"
//                else -> "Oh no!"
//            }
//
//            setContentView(R.layout.activity_result)
//            resultMessage.text = message
//            resultScore.text = points
//        }
    }
}