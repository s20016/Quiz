package jp.ac.it_college.std.s20016.quiz.helper

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHandler(context: Context): SQLiteOpenHelper(context, DBNAME, null, 1) {
    companion object {
        private const val DBNAME = "APIQuestion"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE ApiData(
              Version TEXT,
              Id INTEGER PRIMARY KEY,
              Question TEXT,
              Answer INTEGER,
              Choice0 BLOB,
              Choice1 BLOB,
              Choice2 BLOB,
              Choice3 BLOB,
              Choice4 BLOB,
              Choice5 BLOB
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    @SuppressLint("Recycle", "Range")
    fun readVersion(): String {
        var dataVersion = String()
        val db = this.readableDatabase
        val dataVerRead = "SELECT Version FROM ApiData"
        val result = db.rawQuery(dataVerRead, null)
        if (result != null && result.moveToFirst()) {
            do {
                val ver = result.getString(result.getColumnIndex("Version"))
                dataVersion = ver.toString()
            } while (result.moveToNext())
            result.close()
        }; return dataVersion
    }

    @SuppressLint("Range")
    fun readDataId(): MutableList<String> {
        val idList: MutableList<String> = ArrayList()
        val db = this.readableDatabase
        val dataIdRead = "SELECT Id FROM ApiData"
        val result = db.rawQuery(dataIdRead, null)
        if (result != null && result.moveToFirst()) {
            do {
                val id = result.getString(result.getColumnIndex("Id"))
                idList.add(id)
            } while (result.moveToNext())
            result.close()
        }; return idList
    }

    @SuppressLint("Range")
    fun readValues(id: String): MutableList<String> {
        val idValues = mutableListOf<String>()
        val db = this.readableDatabase
        val dataValueRead = "SELECT * FROM ApiData WHERE Id=$id"
        val result = db.rawQuery(dataValueRead, null)
        if (result != null && result.moveToFirst()) {
            do {
                val question = result.getString(result.getColumnIndex("Question"))
                val answer = result.getString(result.getColumnIndex("Answer"))
                val choice0 = result.getString(result.getColumnIndex("Choice0"))
                val choice1 = result.getString(result.getColumnIndex("Choice1"))
                val choice2 = result.getString(result.getColumnIndex("Choice2"))
                val choice3 = result.getString(result.getColumnIndex("Choice3"))
                val choice4 = result.getString(result.getColumnIndex("Choice4"))
                val choice5 = result.getString(result.getColumnIndex("Choice5"))
                val data = listOf<String>(
                    id, question, answer, choice0, choice1, choice2, choice3, choice4, choice5
                )
                idValues.add(data.toString())
            } while (result.moveToNext())
            result.close()
        }; return idValues
    }
}
