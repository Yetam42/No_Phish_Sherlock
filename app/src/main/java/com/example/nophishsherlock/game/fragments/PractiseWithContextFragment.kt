package com.example.nophishsherlock.game.fragments

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.no_phishing_yannick.games.helper.BaseGameFragment
import com.example.nophishsherlock.InfoFragment
import com.example.nophishsherlock.R
import com.example.nophishsherlock.contentbuilder.GameAnswerButton
import com.example.nophishsherlock.data.GameData

class PractiseWithContextFragment : BaseGameFragment() {

    private lateinit var image: ImageView
    private lateinit var right: GameAnswerButton
    private lateinit var wrong: GameAnswerButton


    private lateinit var contextButton: Button


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
        contextButton = view.findViewById(R.id.contextButton)

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





            if (imageId != null) {
                image.setImageResource(imageId)
            }


            val contextText = getContextString(currentGameData.context)

            contextButton.setOnClickListener {
                val contextInfoDialog = InfoFragment.newContextInstance(contextText)
                contextInfoDialog.show(childFragmentManager, "InformationFragment")

            }




            handleUserSelection()
            updateUI(currentGameData.isCorrect)
        }
    }


    override fun handleUserSelection() {

    }

    private fun getContextString(resourceName: String): CharSequence {
        val contextText = try {
            val resourceName = currentGameData.context

            val resources = resources

            val resourceId = resources.getIdentifier(
                resourceName, "string",
                context?.packageName
            )

            if (resourceId == 0) {
                Log.e("Context Error", "Resource not found: $resourceName")
                "Error: Context not found"
            } else {
                getText(resourceId)
            }

        } catch (e: Resources.NotFoundException) {
            "Error"
        } catch (e: Exception) {
            "Error"
        }

        return contextText
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

