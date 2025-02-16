package com.example.nophishsherlock

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson


class InfoFragment : BottomSheetDialogFragment() {

//    private lateinit var gestureDetector: GestureDetector
//    private val parser = JsonTextParser<Any>()


    private var infoText: String? = null
    private val tutorialLocation = "info/tutorial.json"

    companion object {
        private const val ARG_INFO_TEXT = "infoText"

        // Static factory method to create a new instance of InfoFragment with text
        fun newInstance(text: String): InfoFragment {
            val fragment = InfoFragment()
            val args = Bundle()
            args.putString(ARG_INFO_TEXT, text)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )


        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_information, container, false)

        arguments?.let {
            infoText = it.getString(ARG_INFO_TEXT)
        }
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeButton = view.findViewById<ImageButton>(R.id.closeButton)
        closeButton.setOnClickListener {
            dismiss()
        }
        val contentContainer = view.findViewById<LinearLayout>(R.id.contentContainer)


//        for (i in 0 until 100) {
//            val firstText = TextView(requireContext()).apply {
//                text = infoText
//            }
//
//            contentContainer.addView(firstText)
//        }


        Log.d("InfoFragment", "infoText: $infoText")
        val tutorialMap = getTutorialText()

        var contentString = ""
        for (line in tutorialMap[infoText]!!) {
            contentString += "$line<br><br>"
        }

        val contentTextView = createTextView(contentString)
        contentContainer.addView(contentTextView)


    }

    private fun createTextView(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
            this.setTextColor(resources.getColor(R.color.text_color))
            this.textSize = 16f
        }
    }

    private fun getTutorialText(): Map<String, List<String>> {
        val gson = Gson()
        return try {
            val tutorialMap = mutableMapOf<String, List<String>>()
            val jsonString = requireContext().assets.open(tutorialLocation).bufferedReader()
                .use { it.readText() }
            val tutorialData: TutorialData = gson.fromJson(jsonString, TutorialData::class.java)
            if (tutorialData.tutorial != null) {
                Log.d("TutorialData", "Tutorial data found: $tutorialData")
                for (tutorial in tutorialData.tutorial!!) {
                    val gameName = tutorial.game
                    val contentList = mutableListOf<String>()
                    for (value in tutorial.content!!) {
                        contentList.add(value)
                        Log.d("TutorialData", "Added value: $value")
                    }
                    tutorialMap[gameName!!] = contentList
                }
            }
            tutorialMap
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    class GameTutorial {
        var game: String? = null
        var content: List<String>? = null
    }

    class TutorialData {
        var tutorial: List<GameTutorial>? = null
    }


}