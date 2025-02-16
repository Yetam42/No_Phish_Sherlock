package com.example.no_phishing_yannick.games.helper

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.nophishsherlock.data.GameData
import com.example.nophishsherlock.game.helper.GameFragmentListener

/**
 * Abstrakte Klasse für die GameFragments
 *
 */
abstract class BaseGameFragment : Fragment() {

    private var listener: GameFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GameFragmentListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement GameFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    protected fun notifyActivity(isCorrect: Boolean) {
        listener?.onSelectionMade(isCorrect)
    }

    /**
     * bearbeitet User auswahl
     *
     */
    abstract fun handleUserSelection()

    /**
     * updatet UI nach Antwort
     *
     * @param isCorrect gibt an ob Antwort richtig war
     */
    abstract fun updateUI(isCorrect: Boolean)

    /**
     * Parst GameData
     *
     * @param gameData die GameData die geparst werden soll
     * @return die jeweilige Spieldatenklasse
     */
    abstract fun parseGameData(gameData: GameData): Any
    //TODO Any?

}