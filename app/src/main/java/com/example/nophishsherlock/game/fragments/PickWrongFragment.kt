package com.example.nophishsherlock.game.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.no_phishing_yannick.games.helper.BaseGameFragment
import com.example.nophishsherlock.R
import com.example.nophishsherlock.data.GameData

/**
 * Diese Klasse repräsentiert das PickWrongFragment
 *
 */
class PickWrongFragment : BaseGameFragment() {

    private lateinit var gameText: TextView
    val clickedWords = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view = inflater.inflate(R.layout.fragment_pickwrong, container, false)
        gameText = view.findViewById(R.id.gameText)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameText = view.findViewById(R.id.gameText)


        if (arguments != null) {
            val empfangeneFrage = arguments?.getParcelable<GameData>("game")
            val currentGameData = empfangeneFrage?.let { parseGameData(it) }


            if (currentGameData != null) {

                gameText.movementMethod = LinkMovementMethod.getInstance() // Hinzufügen!
                gameText.text = createSpannableString(currentGameData, gameText)
                context?.let { gameText.setTextColor(it.getColor(R.color.text_color)) }
            }
        }


    }

    /**
     * Diese Funktion erstellt den SpannableString
     * auf diesen kann man klicken
     *
     * @param currentGameData die Daten die eingefügt werden sollen
     * @return SpannableString mit den Daten
     */
    private fun createSpannableString(
        currentGameData: PickWrongData,
        gameText: TextView
    ): SpannableString {
        val spannableString = SpannableString(currentGameData.text)

        /***
         * TODO delimiter in json mitgeben
         * val pattern: Pattern = Pattern.compile("\\b\\w+\\b|[.,!]") // Regex zum Finden von Wörtern und Satzzeichen
         * und das ausgewählter bereich leuchtet
         */

        val words = currentGameData.text.split(Regex("[./]"))

        val rightWords = currentGameData.pickWord.split(Regex("[.]"))


        words.forEach { word ->
            val startIndex = currentGameData.text.indexOf(word)
            val endIndex = startIndex + word.length

            spannableString.setSpan(object : ClickableSpan() {
                var isRight = false


                override fun onClick(p0: View) {

                    if (clickedWords.contains(word)) {
                        clickedWords.remove(word)
                    } else {
                        clickedWords.add(word)
                    }
                    gameText.invalidate()

                    isRight = clickedWords.sorted() == rightWords.sorted()

                    notifyActivity(isRight)
                }


                override fun updateDrawState(ds: android.text.TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(requireContext(), R.color.text_color)
                    if (clickedWords.contains(word)) {
                        ds.bgColor = ContextCompat.getColor(requireContext(), R.color.teal_200) // Farbe für angeklickte Wörter
                    } else {
                        ds.bgColor = Color.TRANSPARENT// Farbe für nicht angeklickte Wörter
                    }
                    ds.isUnderlineText = false
                }

            }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        }
        return spannableString
    }

    override fun handleUserSelection() {

    }

    override fun updateUI(isCorrect: Boolean) {

    }

    override fun parseGameData(gameData: GameData): PickWrongData {

        return try {
            val jsonArray = gameData.content
            var pickWrongData = PickWrongData()


            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val text = jsonObject.getString("text")


                    val pickWord = jsonObject.getString("word")

                    pickWrongData = PickWrongData(text, pickWord)
                }
            }
            pickWrongData
        } catch (e: Exception) {
            e.printStackTrace()
            PickWrongData()
        }
    }


    /**
     * Diese Klasse repräsentiert die Daten die eingefügt werden sollen
     *
     * @property text der Text
     * @property pickWord das Wort das gewählt werden soll
     * @property isCorrect gibt an ob das Wort in richtig ist
     * @property feedBackText der Text der angezeigt werden soll
     */
    data class PickWrongData(
        val text: String = "",
        val pickWord: String = "",
    )

}