package com.example.no_phishing_yannick.games

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nophishsherlock.R
import com.example.nophishsherlock.game.helper.GameFragmentListener
import com.example.nophishsherlock.game.helper.GameViewModel
import com.google.android.material.button.MaterialButton

/**
 * Diese Klasse repräsentiert das EndFragment
 *
 */
class EndFragment : Fragment() {
    private var listener: GameFragmentListener? = null
    private val MESSAGE = "Du hast Lektion %s erfolgreich abgeschlossen."
    lateinit var viewModel: GameViewModel


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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_endscreen, container, false)

        viewModel = ViewModelProvider(this)[GameViewModel::class.java]


        val menuButton = view.findViewById<MaterialButton>(R.id.menu)
        val nextButton = view.findViewById<MaterialButton>(R.id.next)
        val message = view.findViewById<TextView>(R.id.message)

        message.text = String.format(MESSAGE, viewModel.currentChapter.value)

        menuButton.setOnClickListener {
            val isCompleted = true
            listener?.onGameFinished(isCompleted)
        }

        nextButton.setOnClickListener {
            listener?.onNextChapter()
        }


        return view
    }



}