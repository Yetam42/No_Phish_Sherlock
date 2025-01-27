package com.example.nophishsherlock.game.helper

/**
 * Interface für die GameFragments und die GameActivty
 *
 */
interface GameFragmentListener {
    /**
     * Die GameActivty kann hiermit überprüfen ob eine Antwort in den Spielfragmenten getroffen wurde
     *
     * @param isCorrect gibt an ob Antwort richtig war
     */
    fun onSelectionMade(isCorrect: Boolean)
}