package com.example.quiz

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.quiz.databinding.ActivityMainBinding
import java.net.URI

class MainActivity : AppCompatActivity() {
   private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Get data from JSON
        val data = getSampleData(resources)
        // Log.d("JSON", data.toString())

        // TODO: activity_main (Layout)
        val start = findViewById<TextView>(R.id.startButton)

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
