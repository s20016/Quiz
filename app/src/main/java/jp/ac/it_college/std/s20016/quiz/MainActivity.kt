package jp.ac.it_college.std.s20016.quiz

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import jp.ac.it_college.std.s20016.quiz.data.ApiDataItem
import jp.ac.it_college.std.s20016.quiz.data.ApiInterface
import jp.ac.it_college.std.s20016.quiz.data.ApiVersionItem
import jp.ac.it_college.std.s20016.quiz.databinding.ActivityMainBinding
import jp.ac.it_college.std.s20016.quiz.helper.DBHandler
import kotlinx.coroutines.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val baseURL = "https://script.google.com/macros/s/AKfycbznWpk2m8q6lbLWSS6qaz3uS6j3L4zPwv7CqDEiC433YOgAdaFekGJmjoAO60quMg6l/"
    private val dbHelper = DBHandler(this)
    private var infoDialog: AlertDialog? = null
    private var start = false
    private var positionValue: Int = 10
    var latestVersion = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        start = checkDatabase()

        getApiVersion()
        onRefresh()
        numberPicker()

        showAppInfoDialog()
        binding.btnInfo.setOnClickListener {
            infoDialog?.show()
        }

        binding.startButton.isEnabled = true
        binding.startButton.setOnClickListener {
            if (start) {
                Intent (this, QuizActivity::class.java).also {
                    it.putExtra("POSITION", positionValue.toString())
                    startActivity(it)
                }
            } else {
                val msg = "Swipe down to fetch data!"
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // NumberPicker Settings
    private fun numberPicker() {
        val numberPicker = binding.numberPicker
        numberPicker.minValue = 10
        numberPicker.maxValue = 30
        numberPicker.wrapSelectorWheel = true
        numberPicker.setOnValueChangedListener { _, _, newVal ->
            positionValue = newVal
        }
    }

    // Swipe down to update data
    private fun onRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = checkVersion()
        }
    }

    private fun showAppInfoDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Android Class: Quiz App")
        dialogBuilder.setMessage("""
            Next Target Features: 
            - Improved UI/UX
            - Database: High Score
            - Share to SM Function
            - Animations
        """.trimIndent())
        dialogBuilder.setPositiveButton(null) { _: DialogInterface, _: Int -> }
        dialogBuilder.setNegativeButton("OK") { _: DialogInterface, _: Int -> }
        infoDialog = dialogBuilder.create()
    }

    // Checks if DB table != empty
    private fun checkDatabase(): Boolean {
        val data = dbHelper.readDataId()
        return (data.size != 0)
    }

    // Update API (Version and Questions)
    private fun updateAPI() {
        runBlocking {
            launch {
                val msg = "API Updated!"
                deleteData()
                insertVersion(latestVersion)
                getApiData(latestVersion)
                showAppInfoDialog()
                delay(3000L)
                Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
                start = true
            }
        }
    }

    // Checks if a new update is available
    private fun checkVersion(): Boolean {
        binding.startButton.isEnabled = false
        binding.btnInfo.isEnabled = false

        if (checkDatabase()) {
            val curVersion = dbHelper.readVersion()
            val newVersion = latestVersion
            Log.d("MainAct", "CurVer: $curVersion, NewVer: $newVersion")
            if (curVersion != newVersion) {
                val msg = "Update Available!"
                updateAPI()
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
            } else {
                val msg = "Up to date!"
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
            }
        } else updateAPI()

        binding.startButton.isEnabled = true
        binding.btnInfo.isEnabled= true

        start = true
        return false
    }

    private fun insertVersion(version: String) {
        val db = dbHelper.writableDatabase
        val dataVersion = """
            INSERT INTO ApiData (Version) VALUES ("$version")
        """.trimIndent()
        val insert = db.compileStatement(dataVersion)
        insert.executeInsert()
    }

    private fun deleteData() {
        val db = dbHelper.writableDatabase
        val dataValueDelete = "DELETE FROM ApiData"
        val delete = db.compileStatement(dataValueDelete)
        delete.executeInsert()
    }

    // Database
    private fun insertData(
        version: String, id: Int, question: String, answer: Int,
        choice0: String, choice1: String, choice2: String,
        choice3: String, choice4: String, choice5: String,
    ) {
        val db = dbHelper.writableDatabase
        val dataValue = """
            INSERT INTO ApiData VALUES
            ("$version", $id, "$question", $answer, "$choice0", "$choice1", "$choice2", "$choice3", "$choice4", "$choice5")
        """.trimIndent()
        val insert = db.compileStatement(dataValue)
        insert.executeInsert()
    }

    private fun getApiVersion() {
        val retBuilder = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retData = retBuilder.getVersion()

        retData.enqueue(object: Callback<ApiVersionItem?> {
            override fun onResponse(
                call: Call<ApiVersionItem?>,
                response: Response<ApiVersionItem?>
            ) {
                val resBody = response.body()!!
                val dataObj = resBody.version.toString()

                latestVersion = dataObj
            }

            override fun onFailure(call: Call<ApiVersionItem?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun getApiData(updatedVersion: String) {
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

                    for (i in dataObj.choices) {
                        if (i != "") {
                            updatedChoices.add(i)
                        } else {
                            updatedChoices.add("NULL")
                        }
                    }

                    insertData(
                        updatedVersion, dataObj.id, dataObj.question, dataObj.answers,
                        updatedChoices[0], updatedChoices[1], updatedChoices[2],
                        updatedChoices[3], updatedChoices[4], updatedChoices[5]
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