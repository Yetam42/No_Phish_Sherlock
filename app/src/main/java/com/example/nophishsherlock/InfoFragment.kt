package com.example.nophishsherlock

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson


class InfoFragment : BottomSheetDialogFragment() {



    private var infoText: String? = null
    private var contextText: CharSequence? = null
    private val tutorialLocation = "info/tutorial.json"

    companion object {
        private const val ARG_INFO_TEXT = "infoText"
        private const val ARG_CONTEXT_TEXT = "contextText"

        fun newInstance(text: String): InfoFragment {
            val fragment = InfoFragment()
            val args = Bundle()
            args.putString(ARG_INFO_TEXT, text)
            fragment.arguments = args
            return fragment
        }

        fun newContextInstance(text: CharSequence): InfoFragment {
            val fragment = InfoFragment()
            val args = Bundle()
            args.putCharSequence(ARG_CONTEXT_TEXT, text)
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
            contextText = it.getString(ARG_CONTEXT_TEXT)

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


        if (infoText != null) {
            addInfoText(contentContainer)
        }
        if (contextText != null) {
            addContextText(contentContainer)
        }


    }

    private fun addInfoText(contentContainer: LinearLayout) {
        val tutorialMap = getTutorialText()

        var contentString = ""
        for (line in tutorialMap[infoText]!!) {
            contentString += "$line<br><br>"
        }

        val contentTextView = createTextView(contentString)
        contentContainer.addView(contentTextView)
    }

    private fun addContextText(contentContainer: LinearLayout) {
        val contextTextView = createTextView(contextText!!)
        contentContainer.addView(contextTextView)
    }

    private fun createTextView(text: CharSequence): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            this.setTextColor(resources.getColor(R.color.text_color))
            this.textSize = 16f
        }
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
                for (tutorial in tutorialData.tutorial!!) {
                    val gameName = tutorial.game
                    val contentList = mutableListOf<String>()
                    for (value in tutorial.content!!) {
                        contentList.add(value)
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