package com.example.nophishsherlock.parser

import android.content.Context
import com.example.nophishsherlock.data.JsonTextData
import com.example.nophishsherlock.data.MediaData
import org.json.JSONObject


/***
 * Diese Klasse verarbeitet den gebenen String bei der Funktion parse() in eine Liste mit JsonTextData Objekten
 * Diese können dann von den anderen Klassen verwendet werden
 */
class JsonTextParser {

    /**
     * Diese Funktion parst den übergebenen String und gibt eine Liste mit JsonTextData Objekten zurück
     */
    fun parse(context : Context, jsonText: String): List<JsonTextData> {
        return try {
            val jsonString = context.assets.open(jsonText).bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            val dataList = mutableListOf<JsonTextData>()
            val textArray = jsonObject.getJSONArray("texts")

            for (i in 0 until textArray.length()) {

                val textObject = textArray.getJSONObject(i)
                val section = if (textObject.has("section")) textObject.getString("section") else null
                val title = textObject.getString("title")

                //Absätze einlesen
                val paragraphArray = textObject.getJSONArray("paragraphs")
                val paragraphList = mutableListOf<String>()
                for (j in 0 until paragraphArray.length()) {
                    paragraphList.add(paragraphArray.getString(j))
                }

                //Medien einlesen
                val media = if (textObject.has("media")) {
                    val mediaObject = textObject.getJSONObject("media")
                    MediaData(
                        type = mediaObject.getString("type"),
                        description = mediaObject.getString("description"),
                        source = mediaObject.getString("source")
                    )
                }
                else {
                    null
                }

                dataList.add(JsonTextData(section, title, paragraphList, media))

            }

            dataList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}