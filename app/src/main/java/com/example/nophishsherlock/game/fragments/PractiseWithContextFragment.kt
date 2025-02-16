package com.example.nophishsherlock.game.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import com.example.no_phishing_yannick.games.helper.BaseGameFragment
import com.example.nophishsherlock.R
import com.example.nophishsherlock.contentbuilder.CollapsibelTextView
import com.example.nophishsherlock.contentbuilder.GameAnswerButton
import com.example.nophishsherlock.data.GameData
import com.google.android.material.card.MaterialCardView
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class PractiseWithContextFragment : BaseGameFragment() {

    private lateinit var image: ImageView
    private lateinit var right: GameAnswerButton
    private lateinit var wrong: GameAnswerButton


    private lateinit var contextButton: Button

    private lateinit var sidesheetContainer: FrameLayout
    private lateinit var sidesheetText: TextView

    private var currentGameData = PractiseWithContextData()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_taskwithcontext, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image = view.findViewById(R.id.image)
        right = view.findViewById(R.id.right)
        wrong = view.findViewById(R.id.wrong)
        contextButton = view.findViewById(R.id.kontextButton)
        sidesheetContainer = view.findViewById(R.id.sidesheetContainer)
        sidesheetText = view.findViewById(R.id.sidesheetText)

        if (arguments != null) {
            val empfangeneFrage = arguments?.getParcelable<GameData>("game")
            currentGameData =
                (empfangeneFrage?.let { parseGameData(it) } as PractiseWithContextData?)!!


            val imageId =
                context?.resources?.getIdentifier(
                    currentGameData.imageSource,
                    "drawable",
                    context?.packageName
                )


            Log.d("imageId", currentGameData.imageSource)


            sidesheetText.text = currentGameData.context

            if (imageId != null) {
                image.setImageResource(imageId)

                Log.d("PractiseWithContextFragment", "Build Image: ${imageId}")
            }



            contextButton.setOnClickListener {
                toggleSidesheetVisibility()
            }

            sidesheetContainer.isClickable = true
            sidesheetContainer.setOnClickListener {
                toggleSidesheetVisibility()
            }



            handleUserSelection()
            updateUI(currentGameData.isCorrect)
        }
    }

    private fun toggleSidesheetVisibility() {
        if (sidesheetContainer.visibility == View.GONE) {
            sidesheetContainer.bringToFront()
            showSidesheet()

        } else {
            hideSidesheet()
        }
    }


    private fun showSidesheet() {
        sidesheetContainer.visibility = View.VISIBLE
        // Optionale Animation für das Einblenden (Beispiel mit Fade-In):
        sidesheetContainer.animate()
            .alpha(1f)
            .setDuration(200) // Animationsdauer in Millisekunden
            .start()
    }

    private fun hideSidesheet() {
        // Optionale Animation für das Ausblenden (Beispiel mit Fade-Out):
        sidesheetContainer.animate()
            .alpha(0f)
            .setDuration(200) // Animationsdauer in Millisekunden
            .withEndAction { // Nach der Animation:
                sidesheetContainer.visibility = View.GONE // Tatsächlich ausblenden
            }
            .start()
    }

    override fun handleUserSelection() {

    }

    override fun updateUI(isCorrect: Boolean) {
        if (isCorrect) {
            right.setOnClickListener {
                notifyActivity(true)
                right.selectButton(true)
                wrong.deselectButton()
            }
            wrong.setOnClickListener {
                notifyActivity(false)
                wrong.selectButton(false)
                right.deselectButton()
            }
        } else {
            right.setOnClickListener {
                notifyActivity(false)
                right.selectButton(true)
                wrong.deselectButton()
            }
            wrong.setOnClickListener {
                notifyActivity(true)
                wrong.deselectButton()
                wrong.selectButton(false)
            }
        }
    }

    override fun parseGameData(gameData: GameData): PractiseWithContextData {
        return try {
            val jsonArray = gameData.content
            var practiseWithContextData = PractiseWithContextData()
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val imageSource = jsonObject.getString("imageSource")
                    val context = jsonObject.getString("context")
                    val isCorrect = jsonObject.getBoolean("isCorrect")
                    practiseWithContextData =
                        PractiseWithContextData(imageSource, context, isCorrect)
                }
            }

            practiseWithContextData
        } catch (e: Exception) {
            e.printStackTrace()
            PractiseWithContextData()
        }
    }

    data class PractiseWithContextData(
        val imageSource: String = "",
        val context: String = "",
        val isCorrect: Boolean = false
    )
}

