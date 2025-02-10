package com.example.nophishsherlock.game.helper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nophishsherlock.MainActivity
import com.example.nophishsherlock.R
import com.example.nophishsherlock.contentbuilder.ContentViewBuilder
import com.example.nophishsherlock.data.JsonTextData

class LongChapterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_longscreen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val views = requireArguments().getParcelableArrayList<JsonTextData>("views")
            val contentViewBuilder = ContentViewBuilder(requireContext())
            val content = views?.let { contentViewBuilder.buildContent(it) }

            if (content != null) {
                for (c in content) {
                    view.findViewById<ViewGroup>(R.id.fragment_container).addView(c)
                }
            }
        }

    }

    fun addView(view: View) {
        view?.let { fragmentView ->
            val layout = fragmentView.findViewById<ViewGroup>(R.id.fragment_container)
            layout.addView(view)
        }

    }
}