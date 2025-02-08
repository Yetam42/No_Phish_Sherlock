package com.example.nophishsherlock

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.print.PrintAttributes.Margins
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import com.example.nophishsherlock.parser.JsonTextParser
import com.google.android.material.card.MaterialCardView
import java.io.IOException

class LevelOverviewActivity : AppCompatActivity() {

    private lateinit var elementContainer: LinearLayout

    private var shortChapterString = "#_chapter/#_chapter_short_image.json"
    private var longChapterString = "#_chapter/#_chapter_long.json"
    private var gameString = "#_chapter/#_game.json"

    private val jsonTextParser = JsonTextParser()


    private var chapterCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.level_overview_screen)

        elementContainer = findViewById(R.id.elementContainer)

        chapterCount = countDirectories(this)

        for (i in 1..chapterCount) {
            createElement(i)
        }

    }


    private fun createElement(index: Int) {
        val element = layoutInflater.inflate(R.layout.level_overview_element, null)
        element.findViewById<TextView>(R.id.chapter_number).text = "$index"
        element.findViewById<TextView>(R.id.title).text = getChapterTitle(index)


        element.isClickable = true
        element.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("shortChapterString", getCurrentChapterStrings(index)[0])
            intent.putExtra("longChapterString", getCurrentChapterStrings(index)[1])
            intent.putExtra("gameString", getCurrentChapterStrings(index)[2])
            startActivity(intent)
        }

        elementContainer.addView(element)

    }

    private fun countDirectories(context: Context): Int {
        val assetManager: AssetManager = context.assets
        return try {
            assetManager.list("")?.filter { it.contains("chapter") }?.size ?: 0
        } catch (e: IOException) {
            e.printStackTrace()
            0
        }
    }


    private fun getChapterTitle(index: Int): String? {
        val currentShortChapterString = shortChapterString.replace("#", index.toString())
        val jsonTextData = jsonTextParser.parse(this, currentShortChapterString)
        return jsonTextData[0].title
    }

    private fun getCurrentChapterStrings(index: Int): List<String> {
        val currentShortChapterString = shortChapterString.replace("#", index.toString())
        val currentLongChapterString = longChapterString.replace("#", index.toString())
        val currentGameString = gameString.replace("#", index.toString())

        return listOf(currentShortChapterString, currentLongChapterString, currentGameString)
    }
}