package jp.ac.it_college.std.s20016.quiz

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.Gson
import jp.ac.it_college.std.s20016.quiz.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val baseURL = "https://script.google.com/macros/s/AKfycbznWpk2m8q6lbLWSS6qaz3uS6j3L4zPwv7CqDEiC433YOgAdaFekGJmjoAO60quMg6l/"
    private val dbHelper = DBHandler(this)
    private var start = false
    private var positionSpinner: Int = 0
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onRefresh()
        start = checkDatabase()


        ArrayAdapter.createFromResource(
            this, R.array.itemCounts, R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown)
            binding.itemCountSpinner.adapter = adapter
        }

        binding.itemCountSpinner.onItemSelectedListener = this
        binding.startButton.isEnabled = true
        binding.startButton.setOnClickListener {
            if (start) {
                Intent (this, QuizActivity::class.java).also {
                    it.putExtra("POSITION", positionSpinner.toString())
                    startActivity(it)
                }
            } else {
                val msg = "Swipe down to fetch data!"
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Spinner Interface
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        positionSpinner = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        positionSpinner = 0
    }

    // Swipe down to update data
    private fun onRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = updateAPI()
        }
    }

    // Update API (Version and Questions)
    private fun updateAPI(): Boolean  {
        binding.startButton.isEnabled = false
        runBlocking {
            launch {
                val msg = "API Updated!"
                getApiData()
                delay(2500L)
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                start = true
            }
        }
        binding.startButton.isEnabled = true
        start = true
        return false
    }

    private fun checkDatabase(): Boolean {
        val data = dbHelper.readData()
        return (data.size != 0)
    }

    // Database
    private fun insertData(
        id: Int,
        question: String,
        answer: Int,
        choice0: String,
        choice1: String,
        choice2: String,
        choice3: String,
        choice4: String,
        choice5: String,
    ) {
        val db = dbHelper.writableDatabase
        val dataValue = """
            INSERT INTO ApiData (Id, Question, Answer, Choice0, Choice1, Choice2, Choice3, Choice4, Choice5)
            VALUES ($id, "$question", $answer, "$choice0", "$choice1", "$choice2", "$choice3", "$choice4", "$choice5")
        """.trimIndent()
        val insert = db.compileStatement(dataValue)
        insert.executeInsert()
    }

    private fun deleteData() {
        val db = dbHelper.writableDatabase
        val dataValueDelete = "DELETE FROM ApiData"
        val delete = db.compileStatement(dataValueDelete)
        delete.executeInsert()

    }

    private fun getApiData() {
        deleteData()
        val retBuilder = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retData = retBuilder.getData()
        val updatedChoices = mutableListOf<String>()

        retData.enqueue(object: Callback<List<ApiDataItem>> {
            override fun onResponse(
                call: Call<List<ApiDataItem>>,
                response: Response<List<ApiDataItem>>
            ) {
                val resBody = response.body()!!
                for (data in resBody) {
                    val dataObj = ApiDataItem(
                        id = data.id,
                        question = data.question,
                        answers = data.answers,
                        choices = data.choices
                    )

                    Log.d("TEST", data.id.toString())

                    for (i in dataObj.choices) {
                        if (i != "") {
                            updatedChoices.add(i)
                        } else {
                            updatedChoices.add("NULL")
                        }
                    }

                    insertData(
                        dataObj.id,
                        dataObj.question,
                        dataObj.answers,
                        updatedChoices[0],
                        updatedChoices[1],
                        updatedChoices[2],
                        updatedChoices[3],
                        updatedChoices[4],
                        updatedChoices[5]
                    )

                   updatedChoices.clear()
                }
            }

            override fun onFailure(call: Call<List<ApiDataItem>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}