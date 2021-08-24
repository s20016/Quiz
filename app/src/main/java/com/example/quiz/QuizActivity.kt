package com.example.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader

class QuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        supportActionBar?.hide()

        // val data = getSampleData(resources)
        // Log.d("JSON", data.toString())

        // TODO: Get data from CSV
        val data = BufferedReader(InputStreamReader(assets.open("s20016.csv")))
        var line: String?; var displayData = ""
        val dataQuestion = arrayListOf<String>()
        val dataImage = arrayListOf<String>()
        val dataOption1 = arrayListOf<String>()
        val dataOption2 = arrayListOf<String>()
        val dataOption3 = arrayListOf<String>()
        val dataOption4 = arrayListOf<String>()

        while (data.readLine().also { line = it } != null) {
            val row: List<String> = line!!.split(",")
            dataQuestion.add(row[0])
            dataImage.add(row[1])
            dataOption1.add(row[2])
            dataOption2.add(row[3])
            dataOption3.add(row[4])
            dataOption4.add(row[5])
        }

        val quizQuestion = findViewById<TextView>(R.id.quizQuestion)
//        val quizImage= findViewById<TextView>(R.id.quizImage)
        val quizOption1= findViewById<TextView>(R.id.quizOption1)
        val quizOption2= findViewById<TextView>(R.id.quizOption2)
        val quizOption3= findViewById<TextView>(R.id.quizOption3)
        val quizOption4= findViewById<TextView>(R.id.quizOption4)

        val rnd = (1..10).random()

        quizQuestion.text = dataQuestion[rnd]
//        quizImage. = dataQuestion[[rnd]]
        quizOption1.text = dataOption1[rnd]
        quizOption2.text = dataOption2[rnd]
        quizOption3.text = dataOption3[rnd]
        quizOption4.text = dataOption4[rnd]

    }
}