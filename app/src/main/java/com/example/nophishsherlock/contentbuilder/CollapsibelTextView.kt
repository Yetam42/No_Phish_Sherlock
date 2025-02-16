package com.example.nophishsherlock.contentbuilder

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import com.example.nophishsherlock.R

class CollapsibelTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    private val textView: TextView
    private val contentTextView: TextView

    private var isCollapsed = false

    init {
        inflate(context, R.layout.context_layout, this)
        textView = findViewById(R.id.textView)
        contentTextView = findViewById(R.id.contentTextView)

        textView.isClickable = true

        textView.setOnClickListener {
            toggleVisibility()
        }
    }


    fun isCollapsed(): Boolean {
        return isCollapsed
    }

    fun setTextViewText(text: String) {
        textView.text = text
    }

    fun setContentTextViewText(text: String) {
        contentTextView.text = text
    }

    private fun toggleVisibility() {
        if (contentTextView.visibility == View.GONE) {
            showTextView2()
            isCollapsed = true
        } else {
            hideTextView2()
            isCollapsed = false
        }
    }

    private fun showTextView2() {
        contentTextView.visibility = View.VISIBLE
        // Fade-in animation
        ObjectAnimator.ofFloat(contentTextView, "alpha", 0f, 1f)
            .apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
    }

    private fun hideTextView2() {
        // Fade-out animation
        ObjectAnimator.ofFloat(contentTextView, "alpha", 1f, 0f)
            .apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
            .doOnEnd {
                contentTextView.visibility = View.GONE
            }
    }


}