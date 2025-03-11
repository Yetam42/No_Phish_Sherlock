package com.example.nophishsherlock.contentbuilder

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.nophishsherlock.R
import com.example.nophishsherlock.data.ImageText
import com.example.nophishsherlock.data.JsonTextData
import com.example.nophishsherlock.data.ParagraphWithImage
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
            Log.d("Creating", "Creating view for text data: $textData")
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

            if (textData.section != null) {
                layout.addView(createTextView(textData.section, 14f))

            }


            if (textData.paragraphs != null) {
                for (paragraph in textData.paragraphs) {
                    Log.d("Long", paragraph)
                    layout.addView(createTextView(paragraph, 16f))
                }
            }


            textData.imageText?.let { imageText ->
                layout.addView(createTextViewWithImage(imageText, 16f))
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



            textData.paragraphWithMedia?.let { paragraphWithMedia ->
                layout.addView(createParagraphWithMedia(paragraphWithMedia))
            }

            views.add(layout)
        }

        return views
    }


    private fun createParagraphWithMedia(paragraphWithMedia: List<ParagraphWithImage>): LinearLayout {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        val layoutParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        layout.layoutParams = layoutParam


        for (textData in paragraphWithMedia) {

            if (textData.paragraphs != null) {
                for (paragraph in textData.paragraphs) {
                    layout.addView(createTextView(paragraph, 16f))
                }
            }

            if (textData.media2 == null) {
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
            } else {
                val imageLayout = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                }

                val imageParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )

                imageLayout.layoutParams = imageParams

                val image1 = textData.media
                val image2 = textData.media2



                if (image1 != null) {
                    imageLayout.addView(createImageView(image1.source))
                    //imageLayout.addView(createTextView(image1.description, 14f))
                }

                if (image2 != null) {
                    imageLayout.addView(createImageView(image2.source))
                    //imageLayout.addView(createTextView(image2.source, 14f))
                }

                layout.addView(imageLayout)
            }
        }


        return layout
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

            if (text.contains("href")) {
                this.movementMethod = LinkMovementMethod.getInstance()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.tooltipText = "Hier downloaden"
                }
            }

        }
    }

    private fun openFullscreenImageView(imageUrl: String) {
        val intent = Intent(context, FullscreenImageActivity::class.java)
        intent.putExtra("image_url", imageUrl)
        context.startActivity(intent)
    }

    class FullscreenImageActivity : AppCompatActivity() {

        private lateinit var scaleGestureDetector: ScaleGestureDetector
        private var scaleFactor = 1.0f
        private lateinit var imageView: ImageView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.media_fullscreen)

            val imageUrl = intent.getStringExtra("image_url")
            imageView = findViewById(R.id.imageView)
            imageView.visibility = View.VISIBLE

            if (imageUrl != null) {
                val imageId = resources.getIdentifier(imageUrl, "drawable", packageName)
                Log.d("FullscreenImage", "Image ID: $imageId") // Add this line
                if (imageId != 0) {
                    imageView.setImageResource(imageId)
                } else {
                    Log.e("FullscreenImage", "Resource not found for: $imageUrl")
                }
            }

            scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        }

        override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
            scaleGestureDetector.onTouchEvent(motionEvent)
            return true
        }

        private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
                scaleFactor *= scaleGestureDetector.scaleFactor
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f)) // Limit zoom scale
                imageView.scaleX = scaleFactor
                imageView.scaleY = scaleFactor
                return true
            }
        }

    }

    private fun setupDoubleTapListener(imageView: ImageView, imageUrl: String) {
        val gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: android.view.MotionEvent): Boolean {
                    // Open fullscreen view when double-tapped
                    Log.d("DoubleTap", "Double tap detected")
                    openFullscreenImageView(imageUrl)
                    return true
                }
            })

        imageView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            Log.d("DoubleTap", "Touch event detected")
            // Returning false here lets the system also handle the touch event,
            // so it's not blocked for normal interactions like scrolling
            true
        }

    }


    private fun createTextViewWithImage(text: ImageText, textSize: Float): LinearLayout {
        if (text.text == null) return LinearLayout(context)
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val textParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f)


        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }
        for (line in text.text) {
            val textView = createTextView(line, textSize)
            textLayout.addView(textView)
        }

        textLayout.layoutParams = textParams


        //Image wird erstellt
        val imageParams =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)

        val imageView = createImageView(text.imageSource)
        imageView.layoutParams = imageParams
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

        imageView.setPadding(10, 0, 10, 0)

        if (text.description != null) {
            val description = createTextView(text.description, 14f)


            if (text.imageFirst) {
                layout.addView(imageView)
                layout.addView(description)
                layout.addView(textLayout)
            } else {
                layout.addView(textLayout)
                layout.addView(imageView)
                layout.addView(description)
            }
        } else {
            if (text.imageFirst) {
                layout.addView(imageView)
                layout.addView(textLayout)
            } else {
                layout.addView(textLayout)
                layout.addView(imageView)
            }
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
        Log.d("ImageView", "Creating image view for URL: $imageUrl")
        val imageId = context.resources.getIdentifier(imageUrl, "drawable", context.packageName)
        val imageView = ImageView(context)
        imageView.setImageResource(imageId)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, // Set initial width to 0
            400
        )
        layoutParams.weight = 1f // Assign equal weight to each image

        imageView.layoutParams = layoutParams
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.tag = imageUrl

        setupDoubleTapListener(imageView, imageUrl)
        return imageView
    }


    private fun createVideo(videoUrl: String): PlayerView {
        val videoUri = Uri.parse("android.resource://${context.packageName}/raw/example")

        // Create ExoPlayer instance
        val player = ExoPlayer.Builder(context).build()

        // Prepare MediaItem
        val mediaItem = MediaItem.fromUri(videoUri)


        // Create StyledPlayerView
        val playerView = PlayerView(context)


        player.volume = 0f

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
            player.stop()
            openFullscreen(player, videoUri.toString())


        }

        return playerView
    }


    private fun openFullscreen(player: ExoPlayer, videoUri: String) {
        val intent = Intent(context, NewFullScreen::class.java)
        intent.putExtra("video_uri", videoUri)
        intent.putExtra("video_position", player.currentPosition)

        context.startActivity(intent)
    }


}