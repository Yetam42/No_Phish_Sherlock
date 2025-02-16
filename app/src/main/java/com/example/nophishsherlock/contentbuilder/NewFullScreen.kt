package com.example.nophishsherlock.contentbuilder

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.nophishsherlock.R

class NewFullScreen : AppCompatActivity() {
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_fullscreen)

        // Hide system UI for fullscreen
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        // Create PlayerView dynamically
        playerView = findViewById(R.id.player_view)


        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        // Get video URI & position from Intent
        val videoUri = intent.getStringExtra("video_uri") ?: return
        val startPosition = intent.getLongExtra("video_position", 0L)

        // Load Video
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUri))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.seekTo(startPosition)
        player.play()

        // Exit fullscreen on back button
        playerView.setFullscreenButtonClickListener {
            exitFullscreen()
        }
    }

    private fun exitFullscreen() {
        val resultIntent = Intent()
        resultIntent.putExtra("video_position", player.currentPosition)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}