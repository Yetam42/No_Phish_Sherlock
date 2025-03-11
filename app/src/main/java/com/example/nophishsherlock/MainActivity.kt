package com.example.nophishsherlock

import ViewPagerAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.no_phishing_yannick.games.EndFragment
import com.example.nophishsherlock.contentbuilder.ChapterFragment
import com.example.nophishsherlock.data.JsonTextData
import com.example.nophishsherlock.game.MainGameActivity
import com.example.nophishsherlock.game.helper.GameFragmentListener
import com.example.nophishsherlock.game.helper.GameViewModel
import com.example.nophishsherlock.parser.Gson.Parser
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout


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
class MainActivity : AppCompatActivity(), GameFragmentListener {


    // UI Elemente
    private lateinit var topAppBar: MaterialToolbar

    private lateinit var contentContainer: ViewPager2
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPagerData: MutableList<Fragment>


    private lateinit var gameViewButton: Button
    private lateinit var infoButton: FloatingActionButton

    private lateinit var bottomBar: LinearLayout
    private lateinit var previousPage: Button
    private lateinit var nextPage: Button
    private lateinit var pageIndicator: TabLayout


    //Chapter information
    lateinit var viewModel: GameViewModel

    private lateinit var shortChapterString: String
    private lateinit var longChapterString: String
    private lateinit var gameString: String

    // Configurations
    private var showShort = true
    private val gameButtonText = "Spiele"
    private val endChapterText = "Level Beenden"
    private val NO_GAME = "noGame"
    private val NO_CHAPTER = JsonTextData(
        "ERROR",
        "NO DATA AVAILABLE",
        listOf(
            "There is no data for the chapter or version you chose",
            "Please ask the developer to add the chapter or version to the code"
        )
    )
    private val RETRUN = "Return to homepage"

