package com.example.nophishsherlock

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.nophishsherlock.game.helper.GameViewModel
import com.example.nophishsherlock.game.helper.LevelPrefernce
import com.example.nophishsherlock.parser.JsonTextParser
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import java.io.IOException

class LevelOverviewActivity : AppCompatActivity() {

    private lateinit var elementContainer: LinearLayout
    private lateinit var topAppBar: MaterialToolbar

    private var shortChapterString = "#_chapter/#_chapter_short.json"
    private var longChapterString = "#_chapter/#_chapter_long.json"
    private var gameString = "#_chapter/#_game.json"

    private var chapterDirectories: List<String>? = null

    private val jsonTextParser = JsonTextParser()


    private var chapterCount = 0

    lateinit var viewModel: GameViewModel

    private lateinit var levelPreferences: LevelPrefernce

    private val NO_SHORT_VERSION = "No short version available"
    private val NO_DATA_AVAILABLE = "No data available"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.level_overview_screen)

        elementContainer = findViewById(R.id.elementContainer)
        topAppBar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)

        topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_reset -> {
                    for (i in 1..chapterCount) {
                        viewModel.setLevelCompleted(i, false)
                    }
                    true
                }

                else -> super.onOptionsItemSelected(it)
            }
        }

        chapterCount = countDirectories(this)


        levelPreferences = LevelPrefernce(this)
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]


        for (i in 1..chapterCount) {
            createElemente(i)
        }


        viewModel.levelCompletion.observe(this) { map ->

            for (i in 1..chapterCount) {
                val element = elementContainer.getChildAt(i - 1)
                val chapterNumberCard =
                    element.findViewById<MaterialCardView>(R.id.chapterNumberCard)

                // Verändert die Farbe, wenn Kapitel abgeschlossen wurde
                if (map[i] == true) {
                    chapterNumberCard.setBackgroundColor(
                        MaterialColors.getColor(
                            chapterNumberCard,
                            com.google.android.material.R.attr.colorSecondary
                        )
                    )
                } else {
                    chapterNumberCard.setBackgroundColor(
                        MaterialColors.getColor(
                            chapterNumberCard,
                            com.google.android.material.R.attr.colorSurfaceContainer
                        )
                    )
                }
            }
        }




        viewModel.currentLevel.observe(this) { newLevel ->
            if (newLevel != levelPreferences.getCurrentChapter()) {
                levelPreferences.setCurrentChapter(newLevel)
                startChapter(newLevel)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_overview, menu)
        return true
    }


    private fun createElemente(index: Int) {
        val element = layoutInflater.inflate(R.layout.level_overview_element, null)
        var currentTitel = getChapterTitle(index)
        element.findViewById<TextView>(R.id.chapter_number).text = "$index"


        if (currentTitel != NO_SHORT_VERSION) {
            element.isClickable = true
            element.setOnClickListener {
                startChapter(index)
            }
        } else {
            currentTitel = getTitleFromLong(index)
        }

        element.findViewById<TextView>(R.id.title).text = currentTitel




        elementContainer.addView(element)

    }

    private fun startChapter(chapterIndex: Int) {
        val intent = Intent(this, MainActivity::class.java)

        levelPreferences.setCurrentLevel(chapterIndex)
        levelPreferences.setCurrentChapter(chapterIndex)


        intent.putExtra("shortChapterString", getCurrentChapterStrings(chapterIndex)[0])
        intent.putExtra("longChapterString", getCurrentChapterStrings(chapterIndex)[1])

        if (hasGame(chapterIndex)) {
            intent.putExtra("gameString", getCurrentChapterStrings(chapterIndex)[2])
        } else {
            intent.putExtra("gameString", "noGame")
        }
        startActivity(intent)
    }


    private fun countDirectories(context: Context): Int {
        val assetManager: AssetManager = context.assets
        return try {
            val unsortedDirectories = assetManager.list("")?.filter { it.contains("chapter") }
            chapterDirectories = unsortedDirectories?.sortedBy {
                val matchResult = Regex("(\\d+)_chapter").find(it)
                matchResult?.groupValues?.get(1)?.toIntOrNull() ?: Int.MAX_VALUE
            }

            assetManager.list("")?.filter { it.contains("chapter") }?.size ?: 0
        } catch (e: IOException) {
            e.printStackTrace()
            0
        }
    }


    private fun getChapterTitle(index: Int): String? {
        val currentShortChapterString = shortChapterString.replace("#", index.toString())
        val chapterTitel = try {
            val jsonTextData = jsonTextParser.parse(this, currentShortChapterString)
            jsonTextData[0].title

        } catch (e: Exception) {
            e.printStackTrace()
            NO_SHORT_VERSION
        }

        return chapterTitel
    }

    private fun getTitleFromLong(index: Int): String? {
        val currentShortChapterString = longChapterString.replace("#", index.toString())
        val chapterTitel = try {
            val jsonTextData = jsonTextParser.parse(this, currentShortChapterString)
            jsonTextData[0].title
        } catch (e: Exception) {
            e.printStackTrace()
            NO_DATA_AVAILABLE
        }
        return chapterTitel
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
            chapterDirectories?.let { assetManager.list(it[index - 1]) }



        var hasGame = false
        if (assetList != null) {
            hasGame = assetList.contains("${index}_game.json")

        }
        return hasGame
    }
}