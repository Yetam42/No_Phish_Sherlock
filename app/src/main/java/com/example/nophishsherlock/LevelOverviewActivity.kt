package com.example.nophishsherlock

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.nophishsherlock.game.helper.GameViewModel
import com.example.nophishsherlock.game.helper.LevelPrefernce
import com.example.nophishsherlock.parser.Gson.Parser
import com.example.nophishsherlock.parser.JsonTextParser
import java.io.IOException

class LevelOverviewActivity : AppCompatActivity() {

    private lateinit var elementContainer: LinearLayout
    private lateinit var titel: TextView

    private var shortChapterString = "#_chapter/#_chapter_short_image.json"
    private var longChapterString = "#_chapter/#_chapter_long.json"
    private var gameString = "#_chapter/#_game.json"

    private var chapterDirectories: List<String>? = null

    private val jsonTextParser = JsonTextParser<Any>()


    private var chapterCount = 0

    lateinit var viewModel: GameViewModel

    private lateinit var levelPreferences: LevelPrefernce



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.level_overview_screen)

        elementContainer = findViewById(R.id.elementContainer)
        titel = findViewById(R.id.title)

        titel.isClickable

        chapterCount = countDirectories(this)


        levelPreferences = LevelPrefernce(this)
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]

        titel.setOnClickListener {
            for (i in 1..chapterCount) {
                viewModel.setLevelCompleted(i, false)
            }
        }

        for (i in 1..chapterCount) {
            createElemente(i)
        }

        viewModel.levelCompletion.observe(this) { map ->
            Log.d("LevelOverview", "levelCompletion changed")

            for (i in 1..chapterCount) {
                // Find the UI element for level i
                val element = elementContainer.getChildAt(i - 1)
                val titleTextView = element.findViewById<TextView>(R.id.title)

                // Update the text based on whether the level is completed
                if (map[i] == true) {
                    titleTextView.text = "Completed"
                } else {
                    titleTextView.text = getChapterTitle(i)
                }
            }
        }




        viewModel.currentLevel.observe(this) { newLevel ->
            Log.d("LevelOverview", "currentLevel changed to $newLevel")
            Log.d("LevelOverview", "currentChapter is ${levelPreferences.getCurrentChapter()}")
            if (newLevel != levelPreferences.getCurrentChapter()) {
                levelPreferences.setCurrentChapter(newLevel)
                startChapter(newLevel)
            }
        }
        val parser = Parser()

        Log.d("LevelOverview", "Loading chapter data....")
        val jsonTextData = parser.parse(this, "1_chapter/1_chapter_short_image.json")
        for (data in jsonTextData) {
            Log.d("LevelOverview", "data: $data")
        }


    }


    private fun createElemente(index: Int) {
        val element = layoutInflater.inflate(R.layout.level_overview_element, null)
        element.findViewById<TextView>(R.id.chapter_number).text = "$index"
        element.findViewById<TextView>(R.id.title).text = getChapterTitle(index)


        val assetManager: AssetManager = this.assets
        val assetList: Array<String>? =
            chapterDirectories?.let { assetManager.list(it.get(index - 1)) }
        if (assetList != null) {
            Log.d("LevelOverview", "${assetList.contains("${index}_game.json")}")
        }

        element.isClickable = true
        element.setOnClickListener {
            startChapter(index)
        }

        elementContainer.addView(element)

    }

    private fun startChapter(chapterIndex: Int) {
        val intent = Intent(this, MainActivity::class.java)

        levelPreferences.setCurrentLevel(chapterIndex)
        levelPreferences.setCurrentChapter(chapterIndex)

        Log.d("GAME", "Set current level to $chapterIndex")

        intent.putExtra("shortChapterString", getCurrentChapterStrings(chapterIndex)[0])
        intent.putExtra("longChapterString", getCurrentChapterStrings(chapterIndex)[1])

        if (hasGame(chapterIndex)) {
            intent.putExtra("gameString", getCurrentChapterStrings(chapterIndex)[2])
            Log.d("LevelOverview", "has game")
            Log.d("LevelOverview", getCurrentChapterStrings(chapterIndex)[2])
        } else {
            intent.putExtra("gameString", "noGame")
        }
        startActivity(intent)
    }


    private fun countDirectories(context: Context): Int {
        val assetManager: AssetManager = context.assets
        return try {
            chapterDirectories = assetManager.list("")?.filter { it.contains("chapter") }

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

    private fun hasGame(index: Int): Boolean {
        val assetManager: AssetManager = this.assets
        val assetList: Array<String>? =
            chapterDirectories?.let { assetManager.list(it.get(index - 1)) }

        var hasGame = false
        if (assetList != null) {
            hasGame = assetList.contains("${index}_game.json")

        }
        return hasGame
    }
}