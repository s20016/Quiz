package jp.ac.it_college.std.s20016.quiz

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import jp.ac.it_college.std.s20016.quiz.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val baseURL = "https://script.google.com/macros/s/AKfycbznWpk2m8q6lbLWSS6qaz3uS6j3L4zPwv7CqDEiC433YOgAdaFekGJmjoAO60quMg6l/"
    private var newVersion: String = ""

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataIsSet = initializeApiData()
        
        // TODO: Change color when dataIsSet is true
        binding.startButton.isEnabled = dataIsSet

        if (dataIsSet) {
            binding.startButton.setBackgroundColor(Color.parseColor("#0E3858"))
        }

        binding.startButton.setOnClickListener {
            Intent (this, QuizActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    // Initial Setup
    private fun initializeApiData(): Boolean {
        var dataComplete = false
        val verPref = getSharedPreferences("ApiVersionPref", Context.MODE_PRIVATE)
        val hasVerPref = verPref.contains("ApiVersion")
        if (hasVerPref) {
            val currentVersion = verPref.getString("ApiVersion", "")
            val newVersion = getApiVersion()
            val initialVersion = "v.${currentVersion}"
            binding.tvVersion.text = initialVersion
            if (currentVersion != newVersion) {
                getApiData()
                dataComplete = true
            }
        } else {
            getApiVersion()
            getApiData()
            dataComplete = true
        }
        return dataComplete
    }

    // Get API Version
    private fun getApiVersion(): String {
        val verPref = getSharedPreferences("ApiVersionPref", Context.MODE_PRIVATE)
        val sharedEdit = verPref.edit()
        val retBuilder = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retVersion = retBuilder.getVersion()

        retVersion.enqueue(object : Callback<ApiVersionItem?> {
            var newVersion: String  = ""
            override fun onResponse(
                call: Call<ApiVersionItem?>,
                response: Response<ApiVersionItem?>
            ) {
                try {
                    val resBody = response.body()!!
                    newVersion = resBody.version.toString()
                    val resultVer = "v.${newVersion}"

                    // Saving Api version
                    sharedEdit.putString("ApiVersion", resBody.version).apply()
                    binding.tvVersion.text = resultVer

                } catch (e: Exception) {
                    response.errorBody()?.close()
                } finally {
                    response.errorBody()?.close()
                }
            }

            override fun onFailure(call: Call<ApiVersionItem?>, t: Throwable) {
                Log.d("TEST", "onFailure: Error")
            }
        })
        return newVersion
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
                    // Saving Api data
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
                    response.errorBody()?.close()
                }
            }

            override fun onFailure(call: Call<List<ApiDataItem>?>, t: Throwable) {
                Log.d("TEST", "Error Loading")
            }
        })
    }

//    fun openGithub(view: View) {
//        val openInBrowser = Intent(Intent.ACTION_VIEW)
//        openInBrowser.data = Uri.parse("https://github.com/s20016/Quiz")
//        startActivity(openInBrowser)
//    }
}