    private val buttonLayoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.CENTER
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)



        getChapterData()

        setUpAppBar()
        setUpUI()

        showChapter()
        handleInteraction()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun getChapterData() {
        shortChapterString = intent.getStringExtra("shortChapterString")!!
        longChapterString = intent.getStringExtra("longChapterString")!!
        gameString = intent.getStringExtra("gameString")!!

        viewModel = ViewModelProvider(this)[GameViewModel::class.java]

    }

    private fun setUpAppBar() {
        topAppBar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, LevelOverviewActivity::class.java)
            startActivity(intent)
        }

        topAppBar.title =
            viewModel.currentLevel.value.toString() + ". " + getJsonData(shortChapterString)[0].title



        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_short_text -> {
                    clearFragments(true)
                    if (!showShort) {
                        showShort = true
                        // infoButton.visibility = View.VISIBLE
                        bottomBar.visibility = View.GONE
                        showChapter()
                    }
                    true
                }

                R.id.menu_long_text -> {
                    clearFragments(false)
                    if (showShort) {
                        showShort = false
                        bottomBar.visibility = View.VISIBLE
                        // infoButton.visibility = View.INVISIBLE
                        showChapter()
                    }
                    true
                }

                else -> super.onOptionsItemSelected(menuItem)
            }
        }
    }

    private fun setUpUI() {
        contentContainer = findViewById(R.id.contentContainer)
        viewPagerAdapter = ViewPagerAdapter(this, mutableListOf())
        contentContainer.adapter = viewPagerAdapter

        gameViewButton = MaterialButton(this).apply {
            text = gameButtonText
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        infoButton = findViewById(R.id.infoButton)
        previousPage = findViewById(R.id.previousPage)
        nextPage = findViewById(R.id.nextPage)
        pageIndicator = findViewById(R.id.pageIndicator)
        bottomBar = findViewById(R.id.bottomBar)


    }

    private fun handleInteraction() {
        if (gameString == NO_GAME) {

            Log.d("MainActivity", "no game")

            if (topAppBar.title == NO_CHAPTER.title) {
                gameViewButton.text = RETRUN
                gameViewButton.setOnClickListener {
                    finish()
                }
            } else {
                gameViewButton.text = endChapterText

                gameViewButton.setOnClickListener {
                    val currentLevel = viewModel.currentLevel.value ?: 1
                    viewModel.setLevelCompleted(currentLevel, true)

                    viewPagerData.clear()
                    viewPagerData = mutableListOf(EndFragment())
                    viewPagerAdapter.notifyDataSetChanged()
                    infoButton.visibility = View.GONE
                    topAppBar.visibility = View.GONE
                    val menu = topAppBar.menu as Menu
                    menu.clear()

                    viewPagerAdapter = ViewPagerAdapter(this, viewPagerData)
                    contentContainer.adapter = viewPagerAdapter

                }
            }


        } else {
            gameViewButton.setOnClickListener {
                val intent = Intent(this, MainGameActivity::class.java)
                intent.putExtra("gameString", gameString)
                startActivity(intent)
            }
        }

        infoButton.setOnClickListener {
            val dialog = InfoFragment()
            dialog.show(supportFragmentManager, "InformationFragment")
        }

        previousPage.setOnClickListener {
            contentContainer.currentItem -= 1
            checkViewPager(viewPagerAdapter)
        }

        nextPage.setOnClickListener {
            contentContainer.currentItem += 1
            checkViewPager(viewPagerAdapter)
        }

        contentContainer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                checkViewPager(viewPagerAdapter)
            }
        })
    }

    private fun showChapter() {

        if (showShort) {
            val currentChapterData = getJsonData(shortChapterString)
            buildShortView(currentChapterData)
        } else {
            val currentChapterData = getJsonData(longChapterString)
            buildLongView(currentChapterData)
        }
    }

    private fun buildShortView(currentChapterData: List<JsonTextData>) {
        val fragment = ChapterFragment()
        val args = Bundle()
        args.putParcelableArrayList("views", ArrayList(currentChapterData))
        fragment.arguments = args
        fragment.setGameViewButton(gameViewButton, buttonLayoutParams)
        viewPagerData = mutableListOf(fragment)
        viewPagerAdapter = ViewPagerAdapter(this, viewPagerData)
        contentContainer.adapter = viewPagerAdapter
    }

    private fun buildLongView(currentChapterData: List<JsonTextData>) {
        viewPagerData = createChapterFragments(currentChapterData)
        viewPagerAdapter = ViewPagerAdapter(this, viewPagerData)
        contentContainer.adapter = viewPagerAdapter
        checkViewPager(viewPagerAdapter)

    }

    private fun createChapterFragments(currentChapterData: List<JsonTextData>): MutableList<Fragment> {
        val fragments = mutableListOf<Fragment>()

        for (chapter in currentChapterData) {
            val fragment = ChapterFragment()
            val args = Bundle()
            val currentList = ArrayList<JsonTextData>()
            currentList.add(chapter)
            args.putParcelableArrayList("views", currentList)
            fragment.arguments = args
            fragments.add(fragment)
        }

        val chapterFragment = fragments.last() as ChapterFragment
        chapterFragment.setGameViewButton(gameViewButton, buttonLayoutParams)
        return fragments
    }


    private fun getJsonData(chapterString: String): List<JsonTextData> {
        val parser = Parser()

        return try {
            parser.parse(this, chapterString)
        } catch (e: NullPointerException) {
            e.printStackTrace()
            listOf(NO_CHAPTER)
        }

    }

    private fun clearFragments(short: Boolean) {
        if ((!showShort && short) || (showShort && !short)) {
            viewPagerData.clear()
            viewPagerAdapter.notifyDataSetChanged()
        }

    }

    private fun checkViewPager(viewPagerAdapter: ViewPagerAdapter) {
        if (contentContainer.currentItem == 0) {
            previousPage.visibility = View.INVISIBLE
        } else {
            previousPage.visibility = View.VISIBLE
        }

        if (contentContainer.currentItem == viewPagerAdapter.itemCount - 1) {
            nextPage.visibility = View.INVISIBLE
        } else {
            nextPage.visibility = View.VISIBLE
        }
    }

    override fun onSelectionMade(isCorrect: Boolean) {
    }

    override fun onGameFinished(isCompleted: Boolean) {
        val currentLevel = viewModel.currentLevel.value ?: 1
        // Update the level completion status in the ViewModel.
        viewModel.setLevelCompleted(currentLevel, isCompleted)

        Log.d("MainActivity", "Level $currentLevel completed: $isCompleted")

        val intent = Intent(this, LevelOverviewActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onNextChapter() {
        val currentLevel = viewModel.currentLevel.value ?: 1
        viewModel.setLevelCompleted(currentLevel, true)

        viewModel.setCurrentLevel(currentLevel + 1)

        Log.d("MainActivity", "going to next chapter")
        val intent = Intent(this, LevelOverviewActivity::class.java)
        startActivity(intent)
        finish()
    }
}