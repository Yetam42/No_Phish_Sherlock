package com.example.nophishsherlock

import ViewPagerAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.nophishsherlock.adapter.RecyclerViewAdapter
import com.example.nophishsherlock.contentbuilder.ContentViewBuilder
import com.example.nophishsherlock.data.JsonTextData
import com.example.nophishsherlock.game.GameSelectionActivty
import com.example.nophishsherlock.game.helper.LongChapterFragment
import com.example.nophishsherlock.parser.JsonTextParser
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray


/**
 * Todo: notwenig
 *      Spiel UI anpassen:
 *        Infobutton für die Spiele
 *       Weitere Ideen
 *        besseres Drag und Drop
 *        Text zum Ausklappen
 *        UI anpassen
 *
 *
 */

/**
 * Todo: optional
 *       wenn elemte hat soll farbe geändert werden, sonst nicht
 *      Sticky Bottom sheet für infobutton
 *      schauen ob ich noch die texte in eine string datei bekomme
 *       *        feedback anders
 *  *          feedback nicht im content
 */

/**
 * The MainActivity class is the main entry point of the application.
 * It serves as the primary activity for displaying and navigating through
 * the application's content, including short and long text versions of chapters,
 * game selection, and an information dialog.
 *
 * <p>
 * This Activity handles the display of content based on user selection
 * (short/long text), navigates to the game selection screen, and shows an
 * information dialog. It dynamically loads content from JSON files and
 * utilizes a custom view builder and RecyclerView for content presentation.
 * </p>
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


    private lateinit var shortChapterString: String
    private lateinit var longChapterString: String
    private lateinit var gameString: String


    val gameButtonText = "Spiele"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        shortChapterString = intent.getStringExtra("shortChapterString")!!
        longChapterString = intent.getStringExtra("longChapterString")!!
        gameString = intent.getStringExtra("gameString")!!


        setContentView(R.layout.main_screen)

        // Initialisiere Views
        titleTextView = findViewById(R.id.titleTextView)
        contentContainer = findViewById(R.id.contentContainer)
//        gameViewButton = findViewById(R.id.gameViewButton)
        gameViewButton = Button(this)
        gameViewButton.text = gameButtonText
        gameViewButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

//        nextButton = findViewById(R.id.nextButton)
        infoButton = findViewById(R.id.infoButton)

        titleTextView.setTextColor(getColor(R.color.text_color))

        showChapter()

        gameViewButton.setOnClickListener {
            val intent = Intent(this, GameSelectionActivty::class.java)
            Log.d("MainActivity", "Game string: $gameString")
            intent.putExtra("gameString", gameString)
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
                //nextButton.visibility = View.INVISIBLE
                infoButton.visibility = View.VISIBLE
                contentContainer.removeAllViews()
                showChapter()
                true
            }

            R.id.menu_long_text -> {
                showShort = false
                //nextButton.visibility = View.VISIBLE
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
        if (showShort) {
            // nextButton.visibility = View.INVISIBLE
            val currentChapterData = getJsonData(shortChapterString)
            buildShortView(currentChapterData)
        } else {
            // nextButton.visibility = View.VISIBLE
            val currentChapterData = getJsonData(longChapterString)
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

        contentContainer.addView(gameViewButton)

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
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )

//            (this.layoutParams as LinearLayout.LayoutParams).weight = 1f
        }

        val pageIndicator = TabLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tabGravity = TabLayout.GRAVITY_CENTER
            tabGravity = TabLayout.GRAVITY_CENTER
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            setSelectedTabIndicatorColor(R.drawable.selected_dot)
            setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_BOTTOM)

        }

        pageIndicator.requestLayout()
        pageIndicator.invalidate()

        val adapter = RecyclerViewAdapter(currentChapterData, this)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val fragments = createLongChapterFragments(currentChapterData)




        val viewPagerAdapter = ViewPagerAdapter(this, fragments)

        val viewPager = ViewPager2(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT // Oder eine feste Höhe
            )
        }

        contentContainer.addView(pageIndicator)
        contentContainer.addView(viewPager)
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(pageIndicator, viewPager) { tab, position ->
            tab.setIcon(R.drawable.selected_dot)
        }.attach()




        recyclerView.adapter = adapter
        //contentContainer.addView(recyclerView)
    }

    /**
     * Die Funktion holt die Daten für das aktuelle Kapitel
     *
     * @param jsonFile die JSON-Datei mit den Daten
     * @return die Daten für das aktuelle Kapitel
     */
    private fun getJsonData(jsonFile: String): List<JsonTextData> {
        val jsonData = jsonTextParser.parse(this, jsonFile)
        return jsonData
    }

    private fun createLongChapterFragments(currentChapterData: List<JsonTextData>): MutableList<Fragment> {
        val fragments = mutableListOf<Fragment>()
        for (chapter in currentChapterData) {
            Log.d("MainActivity", "Creating fragment for chapter: $chapter")
            val fragment = LongChapterFragment()
            val args = Bundle()
            val currentList = ArrayList<JsonTextData>()
            currentList.add(chapter)
            args.putParcelableArrayList("views", currentList)
            fragment.arguments = args
            fragments.add(fragment)
            Log.d("MainActivity", "Created fragment: $fragment")
        }
        return fragments

    }


}