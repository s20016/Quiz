package jp.ac.it_college.std.s20016.quiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        supportActionBar?.hide()

        val resultMessage = findViewById<TextView>(R.id.resultMessage)
        val resultScore= findViewById<TextView>(R.id.resultScore)
        val resultHome = findViewById<Button>(R.id.resultHome)
        val resultRestart = findViewById<Button>(R.id.resultRestart)

        val score = intent.getStringExtra("SCORE")
        val message = intent.getStringExtra("MESSAGE")

        resultMessage.text = message
        resultScore.text = score

        resultHome.setOnClickListener {
            Intent(this, MainActivity::class.java).also { startActivity(it) }
        }

        resultRestart.setOnClickListener {
            Intent(this, QuizActivity::class.java).also { startActivity(it) }
        }
    }
}