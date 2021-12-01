package jp.ac.it_college.std.s20016.quiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import jp.ac.it_college.std.s20016.quiz.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val score = intent.getStringExtra("SCORE")
        val message = intent.getStringExtra("MESSAGE")

        binding.resultMessage.text = message
        binding.resultScore.text = score

        binding.resultHome.setOnClickListener {
            Intent(this, MainActivity::class.java).also { startActivity(it) }
        }

        binding.resultRestart.setOnClickListener {
            Intent(this, QuizActivity::class.java).also { startActivity(it) }
        }
    }
}