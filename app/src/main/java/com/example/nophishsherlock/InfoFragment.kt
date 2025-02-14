package com.example.nophishsherlock

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.core.content.ContextCompat
import com.example.nophishsherlock.contentbuilder.FullscreenVideoActivity
import com.example.nophishsherlock.parser.JsonTextParser
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoFragment : BottomSheetDialogFragment() {

    private lateinit var gestureDetector: GestureDetector
    private val parser = JsonTextParser<Any>()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        //dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))


        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_information, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contentContainer = view.findViewById<LinearLayout>(R.id.contentContainer)
        val instruction = parser.parse(requireContext(), "1_chapter/1_chapter_long.json")

        for (jsonData in instruction) {
            // Abschnitt hinzufügen
            val sectionView = createTextView(
                requireContext(),
                text = jsonData.section,
                textSize = 14f,
            )
            Log.d("Info", "added section")
            contentContainer.addView(sectionView)

            // Titel hinzufügen
            val titleView = createTextView(
                requireContext(),
                text = jsonData.title,
                textSize = 20f,
            )
            contentContainer.addView(titleView)

            // Absätze hinzufügen
            if (jsonData.paragraphs != null) {
                for (paragraph in jsonData.paragraphs) {
                    val paragraphView = createTextView(
                        requireContext(),
                        text = paragraph,
                        textSize = 16f
                    )
                    contentContainer.addView(paragraphView)
                }
            }


            // Video hinzufügen (falls vorhanden)
            jsonData.media?.let { media ->
                if (media.type == "video") {
                    val videoView = createVideoView(requireContext(), media.source)
                    contentContainer.addView(videoView)

                    // Beschreibung des Videos hinzufügen
                    val mediaDescriptionView = createTextView(
                        requireContext(),
                        text = media.description,
                        textSize = 14f
                    )
                    contentContainer.addView(mediaDescriptionView)
                }
            }

            jsonData.media?.let { media ->
                if (media.type == "image") {
                    val imageView = createImageView(requireContext(), media.source)
                    contentContainer.addView(imageView)

                    // Beschreibung des Bildes hinzufügen
                    val mediaDescriptionView = createTextView(
                        requireContext(),
                        text = media.description,
                        textSize = 14f
                    )
                    contentContainer.addView(mediaDescriptionView)
                }
            }

            // Abtrennung hinzufügen (optional)
            val divider = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
                ).apply {
                    setMargins(0, 16, 0, 16)
                }
                setBackgroundColor(
                    ContextCompat.getColor(
                        this@InfoFragment.requireContext(),
                        android.R.color.darker_gray
                    )
                )
            }
            contentContainer.addView(divider)
        }

        // Schließen-Button
        view.findViewById<ImageButton>(R.id.closeButton).setOnClickListener {
            Log.d("InformationFragment", "Schließen-Button geklickt")

            dismiss()
        }
    }

    private fun createTextView(
        context: Context,
        text: String?,
        textSize: Float,
    ): TextView {
        return TextView(context).apply {
            Log.d("Info", "created Text")
            val htmlText = text
            this.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
            this.textSize = textSize
            setPadding(0, 8, 0, 8)
        }
    }

    private fun createImageView(context: Context, imageUrl: String): ImageView {
        return ImageView(context).apply {
            val imageId =
                resources.getIdentifier(imageUrl, "drawable", requireContext().packageName)
            setImageResource(imageId)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                400
            ).apply {
                setMargins(0, 10, 0, 20)
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    // Funktion zum Erstellen eines VideoViews
    private fun createVideoView(context: Context, videoUrl: String): VideoView {
        return VideoView(context).apply {
            val videoId = resources.getIdentifier(videoUrl, "raw", requireContext().packageName)
            val videoUri =
                Uri.parse("android.resource://${requireContext().packageName}/raw/$videoId")

            setVideoURI(videoUri)
            tag = videoUri


            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                400 // Höhe des VideoViews
            ).apply {
                setMargins(0, 16, 0, 16)
            }


            gestureDetector = GestureDetector(
                requireContext(),
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        openFullscreenVideo(this@apply)
                        return true
                    }

                    override fun onSingleTapUp(e: MotionEvent): Boolean {
                        if (this@apply.isPlaying) this@apply.pause() else this@apply.start()
                        return true
                    }
                })

            // Füge den OnTouchListener hinzu
            this.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }
        }
    }




    private fun openFullscreenVideo(videoView: VideoView) {
        val videoUri = videoView.tag as? Uri
        if (videoUri != null) {
            val intent = Intent(requireContext(), FullscreenVideoActivity::class.java)
            intent.putExtra("video_uri", videoUri.toString())
            intent.putExtra(
                "video_position",
                videoView.currentPosition
            ) // Aktuelle Position speichern
            startActivity(intent)
        }
    }


}