package com.example.quiz

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.quiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val start = findViewById<Button>(R.id.startButton)

        start.setOnClickListener {
            Intent (this, QuizActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    fun openGithub(view: View) {
        val openInBrowser = Intent(Intent.ACTION_VIEW)
        openInBrowser.data = Uri.parse("https://github.com/s20016/Quiz")
        startActivity(openInBrowser)
    }
}
