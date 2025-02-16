package com.example.nophishsherlock.game.fragments

import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.no_phishing_yannick.games.helper.BaseGameFragment
import com.example.nophishsherlock.R
import com.example.nophishsherlock.contentbuilder.GameAnswerButton
import com.example.nophishsherlock.data.GameData

class HiddenLinkFragment : BaseGameFragment() {

    lateinit var hiddenLinkFragment: ViewGroup
    lateinit var gameText: TextView
    private var currentGameData = HiddenLink()
    lateinit var right: GameAnswerButton
    lateinit var wrong: GameAnswerButton
    lateinit var popupWindow: PopupWindow

    var hasSeenHiddenLink: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rightorwrong, container, false)
        gameText = view.findViewById(R.id.gameText)
        hiddenLinkFragment = view as ViewGroup

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val empfangeneFrage = arguments?.getParcelable<GameData>("game")
            currentGameData = empfangeneFrage?.let { parseGameData(it) }!!

            gameText.movementMethod = LinkMovementMethod.getInstance()
            gameText.text = spannableString(currentGameData)
            context?.let { gameText.setTextColor(it.getColor(R.color.text_color)) }


//            gameText.setTextColor(android.graphics.Color.BLUE)
//            gameText.isClickable = true


            val inflater: LayoutInflater = layoutInflater
            val tooltipView =
                inflater.inflate(R.layout.tooltip_layout, null) // Layout für den Tooltip
            val textViewInPopup = tooltipView.findViewById<TextView>(R.id.tooltip_text)
            textViewInPopup.text = currentGameData.trueLink
            popupWindow = PopupWindow(
                tooltipView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )


            right = view.findViewById(R.id.right)
            wrong = view.findViewById(R.id.wrong)

        }
    }

    override fun onPause() {
        super.onPause()
        popupWindow.dismiss()
    }


    private fun spannableString(currentGameData: HiddenLink): SpannableString {
        val spannableString = SpannableString(currentGameData.shownText)

        val startIndex = currentGameData.shownText.indexOf(currentGameData.shownLink)
        val endIndex = startIndex + currentGameData.shownLink.length

        spannableString.setSpan(
            object : ClickableSpan() {
                override fun onClick(p0: View) {
                    Log.d("HiddenLinkFragment", "Link clicked")
                    handleUserSelection()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(requireContext(), R.color.teal_200)
                    ds.isUnderlineText = true
                }
            }, startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    override fun handleUserSelection() {

        Log.d("HiddenLinkFragment", "handleUserSelection called")
        popupWindow.showAsDropDown(gameText)
        hasSeenHiddenLink = true


        hiddenLinkFragment.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN && popupWindow.isShowing) {
                popupWindow.dismiss()
                view.performClick()
                true
            } else {
                false
            }
        }

        if (currentGameData.isCorrect) {
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
                right.deselectButton()
                wrong.selectButton(false)
            }
        }


    }


    override fun updateUI(isCorrect: Boolean) {
    }

    override fun parseGameData(gameData: GameData): HiddenLink {
        return try {
            val jsonArray = gameData.content
            var hiddenLink = HiddenLink()
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val shownText = jsonObject.getString("shownText")
                    val shownLink = jsonObject.getString("shownLink")
                    val trueLink = jsonObject.getString("trueLink")
                    val isCorrect = jsonObject.getBoolean("isCorrect")
                    hiddenLink = HiddenLink(shownText, shownLink, trueLink, isCorrect)
                }
            }
            hiddenLink
        } catch (e: Exception) {
            e.printStackTrace()
            HiddenLink()
        }
    }


    data class HiddenLink(
        val shownText: String = "",
        val shownLink: String = "",
        val trueLink: String = "",
        val isCorrect: Boolean = false,
    )
}