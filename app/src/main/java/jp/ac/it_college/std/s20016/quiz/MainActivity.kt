package jp.ac.it_college.std.s20016.quiz

import android.app.Activity
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val baseURL = "https://script.google.com/macros/s/AKfycbznWpk2m8q6lbLWSS6qaz3uS6j3L4zPwv7CqDEiC433YOgAdaFekGJmjoAO60quMg6l/"
    private var start = false

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onRefresh()
        getCurrentVersion()
        start = checkQuestions()

        // Spinner
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
                    startActivity(it)
                }
            }
        }

    }

    // Spinner Interface
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d("TEST", "onItemSelected: $position ")
        val intent = Intent(this, QuizActivity::class.java)
        intent.putExtra("SPINNER_POSITION", position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d("TEST", "onNothingSelected: None Selected")
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
                getApiVersion()
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

    // Get API Current Version
    private fun getCurrentVersion() {
        val verPref = getSharedPreferences("ApiVersionPref", Context.MODE_PRIVATE)
        val hasVerPref = verPref.contains("ApiVersion")

        if (hasVerPref) {
            val currentVersion = verPref.getString("ApiVersion", "")
            val initialVersion = "v.${currentVersion} \nSwipe down to refresh!"
            binding.tvVersion.text = initialVersion
        } else {
            val initialVersion = "Swipe down to refresh!"
            binding.tvVersion.text = initialVersion
        }
    }

    // Check if APIQuestion is available
    private fun checkQuestions(): Boolean {
        val ret: Boolean
        val dataPref = getSharedPreferences("ApiDataPref", Context.MODE_PRIVATE)
        val dataQuestionSize = dataPref.all.size

        ret = dataQuestionSize > 0
        return ret
    }


    // Get API Latest Version
    private fun getApiVersion() {
        val verPref = getSharedPreferences("ApiVersionPref", Context.MODE_PRIVATE)
        val sharedEdit = verPref.edit()
        val retBuilder = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
        val retVersion = retBuilder.getVersion()

        retVersion.enqueue(object : Callback<ApiVersionItem?> {
            override fun onResponse(
                call: Call<ApiVersionItem?>,
                response: Response<ApiVersionItem?>
            ) {
                try {
                    var newVer = ""

                    runBlocking {
                        val resBody = async { response.body()!!.version }
                        newVer = resBody.await().toString()
                    }

                    val resultVer = "v.${newVer}"

                    sharedEdit.putString("ApiVersion", newVer).apply()
                    binding.tvVersion.text = resultVer

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("TEST", "Error")
                } finally {
                    response.errorBody()?.close()
                }
            }
            override fun onFailure(call: Call<ApiVersionItem?>, t: Throwable) {
                Log.d("TEST", "Error")
            }
        })
    }


    // Get API Data
    private fun getApiData() {
        val dataPref = getSharedPreferences("ApiDataPref", Context.MODE_PRIVATE)
        val sharedEdit = dataPref.edit()
        val retBuilder = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retData = retBuilder.getData()

        retData.enqueue(object : Callback<List<ApiDataItem>?> {
            override fun onResponse(
                call: Call<List<ApiDataItem>?>,
                response: Response<List<ApiDataItem>?>
            ) {
                try {
                    var dataApiId: String
                    val resBody = response.body()!!
                    val gson = Gson()
                    for (data in resBody) {
                        dataApiId = data.id.toString()
                        val dataObj = ApiDataItem(
                            id = data.id,
                            question = data.question,
                            answers = data.answers,
                            choices = data.choices
                        )
                        val jsonData = gson.toJson(dataObj)
                        sharedEdit.putString(dataApiId, jsonData).apply()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("TEST", "Error")
                } finally {
                    response.errorBody()?.close()
                }
            }

            override fun onFailure(call: Call<List<ApiDataItem>?>, t: Throwable) {
                Log.d("TEST", "Error Loading")
            }
        })
    }
}