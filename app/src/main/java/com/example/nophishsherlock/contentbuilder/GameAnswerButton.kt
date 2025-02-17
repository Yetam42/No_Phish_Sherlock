package com.example.nophishsherlock.contentbuilder

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors

class GameAnswerButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialButtonStyle
) : MaterialButton(context, attrs, defStyleAttr) {

    private val selectedScale = 1.3f
    private val defaultScale = 1f
    private val animationDuration = 200L


    fun selectButton(isCorrect: Boolean) {
        animate().scaleX(selectedScale).scaleY(selectedScale).setDuration(animationDuration).start()
        elevation = 10f


        val buttonColor = if (isCorrect) Color.GREEN else Color.RED
        setBackgroundColor(buttonColor)
    }

    fun deselectButton() {
        animate().scaleX(defaultScale).scaleY(defaultScale).setDuration(animationDuration).start()
        elevation = 0f
        setBackgroundColor(
            MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary)
        )
    }
}