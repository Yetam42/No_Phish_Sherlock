package com.example.no_phishing_yannick.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nophishsherlock.R

/**
 * Diese Klasse repräsentiert das EndFragment
 *
 */
class EndFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_endscreen, container, false)
    }
}