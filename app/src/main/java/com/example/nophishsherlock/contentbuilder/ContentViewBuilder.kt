package com.example.nophishsherlock.contentbuilder

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.VideoView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.nophishsherlock.data.ImageText
import com.example.nophishsherlock.data.JsonTextData
import com.google.android.material.textview.MaterialTextView


/**
 * Diese Klasse repräsentiert das ContentViewBuilder
 * Dieser baut die Views für das Hauptmenü zusammen
 *
 * @property context das Kontext in dem die Views erstellt werden sollen
 */
class ContentViewBuilder(private val context: Context) {

    private lateinit var gestureDetector: GestureDetector

    /**
     * diese Funktion erstellt die Views
     *
     * @param textDataList
     * @return
     */
    fun buildContent(textDataList: List<JsonTextData>): List<View> {

        val views = mutableListOf<View>()

        for (textData in textDataList) {
            val scrollView = ScrollView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
                )
            }

            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(32, 16, 32, 16)
            }

            Log.d("ContentViewBuilder", "Section: ${textData.section}")
            if (textData.section != null) {
                layout.addView(createTextView(textData.section, 14f))

            }


            //layout.addView(createTextView(instruction.title, 20f))   würde titel hinzufügen
            if (textData.paragraphs != null) {
                for (paragraph in textData.paragraphs) {
                    layout.addView(createTextView(paragraph, 16f))
                }
            }


            textData.media?.let { media ->
                when (media.type) {
                    "video" -> {
                        layout.addView(createVideo(media.source))
                        layout.addView(createTextView(media.description, 14f))
                    }

                    "image" -> {
                        layout.addView(createImageView(media.source))
                        layout.addView(createTextView(media.description, 14f))
                    }

                    else -> {}
                }
            }

            textData.imageText?.let { imageText ->
                layout.addView(createTextViewWithImage(imageText, 16f))
            }


            scrollView.addView(layout)
            views.add(scrollView)

        }

        return views
    }


    /**
     * Diese Funktion erstellt den TextView
     *
     * @param text der Text den eingefügt werden soll
     * @param textSize die Größe des Textes
     * @return TextView mit Text
     */
    private fun createTextView(text: String?, textSize: Float): TextView {
        if (text == null) return MaterialTextView(context)
        return MaterialTextView(context).apply {
            this.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
            this.textSize = textSize
            setPadding(0, 0, 0, 8)
        }
    }

    private fun createTextViewWithImage(text: ImageText, textSize: Float): LinearLayout {
        if (text.text == null) return LinearLayout(context)
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val layoutParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        layout.layoutParams = layoutParam

        val textParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f)

        val textView = createTextView(text.text.toString(), textSize)
        textView.layoutParams = textParams

        val imageParams =
            LinearLayout.LayoutParams(0, textView.maxHeight, 1f)

        val imageView = createImageView(text.imageSource)
        imageView.layoutParams = imageParams
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

        if (text.imageFirst) {

            layout.addView(imageView)
            layout.addView(textView)
        } else {
            layout.addView(textView)
            layout.addView(imageView)
        }

        return layout

    }

    /**
     * Diese Funktion erstellt die ImageView
     *
     * @param imageUrl das Bild was eingefügt werden soll
     * @return ImageView mit Bild
     */
    private fun createImageView(imageUrl: String): ImageView {
        val imageId = context.resources.getIdentifier(imageUrl, "drawable", context.packageName)
        val imageView = ImageView(context)
        imageView.setImageResource(imageId)
        imageView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 400
        ).apply {
            //setMargins(0, 8, 0, 0)
        }
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.tag = imageUrl
        return imageView
    }


    private fun createVideo(videoUrl: String): PlayerView {
        Log.d("VideoView", "Creating video view for URL: $videoUrl")
        val videoUri = Uri.parse("android.resource://${context.packageName}/raw/example")
        Log.d("VideoView", "Video URI: $videoUri")

        var isFullscreen = false

        // Create ExoPlayer instance
        val player = ExoPlayer.Builder(context).build()

        // Prepare MediaItem
        val mediaItem = MediaItem.fromUri(videoUri)


        // Create StyledPlayerView
        val playerView = PlayerView(context)


        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = false

        playerView.player = player
        playerView.useController = true // Show media controls


        playerView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 600
        ).apply {
            setMargins(0, 16, 0, 16)
        }

        playerView.setFullscreenButtonClickListener {
            openFullscreen(player)

        }

        return playerView
    }


    private fun openFullscreen(player: ExoPlayer) {
        val intent = Intent(context, NewFullScreen::class.java)
        intent.putExtra("video_uri", "android.resource://${context.packageName}/raw/example")
        intent.putExtra("video_position", player.currentPosition)
        context.startActivity(intent)
    }


    /**
     * Diese Funktion erstellt den VideoView
     *
     * @param videoUrl das Video was eingefügt werden soll
     * @return VideoView mit Video
     */
    private fun createVideoView(videoUrl: String): VideoView {
        Log.d("VideoView", "Creating video view for URL: $videoUrl")
        val videoId = context.resources.getIdentifier(videoUrl, "raw", context.packageName)
        val videoUri = Uri.parse("android.resource://${context.packageName}/raw/$videoId")
        Log.d("VideoView", "Video URI: $videoUri")
        val videoView = VideoView(context)
        videoView.setVideoURI(videoUri)
        videoView.tag = videoUri


        videoView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 400
        ).apply {
            setMargins(0, 16, 0, 16)

        }


        gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    openFullscreenVideo(videoView)
                    return true
                }

                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    if (videoView.isPlaying) videoView.pause() else videoView.start()
                    return true
                }
            })

        videoView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        return videoView
    }

    /**
     * öffnet das Video im Vollbild
     *
     * @param videoView das Video was geöffnet werden soll
     */
    private fun openFullscreenVideo(videoView: VideoView) {
        val videoUri = videoView.tag as? Uri
        if (videoUri != null) {
            val intent = Intent(context, FullscreenVideoActivity::class.java)
            intent.putExtra("video_uri", videoUri.toString())
            intent.putExtra("video_position", videoView.currentPosition)
            context.startActivity(intent)
        }
    }
}