package com.example.nophishsherlock.parser

import android.content.Context
import android.util.Log
import com.example.nophishsherlock.data.ImageText
import com.example.nophishsherlock.data.JsonTextData
import com.example.nophishsherlock.data.MediaData
import org.json.JSONArray
import org.json.JSONObject


/**
 * Diese Klasse verarbeitet den gebenen String bei der Funktion parse() in eine Liste mit JsonTextData Objekten
 * Diese können dann von den anderen Klassen verwendet werden
 */
class JsonTextParser {

    /**
     * Diese Funktion parst den übergebenen String und gibt eine Liste mit JsonTextData Objekten zurück
     */
    fun parse(context: Context, jsonText: String): List<JsonTextData> {
        return try {
            val jsonString = context.assets.open(jsonText).bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            val dataList = mutableListOf<JsonTextData>()
            val textArray = jsonObject.getJSONArray("texts")

            for (i in 0 until textArray.length()) {

                val textObject = textArray.getJSONObject(i)
                val section =
                    if (textObject.has("section")) textObject.getString("section") else null
                val title = if (textObject.has("title")) textObject.getString("title") else null


                //Absätze einlesen
                val paragraphList = textObject.optJSONArray("paragraphs")?.let { getParagraphs(it) }

                //Medien einlesen
                val media = if (textObject.has("media")) {
                    val mediaObject = textObject.getJSONObject("media")
                    MediaData(
                        type = mediaObject.getString("type"),
                        description = if (mediaObject.has("description")) mediaObject.getString("description") else null,
                        source = mediaObject.getString("source")
                    )
                } else {
                    null
                }

                val imageText = if (textObject.has("image text")) {
                    val imagetextObject = textObject.getJSONObject("image text")
                    ImageText(
                        text = getParagraphs(imagetextObject.getJSONArray("text")), //getParagraphString(imagetextObject.getJSONArray("text")),
                        imageSource = imagetextObject.getString("image_source"),
                        imageFirst = imagetextObject.getBoolean("image_first")
                    )
                } else {
                    null
                }

                val paragraphWithMedia = if (textObject.has("paragraphWithMedia")) {
                    val paragraphWithMediaObject = textObject.getJSONArray("paragraphWithMedia")
                    getParagraphWithMedia(paragraphWithMediaObject)
                } else {
                    null
                }

                dataList.add(JsonTextData(section, title, paragraphList, media, imageText))

            }
            for (data in dataList) {
                Log.d("JsonTextParser", "data: $data")
            }
            dataList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun getParagraphWithMedia(paragraphWithMediaObject: JSONArray): List<JsonTextData> {
        return emptyList()
    }

    private fun getParagraphs(jsonArray: JSONArray): MutableList<String> {

        val output = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            output.add(jsonArray.getString(i))
        }
        return output

    }

    private fun getParagraphString(jsonArray: JSONArray): String {
        var output = ""
        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getString(i).get(0) == '•') {
                output += "\t"
            }

            output += jsonArray.getString(i)

            if (i < jsonArray.length() - 1) {
                output += "<br/>"
            }
        }
        Log.d("JsonTextParser", "String: $output")
        return output
    }


}