package com.example.nophishsherlock.contentbuilder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.nophishsherlock.data.JsonTextData
import com.example.nophishsherlock.databinding.FragmentLongscreenBinding

class ChapterFragment : Fragment() {

    private var gameViewButton: Button? = null
    private lateinit var contentContainer: LinearLayout
    private lateinit var layoutParams: LinearLayout.LayoutParams

    private lateinit var binding: FragmentLongscreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val view = inflater.inflate(R.layout.fragment_longscreen, container, false)
//        contentContainer = view.findViewById(R.id.fragment_container)
//        return view
        binding = FragmentLongscreenBinding.inflate(inflater, container, false)
        contentContainer = binding.fragmentContainer
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val views = requireArguments().getParcelableArrayList<JsonTextData>("views")
            val contentViewBuilder = ContentViewBuilder(requireContext())
            val content = views?.let { contentViewBuilder.buildContent(it) }

            if (content != null) {
                for (c in content) {
                    Log.d("LongChapterFragment", "Adding $c to content container")

                    contentContainer.addView(c)
                }
            }
        }

        if (gameViewButton != null) {

            if (gameViewButton!!.parent != null) {
                removeFromParent(gameViewButton!!)
            }

            contentContainer.addView(gameViewButton, layoutParams)
        }

    }


    fun setGameViewButton(button: Button, layoutParams: LinearLayout.LayoutParams) {

        gameViewButton = button
        this.layoutParams = layoutParams

    }

    private fun removeFromParent(button: Button) {
        Log.d("LongChapterFragment", "Removing button from parent")
        if (button.parent != null) {
            val parent = button.parent as ViewGroup
            parent.removeView(button)
            Log.d("LongChapterFragment", "Button removed from parent")
        }
    }
}