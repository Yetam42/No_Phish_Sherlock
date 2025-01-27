package com.example.nophishsherlock.game

import ViewPagerAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.no_phishing_yannick.games.EndFragment
import com.example.no_phishing_yannick.games.pickwrong.PickWrongFragment
import com.example.no_phishing_yannick.games.rightOrwrong.RightOrWrongFragment
import com.example.nophishsherlock.R
import com.example.nophishsherlock.data.JsonGameData
import com.example.nophishsherlock.game.fragments.GameOverFragment
import com.example.nophishsherlock.game.helper.GameFragmentListener
import com.example.nophishsherlock.parser.JsonGameParser

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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_main_screen)

        // Initialisiere Views
        title = findViewById(R.id.instruction)
        description = findViewById(R.id.description)
        progressBar = findViewById(R.id.progressBar)
        viewPager = findViewById(R.id.gameContainer)
        lives = findViewById(R.id.lives)
        confirm = findViewById(R.id.confirm)
        exit = findViewById(R.id.exit)

        title.setTextColor(getColor(R.color.text_color))
        description.setTextColor(getColor(R.color.text_color))

        /*der confirm button wird am anfang unsichtbar gemacht underst,
        wenn eine entcheidung getroffen wurde sichtbar*/
        confirm.visibility = View.INVISIBLE

        loadGameDatalist()

        initiateProgressBar()

        for (gameData in gameDataList) {
            loadFragements(gameData)
        }

        loadTitel()


        //initializer für den viewpager
        adapter = ViewPagerAdapter(this, gameFraments)
        viewPager.adapter = adapter



        // Click Funktionene für die Buttons
        confirm.setOnClickListener {
            nextFragment()
        }

        exit.setOnClickListener {
            finish()
        }

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
        gameDataList = jsonGameParser.parse(this, "1_chapter/1_game.json").toMutableList()
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
        confirm.visibility = View.INVISIBLE
        currentGameIndex++
        progressBar.progress = currentGameIndex + 1
        if (currentGameIndex < gameDataList.size) {
            viewPager.currentItem = currentGameIndex
            loadTitel()
        }
        if (currentGameIndex == gameDataList.size) {
            adapter.addFragment(EndFragment())
            adapter.notifyDataSetChanged()
            viewPager.currentItem = currentGameIndex
            removeGameUI()
        }
    }

    /**
     * Diese Funktion entfernt die UI für das Spiel
     */
    private fun removeGameUI() {
        title.visibility = View.INVISIBLE
        description.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        lives.visibility = View.INVISIBLE
        confirm.visibility = View.INVISIBLE
    }

    /**
     * Diese Funktion wird aufgerufen, wenn eine Antwort im Spiel getroffen wurde
     * war die Antwort falsch, wird enmalig, ein Leben abgezogen.
     * hat meine keine Leben mehr, wird das Spiel beendet
     */
    override fun onSelectionMade(isCorrect: Boolean) {
        confirm.visibility = View.VISIBLE

        confirm.setOnClickListener {
            if (isCorrect) {
                nextFragment()
                hasLostLife = false
            } else {
                if (!hasLostLife) {
                    liveCount--
                    Toast.makeText(this, "Leider falsch, du verlierst ein Leben", Toast.LENGTH_SHORT).show()
                    hasLostLife = true
                }
                if (liveCount <= 0) {
                    Log.d("MainGameActivity", "Game over")
                    adapter.addFragment(GameOverFragment())
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.itemCount -1
                    removeGameUI()
                } else {
                    lives.text = "$liveCount"
                }
            }
        }
    }
}