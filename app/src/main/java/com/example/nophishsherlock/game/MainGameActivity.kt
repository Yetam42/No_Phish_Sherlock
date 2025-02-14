package com.example.nophishsherlock.game

import ViewPagerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.no_phishing_yannick.games.EndFragment
import com.example.no_phishing_yannick.games.pickwrong.PickWrongFragment
import com.example.no_phishing_yannick.games.rightOrwrong.RightOrWrongFragment
import com.example.nophishsherlock.InfoFragment
import com.example.nophishsherlock.LevelOverviewActivity
import com.example.nophishsherlock.R
import com.example.nophishsherlock.data.JsonGameData
import com.example.nophishsherlock.game.fragments.DragAndDropFragment
import com.example.nophishsherlock.game.fragments.GameOverFragment
import com.example.nophishsherlock.game.fragments.HiddenLinkFragment
import com.example.nophishsherlock.game.helper.GameFragmentListener
import com.example.nophishsherlock.game.helper.GameViewModel
import com.example.nophishsherlock.game.helper.LevelPrefernce
import com.example.nophishsherlock.parser.JsonGameParser
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// ... rest of your code ...
/**
 * Diese Klasse ist die Activity für das Hauptspiel
 *
 */
class MainGameActivity : AppCompatActivity(), GameFragmentListener {

    //hier werden die Elemente der Activity initialisiert
    lateinit var title: TextView
    lateinit var description: TextView
    lateinit var progressBar: ProgressBar
    lateinit var lives: TextView
    lateinit var viewPager: ViewPager2
    lateinit var confirm: Button
    lateinit var exit: ImageButton

    //der adapter für den ViewPager
    lateinit var adapter: ViewPagerAdapter

    lateinit var bottomAppBarContainer: LinearLayout
    lateinit var feedBack: TextView
    lateinit var weiterButton: Button

    lateinit var infoButton: ImageButton

    //die Liste mit allen Spielen, für das jeweilige Kapitel
    private var gameFraments = mutableListOf<Fragment>()

    //die Liste mit allen Daten für die Spiele
    private var gameDataList = mutableListOf<JsonGameData>()

    //index für das aktuelle Spiel
    private var currentGameIndex = 0

    // hier wird der Parser initialisiert
    private val jsonGameParser = JsonGameParser()

    //die Variable hasLostLife speichert, ob eine Leben verloren wurde
    private var hasLostLife = false

    //die Variable liveCount speichert die Anzahl der Leben. Es fängt mit 5 Leben an
    private var liveCount = 2

    lateinit var gameString: String

    lateinit var viewModel: GameViewModel

    lateinit var levelPrefernce: LevelPrefernce

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_main_screen)

        supportActionBar?.hide()
