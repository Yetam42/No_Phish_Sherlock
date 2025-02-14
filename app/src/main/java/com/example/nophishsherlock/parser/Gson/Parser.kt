package com.example.nophishsherlock.parser.Gson

import android.content.Context
import com.example.nophishsherlock.data.JsonTextData
import com.google.gson.Gson
import java.io.IOException

class Parser {

    private val gson = Gson()

    fun parse(context: Context, jsonFileName: String): List<JsonTextData> {
        return try {
            val jsonString =
                context.assets.open(jsonFileName).bufferedReader().use { it.readText() }

            // Parse the JSON object that contains the "texts" array
            val jsonObject = gson.fromJson(jsonString, JsonWrapper::class.java)

            jsonObject.texts ?: emptyList()
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Wrapper class for JSON structure
    private data class JsonWrapper(
        val texts: List<JsonTextData>?
    )
}