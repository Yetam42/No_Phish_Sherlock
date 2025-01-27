package com.example.nophishsherlock

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nophishsherlock.adapter.RecyclerViewAdapter
import com.example.nophishsherlock.contentbuilder.ContentViewBuilder
import com.example.nophishsherlock.data.JsonTextData
import com.example.nophishsherlock.game.GameSelectionActivty
import com.example.nophishsherlock.parser.JsonTextParser
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray


/**
 * Diese Klasse ist die Activity für das Hauptmenü
 */
class MainActivity : AppCompatActivity() {

    // hier werden die Elemente der Activity initialisiert
    private lateinit var titleTextView: TextView
    private lateinit var contentContainer: LinearLayout
    private lateinit var gameViewButton: Button
    private lateinit var nextButton: Button
    private lateinit var infoButton: FloatingActionButton

    // hier wird der JsonTextParser initialisiert
    private val jsonTextParser = JsonTextParser()

    //Die Variable showShort gibt an, ob ein kurzer Text oder langer Text angezeigt werden soll
    private var showShort = true

    //Der JSONArray chapters speichert alle Kapitel
    private lateinit var chapters: JSONArray

    //Die Variable currentChapterIndex speichert den Index des aktuellen Kapitels
    //private var currentChapterIndex = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_screen)

        // Initialisiere Views
        titleTextView = findViewById(R.id.titleTextView)
        contentContainer = findViewById(R.id.contentContainer)
        gameViewButton = findViewById(R.id.gameViewButton)
        nextButton = findViewById(R.id.nextButton)
        infoButton = findViewById(R.id.infoButton)

        titleTextView.setTextColor(getColor(R.color.text_color))

        showChapter()

        gameViewButton.setOnClickListener {
            val intent = Intent(this, GameSelectionActivty::class.java)
            startActivity(intent)
        }

        infoButton.setOnClickListener {
            val dialog = InfoFragment()
            dialog.show(supportFragmentManager, "InformationFragment")

        }

    }

    /**
     * Diese Funktion kümmert sich um die Funktionalität des Menus
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        contentContainer.removeAllViews()
        return when (item.itemId) {
            R.id.menu_short_text -> {
                showShort = true
                nextButton.visibility = View.INVISIBLE
                infoButton.visibility = View.VISIBLE
                contentContainer.removeAllViews()
                showChapter()
                true
            }

            R.id.menu_long_text -> {
                showShort = false
                nextButton.visibility = View.VISIBLE
                infoButton.visibility = View.INVISIBLE
                contentContainer.removeAllViews()
                showChapter()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Diese Funktion erstellt das Menü
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Die Funktion showChapter() zeigt das aktuellen Kapitel an
     */
    private fun showChapter() {
        if(showShort) {
            nextButton.visibility = View.INVISIBLE
            val currentChapterData = getJsonData("1_chapter/1_chapter_short.json")
            buildShortView(currentChapterData)
        }
        else {
            nextButton.visibility = View.VISIBLE
            val currentChapterData = getJsonData("1_chapter/1_chapter_long.json")
            buildLongView(currentChapterData)
        }
    }

    /**
     * Die Funktion buildShortView() zeigt den kurzen Text für das aktuelle an
     *
     * @param currentChapterData die Daten für das aktuelle Kapitel
     */
    private fun buildShortView(currentChapterData: List<JsonTextData>) {
        val viewBuilder = ContentViewBuilder(this)
        titleTextView.text = currentChapterData[0].title
        val views = viewBuilder.buildContent(currentChapterData)
        for (view in views) {
            contentContainer.addView(view)
        }
    }

    /**
     * Die Funktion buildLongView() zeigt den langen Text für das aktuelle Kapitel an
     *
     * @param currentChapterData die Daten für das aktuelle Kapitel
     */
    private fun buildLongView(currentChapterData: List<JsonTextData>) {
        titleTextView.text = currentChapterData[0].title
        val recyclerView = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT // Oder eine feste Höhe
            )
        }

        val adapter = RecyclerViewAdapter(currentChapterData, this)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerView.adapter = adapter
        contentContainer.addView(recyclerView)
    }

    /**
     * Die Funktion holt die Daten für das aktuelle Kapitel
     *
     * @param jsonFile die JSON-Datei mit den Daten
     * @return die Daten für das aktuelle Kapitel
     */
    private fun getJsonData(jsonFile: String) : List<JsonTextData> {
        val jsonData = jsonTextParser.parse(this, jsonFile)
        return jsonData
    }

}