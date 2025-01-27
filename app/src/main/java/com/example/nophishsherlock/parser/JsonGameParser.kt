package com.example.nophishsherlock.parser

import android.content.Context
import android.util.Log
import com.example.nophishsherlock.data.GameData
import com.example.nophishsherlock.data.JsonGameData
import org.json.JSONObject

/**
 * Diese Klasse verarbeitet den gebenen String bei der Funktion parse() in eine Liste mit JsonGameData Objekten
 * Diese können dann von den anderen Klassen verwendet werden
 */

class JsonGameParser {

    /**
     * Diese Funktion parst den übergebenen String und gibt eine Liste mit JsonGameData Objekten zurück
     */
    fun parse(context: Context, jsonText: String): List<JsonGameData> {
        return try {
            val jsonString = context.assets.open(jsonText).bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            val dataList = mutableListOf<JsonGameData>()
            val textArray = jsonObject.getJSONArray("game")
            for (i in 0 until textArray.length()) {
                val textObject = textArray.getJSONObject(i)

                val title = textObject.getString("title")
                val taskDescription = textObject.getString("taskDescription")

                val game = if (textObject.has("game")) {
                    val gameObject = textObject.getJSONObject("game")
                    Log.d("GameJsonDataParser", "Parsed game data: ${gameObject.getString("type")}")

                    GameData(
                        type = gameObject.getString("type"),
                        content = gameObject.getJSONArray("content")
                    )
                } else {
                    null
                }
                dataList.add(JsonGameData(title, taskDescription, game))
                Log.d("GameJsonDataParser", "Parsed data: $dataList")
            }

            dataList

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }


    }

}