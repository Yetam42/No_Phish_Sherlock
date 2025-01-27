package com.example.nophishsherlock.contentbuilder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.nophishsherlock.R


/**
 * Diese Klasse repräsentiert die FullscreenVideoActivity
 * sie wird benutzt um das Video im Vollbild zu sehen
 *
 */
class FullscreenVideoActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_fullscreen)

        videoView = findViewById(R.id.video_view)

        val videoUri = intent.getStringExtra("video_uri")
        val position = intent.getIntExtra("video_position", 0)

        Log.d("FullscreenVideoActivity", "Received video URI: $videoUri")
        videoUri?.let {
            val uri = Uri.parse(it)

            // MediaController erstellen und an VideoView binden

            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)

            videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = false
                videoView.seekTo(position) // Zur vorherigen Position springen
                mediaController.show() // MediaController anzeigen
                mediaPlayer.start() // Video abspielen
                videoView.start()
            }

            videoView.setVideoURI(uri)


        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Zurück zum normalen Modus
        setResult(RESULT_OK, Intent().apply {
            putExtra("video_position", videoView.currentPosition)
        })

    }

    override fun onResume() {
        super.onResume()
        videoView.start()
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback()
    }

}