package com.example.nophishsherlock.game.fragments

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.nophishsherlock.R
import com.example.nophishsherlock.game.dragAndDropUtil.BaseDragAndDropFragment
import com.google.android.material.color.MaterialColors

class SimpleDragAndDropFragment : BaseDragAndDropFragment() {
    override fun createDragItemViews() {
        val textViewList = mutableListOf<TextView>()

        for (values in currentGameData.itemToTargetMap.values) {
            for (item in values) {
                val dragItemTextView = TextView(requireContext()).apply {
                    text = item
                    setTextColor(resources.getColor(R.color.text_color))

                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 16, 8, 16)
                    }
                    textSize = 16f
                    setBackgroundColor(
                        MaterialColors.getColor(
                            this,
                            com.google.android.material.R.attr.colorSecondary
                        )
                    )

                    setOnTouchListener(createTouchItemListener())
                }
                textViewList.add(dragItemTextView)
            }
        }

        textViewList.shuffle()

        for (textView in textViewList) {
            draggableItemsContainer.addView(textView)
        }
    }

    override fun getItemsFromView(view: ViewGroup): List<String> {
        val items = mutableListOf<String>()
        for (i in 0 until view.childCount) {
            val child = view.getChildAt(i)
            val item = extractString(child)
            if (item != null) {
                items.add(item.toString())
            }
        }
        return items

    }

    override fun extractString(view: View): CharSequence? {
        val currentTextView = view as? TextView
        val text = currentTextView?.text

        if (text == dropZone1Text.text || text == dropZone2Text.text) {
            return null
        }

        return text
    }

}