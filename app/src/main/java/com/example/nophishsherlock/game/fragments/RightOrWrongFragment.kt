package com.example.no_phishing_yannick.games.rightOrwrong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.no_phishing_yannick.games.helper.BaseGameFragment
import com.example.nophishsherlock.R
import com.example.nophishsherlock.data.GameData

/**
 * Diese Klasse repräsentiert das RightOrWrongFragment
 *
 */
class RightOrWrongFragment : BaseGameFragment() {

    //hier werden die Elemente der Activity initialisiert
    private lateinit var right: Button
    private lateinit var wrong: Button

    //hier wird ein leeres Datenobjekt erstellt
    val emptyData = RightOrWrongData("", "", false)

    //hier wird das aktuelle Datenobjekt gespeichert
    private var currentGameData: RightOrWrongData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rightorwrong, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gameText = view.findViewById<TextView>(R.id.gameText)
        context?.let { gameText.setTextColor(it.getColor(R.color.text_color)) }

        if (arguments != null) {
            val empfangeneFrage = arguments?.getParcelable<GameData>("game")
            currentGameData = empfangeneFrage?.let { parseGameData(it) } as RightOrWrongData?


            if (currentGameData != null) {
                gameText.text = currentGameData!!.statement
            }
        }

        handleUserSelection()
    }


    /**
     * Diese Klasse repräsentiert das Datenobjekt
     *
     * @property statement der Satz der angezeigt werden soll
     * @property word das relevante Wort für die Aussage
     * @property isCorrect gibt an ob die Aussage richtig ist
     */
    data class RightOrWrongData(
        val statement: String,
        val word: String,
        val isCorrect: Boolean?,
    )

    override fun handleUserSelection() {

        right = requireView().findViewById(R.id.right)
        wrong = requireView().findViewById(R.id.wrong)

        if (currentGameData?.isCorrect!!) {
            right.setOnClickListener {
                notifyActivity(true)
            }
            wrong.setOnClickListener {
                notifyActivity(false)
            }
        } else {
            right.setOnClickListener {
                notifyActivity(false)
            }
            wrong.setOnClickListener {
                notifyActivity(true)
            }


        }
    }

    override fun updateUI(isCorrect: Boolean) {

        if (isCorrect) {
            right.visibility = View.VISIBLE
            wrong.visibility = View.INVISIBLE
        } else {
            right.visibility = View.INVISIBLE
            wrong.visibility = View.VISIBLE
        }
    }


    override fun parseGameData(gameData: GameData): RightOrWrongData {

        return try {
            val jsonArray = gameData.content
            var rightOrWrongData = emptyData

            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val statement = jsonObject.getString("statement")
                    val word: String = jsonObject.getString("word")
                    val isCorrect = jsonObject.getBoolean("isCorrect")

                    rightOrWrongData = RightOrWrongData(statement, word, isCorrect)
                }
            }
            rightOrWrongData
        } catch (e: Exception) {
            e.printStackTrace()
            emptyData
        }
    }


}