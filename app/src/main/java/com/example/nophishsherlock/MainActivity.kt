package com.example.nophishsherlock

import ViewPagerAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.nophishsherlock.contentbuilder.ContentViewBuilder
import com.example.nophishsherlock.data.JsonTextData
import com.example.nophishsherlock.game.MainGameActivity
import com.example.nophishsherlock.game.helper.GameViewModel
import com.example.nophishsherlock.game.helper.LevelPrefernce
import com.example.nophishsherlock.game.helper.LongChapterFragment
import com.example.nophishsherlock.parser.Gson.Parser
import com.example.nophishsherlock.parser.JsonTextParser
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray


/**
 * Todo: notwenig
 *      Spiel UI anpassen:
 *        Infobutton für die Spiele
 *     leere JSON Datei
 *
 *
 */

/**
 * Todo: optional
 *       wenn elemte hat soll farbe geändert werden, sonst nicht
 *      schauen ob ich noch die texte in eine string datei bekomme
 *  *          feedback nicht im content
 *   *        Text zum Ausklappen
 *
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
    private lateinit var infoButton: FloatingActionButton

    // hier wird der JsonTextParser initialisiert
    private val jsonTextParser = JsonTextParser<Any>()

    //Die Variable showShort gibt an, ob ein kurzer Text oder langer Text angezeigt werden soll
    private var showShort = true

    //Der JSONArray chapters speichert alle Kapitel
    private lateinit var chapters: JSONArray

    //Die Variable currentChapterIndex speichert den Index des aktuellen Kapitels
    //private var currentChapterIndex = 0


    private lateinit var previousPage: Button
    private lateinit var nextPage: Button
    private lateinit var pageIndicator: TabLayout

    private lateinit var shortChapterString: String
    private lateinit var longChapterString: String
    private lateinit var gameString: String


    val gameButtonText = "Spiele"

    val NO_GAME = "noGame"

    lateinit var viewModel: GameViewModel

    lateinit var levelPrefernce: LevelPrefernce


    private lateinit var viewPager: ViewPager2

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
        gameViewButton = MaterialButton(this).apply {
            text = gameButtonText
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        levelPrefernce = LevelPrefernce(this)


        if (gameString == NO_GAME) {
            gameViewButton.text = "Level Beenden"

            gameViewButton.setOnClickListener {
                val currentLevel = viewModel.currentLevel.value ?: 1
                viewModel.setLevelCompleted(currentLevel, true)

                // Also update the current level (move on to the next level).
                viewModel.setCurrentLevel(currentLevel + 1)
                val intent = Intent(this, LevelOverviewActivity::class.java)

                viewPager.adapter
                startActivity(intent)
            }
        } else {
            gameViewButton.setOnClickListener {
                val intent = Intent(this, MainGameActivity::class.java)
                intent.putExtra("gameString", gameString)
                startActivity(intent)
            }
        }

//        nextButton = findViewById(R.id.nextButton)
        infoButton = findViewById(R.id.infoButton)

        titleTextView.setTextColor(getColor(R.color.text_color))

        showChapter()


        infoButton.setOnClickListener {
            val dialog = InfoFragment()
            dialog.show(supportFragmentManager, "InformationFragment")

        }

        previousPage = findViewById(R.id.previousPage)
        nextPage = findViewById(R.id.nextPage)
        pageIndicator = findViewById(R.id.pageIndicator)


    }

    /**
     * Diese Funktion kümmert sich um die Funktionalität des Menus
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        contentContainer.removeAllViews()
        return when (item.itemId) {
            R.id.menu_short_text -> {
                showShort = true
                val parent = gameViewButton.parent
                if (parent is ViewGroup) {
                    parent.removeView(gameViewButton)
                }
                //nextButton.visibility = View.INVISIBLE
                infoButton.visibility = View.VISIBLE
                nextPage.visibility = View.GONE
                previousPage.visibility = View.GONE
                pageIndicator.visibility = View.GONE
                contentContainer.removeAllViews()
                showChapter()
                true
            }

            R.id.menu_long_text -> {
                showShort = false
                //nextButton.visibility = View.VISIBLE
                nextPage.visibility = View.VISIBLE
                previousPage.visibility = View.VISIBLE
                pageIndicator.visibility = View.VISIBLE
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

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        contentContainer.addView(gameViewButton, layoutParams)

    }

    /**
     * Die Funktion buildLongView() zeigt den langen Text für das aktuelle Kapitel an
     *
     * @param currentChapterData die Daten für das aktuelle Kapitel
     */
    private fun buildLongView(currentChapterData: List<JsonTextData>) {
        titleTextView.text = currentChapterData[0].title
//        val recyclerView = RecyclerView(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//            )
//
//        }

        //val pageIndicator = createPageIndicator(this)

//        val adapter = RecyclerViewAdapter(currentChapterData, this)
//        recyclerView.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val fragments = createLongChapterFragments(currentChapterData)
        Log.d("MainActivity", "creating new fragments")


        val viewPagerAdapter = ViewPagerAdapter(this, fragments)

        viewPager = ViewPager2(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT // Oder eine feste Höhe
            )
        }


        // contentContainer.addView(pageIndicator)
        contentContainer.addView(viewPager)
        viewPager.adapter = viewPagerAdapter


        check(viewPagerAdapter)

        previousPage.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem - 1
            check(viewPagerAdapter)

        }

        nextPage.setOnClickListener {
            viewPager.currentItem += 1
            check(viewPagerAdapter)
        }



        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                check(viewPagerAdapter)
            }
        })


        TabLayoutMediator(pageIndicator, viewPager) { tab, index ->
            tab.text = "${index + 1}"
        }.attach()


        //recyclerView.adapter = adapter
        //contentContainer.addView(recyclerView)
    }

    fun check(viewPagerAdapter: ViewPagerAdapter) {
        if (viewPager.currentItem == 0) {
            previousPage.visibility = View.INVISIBLE
        } else {
            previousPage.visibility = View.VISIBLE
        }

        if (viewPager.currentItem == viewPagerAdapter.itemCount - 1) {
            nextPage.visibility = View.INVISIBLE
        } else {
            nextPage.visibility = View.VISIBLE
        }
    }

    /**
     * Die Funktion holt die Daten für das aktuelle Kapitel
     *
     * @param jsonFile die JSON-Datei mit den Daten
     * @return die Daten für das aktuelle Kapitel
     */
    private fun getJsonData(jsonFile: String): List<JsonTextData> {
        val parser = Parser()
        //TODO richtig implementieren


        val jsonData = jsonTextParser.parse(this, jsonFile)
        return parser.parse(this, jsonFile)
    }

    private fun createLongChapterFragments(currentChapterData: List<JsonTextData>): MutableList<Fragment> {
        val fragments = mutableListOf<Fragment>()
        for (chapter in currentChapterData) {
            val fragment = LongChapterFragment()
            val args = Bundle()
            val currentList = ArrayList<JsonTextData>()
            currentList.add(chapter)
            args.putParcelableArrayList("views", currentList)
            fragment.arguments = args
            fragments.add(fragment)
        }

        val longChapterFragment = fragments.last() as LongChapterFragment
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        longChapterFragment.setGameViewButton(gameViewButton, layoutParams)

        return fragments

    }


    private fun createPageIndicator(context: Context): TabLayout {
        return TabLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tabGravity = TabLayout.GRAVITY_CENTER
            setSelectedTabIndicator(R.drawable.selected_dot)
            setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_BOTTOM)
        }
    }
}