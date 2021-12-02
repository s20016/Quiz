package jp.ac.it_college.std.s20016.quiz

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
              Id INTEGER PRIMARY KEY NOT NULL,
              Question TEXT NOT NULL,
              Answer INTEGER NOT NULL,
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


    fun readDataId(): MutableList<String> {
        val idList: MutableList<String> = ArrayList()
        val db = this.readableDatabase
        val dataIdRead = "SELECT Id FROM ApiData"
        val result = db.rawQuery(dataIdRead, null)
        if (result.moveToNext()) {
            do {
                val id = result.getString(result.getColumnIndex("Id").toInt())
                idList.add(id)
            } while (result.moveToNext())
            result.close()
        }
        return idList
    }

    @SuppressLint("Recycle")
    fun readData(): MutableList<String> {
        val apiList: MutableList<String> = ArrayList()
        val db = this.readableDatabase
        val dataValueRead = "SELECT * FROM ApiData"
        val result = db.rawQuery(dataValueRead, null)
        if (result.moveToFirst()) {
            do {
                val id = result.getString(result.getColumnIndex("Id").toInt())
                val question = result.getString(result.getColumnIndex("Question").toInt())
                val answer = result.getString(result.getColumnIndex("Answer").toInt())
                val choice0 = result.getString(result.getColumnIndex("Choice0").toInt())
                val choice1 = result.getString(result.getColumnIndex("Choice1").toInt())
                val choice2 = result.getString(result.getColumnIndex("Choice2").toInt())
                val choice3 = result.getString(result.getColumnIndex("Choice3").toInt())
                val choice4 = result.getString(result.getColumnIndex("Choice4").toInt())
                val choice5 = result.getString(result.getColumnIndex("Choice5").toInt())
                val data = listOf<String>(
                    id, question, answer, choice0, choice1, choice2, choice3, choice4, choice5
                )
                apiList.add(data.toString())
            } while (result.moveToNext())
            result.close()
        }
        return apiList
    }
}