//        supportActionBar?.setDisplayHomeAsUpEnabled(true);
//        supportActionBar?.setDisplayShowHomeEnabled(true);

        gameString = intent.getStringExtra("gameString")!!

        // Initialisiere Views
        title = findViewById(R.id.instruction)
        description = findViewById(R.id.description)
        progressBar = findViewById(R.id.progressBar)
        viewPager = findViewById(R.id.gameContainer)
        lives = findViewById(R.id.lives)
        confirm = findViewById(R.id.confirm)
        exit = findViewById(R.id.exit)

        bottomAppBarContainer = findViewById(R.id.bottomAppBarContainer)
        feedBack = findViewById(R.id.feedBack)
        weiterButton = findViewById(R.id.weiterButton)

        infoButton = findViewById(R.id.infoButton)

        title.setTextColor(getColor(R.color.text_color))
        description.setTextColor(getColor(R.color.text_color))




        /*der confirm button wird am anfang unsichtbar gemacht underst,
        wenn eine entcheidung getroffen wurde sichtbar*/
        //confirm.visibility = View.INVISIBLE

        loadGameDatalist()

        initiateProgressBar()

        for (gameData in gameDataList) {
            loadFragements(gameData)
        }

        loadTitel()


        //initializer für den viewpager
        viewPager.isUserInputEnabled = false
        adapter = ViewPagerAdapter(this, gameFraments)

        viewPager.adapter = adapter



        // Click Funktionene für die Buttons
        confirm.setOnClickListener {
            nextFragment()
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Info")
            .setMessage("")
            .setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }
            .setNeutralButton("Verlassen") { dialog, which ->
                finish()
            }
            .create()


        exit.setOnClickListener {
            dialog.show()
        }

        infoButton.setOnClickListener {
            val dialog = InfoFragment()
            dialog.show(supportFragmentManager, "InformationFragment")
        }

        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        levelPrefernce = LevelPrefernce(this)

    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        if (item.itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Diese Funktion lädt die Fragmente der Spiele
     *
     * @param gameData die Daten für das Spiel
     */
    private fun loadFragements(gameData: JsonGameData) {
        title.text = gameData.title
        description.text = gameData.taskDescription

        when (gameData.game?.type) {
            "rightOrWrong" -> gameFraments.add(prepareFragment(RightOrWrongFragment(), gameData))
            "pickwrong" -> gameFraments.add(prepareFragment(PickWrongFragment(), gameData))
            "dragAndDrop" -> gameFraments.add(prepareFragment(DragAndDropFragment(), gameData))
            "hiddenLink" -> gameFraments.add(prepareFragment(HiddenLinkFragment(), gameData))
            else -> {
                println("Unknown game type: ${gameData.game?.type}")
            }
        }
    }

    /**
     * Diese Funktion übergibt den Fragmenten die notwendigen DAten
     *
     * @param fragment das jeweilige Fragment
     * @param gameData die Daten für das Spiel
     * @return das Fragment mit den Daten
     */
    private fun prepareFragment(fragment: Fragment, gameData: JsonGameData): Fragment {
        val bundle = Bundle()
        bundle.putParcelable("game", gameData.game)
        fragment.arguments = bundle
        return fragment
    }

    /**
     * Diese Funktion lädt die Daten für das Spiel
     */
    private fun loadGameDatalist(){
        gameDataList = jsonGameParser.parse(this, gameString).toMutableList()
    }

    /**
     * Diese Funktion initialisiert den Progressbar
     */
    private fun initiateProgressBar() {
        progressBar.max = gameDataList.size
        progressBar.progress = currentGameIndex + 1
        lives.text = "$liveCount"
    }

    /**
     * Diese Funktion lädt den Titel und die Beschreibung des aktuellen Spiels
     */
    private fun loadTitel() {
        title.text = gameDataList[currentGameIndex].title
        description.text = gameDataList[currentGameIndex].taskDescription
    }

    /**
     * Diese Funktion zeigt das nächste Spiel an
     */
    private fun nextFragment() {
        removeAppBar()
        confirm.isEnabled = false
        confirm.isClickable = false
        currentGameIndex++
        progressBar.progress = currentGameIndex + 1
        if (currentGameIndex < gameDataList.size) {
            viewPager.currentItem = currentGameIndex
            loadTitel()
        }
        if (currentGameIndex == gameDataList.size) {
            showEndScreen()
        }
    }

    /**
     * Diese Funktion entfernt die UI für das Spiel
     */
    private fun removeGameUI() {
        title.visibility = View.GONE
        description.visibility = View.GONE
        progressBar.visibility = View.GONE
        lives.visibility = View.GONE
        confirm.visibility = View.GONE
        infoButton.visibility = View.GONE
        exit.visibility = View.GONE
    }

    /**
     * Diese Funktion wird aufgerufen, wenn eine Antwort im Spiel getroffen wurde
     * war die Antwort falsch, wird enmalig, ein Leben abgezogen.
     * hat meine keine Leben mehr, wird das Spiel beendet
     */
    override fun onSelectionMade(isCorrect: Boolean) {

        confirm.isEnabled = true
        confirm.isClickable = true


        confirm.setOnClickListener {
            showFeedBack(isCorrect)
        }
    }

    private fun loseLife() {
        removeAppBar()
        if(liveCount <= 0){
            showGameOver()
        }
        else{
            lives.text = "$liveCount"
        }
    }

    private fun showFeedBack(isCorrect: Boolean) {
        addAppBar()

        if (isCorrect) {
            showCorrectFeedback()
        } else {
            showWrongFeedback()
        }

    }



    private fun addAppBar() {
        bottomAppBarContainer.visibility = View.VISIBLE
        val feedback = findViewById<TextView>(R.id.feedBack)
        val weiter = findViewById<Button>(R.id.weiterButton)
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta // fromYDelta
            bottomAppBarContainer.height.toFloat(),
            0f// toYDelta
        )
        animate.duration = 500
        animate.fillAfter = true
        bottomAppBarContainer.startAnimation(animate)
        weiter.isClickable = true
    }

    private fun showCorrectFeedback() {
        bottomAppBarContainer.background = AppCompatResources.getDrawable(
            this,
            android.R.color.holo_green_light
        )
        feedBack.text = "Voll gut richtig gemacht"
        weiterButton.text = "Weiter"

        weiterButton.setOnClickListener {
            hasLostLife = false
            nextFragment()
            weiterButton.isClickable = false
        }
    }



    private fun removeAppBar(){
        bottomAppBarContainer.visibility = View.GONE
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            0f,  // fromYDelta
            bottomAppBarContainer.height.toFloat()// toYDelta
        )
        animate.duration = 500
        animate.fillAfter = true
        bottomAppBarContainer.startAnimation(animate)
    }

    private fun showWrongFeedback() {
        bottomAppBarContainer.background = AppCompatResources.getDrawable(
            this,
            android.R.color.holo_red_light
        )
        if (!hasLostLife) {
            liveCount--
            hasLostLife = true
        }
        feedBack.text = "Schade man, dass war falsch :/"
        weiterButton.text = if (liveCount == 0) "Vorbei" else "Wiederholen"
        weiterButton.setOnClickListener {
            loseLife()
            weiterButton.isClickable = false

        }
    }

    private fun showGameOver() {
        adapter.addFragment(GameOverFragment())
        viewPager.currentItem = adapter.itemCount -1
        adapter.notifyItemInserted(adapter.itemCount - 1)
        removeGameUI()
    }

    private fun showEndScreen() {
        //viewModel.setLevelCompletion(currentLevel, true)


        adapter.addFragment(EndFragment())
        viewPager.currentItem = currentGameIndex
        adapter.notifyItemInserted(currentGameIndex)
        removeGameUI()
    }


    override fun onGameFinished(isCompleted: Boolean) {
        val currentLevel = viewModel.currentLevel.value ?: 1

        // Update the level completion status in the ViewModel.
        viewModel.setLevelCompleted(currentLevel, isCompleted)


        val intent = Intent(this, LevelOverviewActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onNextChapter() {
        val currentLevel = viewModel.currentLevel.value ?: 1
        viewModel.setLevelCompleted(currentLevel, true)

        // Also update the current level (move on to the next level).
        viewModel.setCurrentLevel(currentLevel + 1)
        val intent = Intent(this, LevelOverviewActivity::class.java)
        startActivity(intent)
        finish()

    }
}