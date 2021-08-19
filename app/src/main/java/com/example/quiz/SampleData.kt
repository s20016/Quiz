package com.example.quiz

import android.content.res.Resources
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

@Serializable
data class SampleData(
   val question: String,
   val option1: String,
   val option2: String,
   val option3: String,
   val option4: String,
)

fun getSampleData(resources: Resources): List<SampleData> {
   val assetManager = resources.assets
   val inputStream = assetManager.open("sample.json")
   val bufferedReader = BufferedReader(InputStreamReader(inputStream))
   val str: String = bufferedReader.readText()
   val obj = Json.decodeFromString<List<SampleData>>(str)
   return obj
}