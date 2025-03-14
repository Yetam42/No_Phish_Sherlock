package com.example.nophishsherlock.contentbuilder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.nophishsherlock.data.JsonTextData
import com.example.nophishsherlock.databinding.FragmentLongscreenBinding

class ChapterFragment : Fragment() {

    private var gameViewButton: Button? = null
    private lateinit var contentContainer: LinearLayout
    private lateinit var layoutParams: LinearLayout.LayoutParams

    private lateinit var binding: FragmentLongscreenBinding

    private var playerView: PlayerView? = null
    private var exoPlayer: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
                    contentContainer.addView(c)
                }
            }
        }

        playerView = findPlayerView(contentContainer)
        if (playerView != null) {
            exoPlayer = playerView?.player as? ExoPlayer
        }


        if (gameViewButton != null) {

            if (gameViewButton!!.parent != null) {
                removeFromParent(gameViewButton!!)
            }

            contentContainer.addView(gameViewButton, layoutParams)
        }

    }

    private fun findPlayerView(viewGroup: ViewGroup): PlayerView? {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            Log.d("VIDEO", "Checking child: $child")
            if (child is PlayerView) {
                return child
            } else if (child is ViewGroup) {
                val found = findPlayerView(child)
                if (found != null) {
                    return found
                }
            }
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()

        playerView?.player = null
        exoPlayer?.release()
        exoPlayer = null
        playerView = null
    }



    fun setGameViewButton(button: Button, layoutParams: LinearLayout.LayoutParams) {

        gameViewButton = button
        this.layoutParams = layoutParams

    }

    private fun removeFromParent(button: Button) {
        if (button.parent != null) {
            val parent = button.parent as ViewGroup
            parent.removeView(button)
        }
    }
}