package com.example.nophishsherlock.game.fragments

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.nophishsherlock.R
import com.example.nophishsherlock.game.dragAndDropUtil.BaseDragAndDropFragment
import com.google.android.material.color.MaterialColors

class LayoutDragAndDropFragment : BaseDragAndDropFragment() {

    private val ORGANIZATION = "Organisation: "
    private val WEBADRESSE = "Webadresse: "


    override fun createDragItemViews() {
        val layoutList = mutableListOf<LinearLayout>()
        for (values in currentGameData.itemToTargetMap.values) {
            for (item in values) {
                val layout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(8, 16, 8, 16) }
                    setBackgroundColor(Color.LTGRAY)

                    val webadresseTextView = createTextView(WEBADRESSE, item)

                    val organizationString = currentGameData.itemToOrganisation[item]

                    if (organizationString != null) {
                        val organisationTextView = createTextView(ORGANIZATION, organizationString)
                        addView(organisationTextView)
                    }
                    addView(webadresseTextView)

                    setBackgroundColor(
                        MaterialColors.getColor(
                            this,
                            com.google.android.material.R.attr.colorSecondary
                        )
                    )

                    setOnTouchListener(createTouchItemListener())
                }
                layoutList.add(layout)
            }
        }

        layoutList.shuffle()

        for (linearLayout in layoutList) {
            draggableItemsContainer.addView(linearLayout)
        }
    }

    private fun createTextView(type: String, item: String): TextView {
        val textView = TextView(requireContext()).apply {
            text = type + item
            setTextColor(resources.getColor(R.color.text_color))

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 16, 8, 16)
            }
        }
        return textView
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
        val currentLinearLayout = view as? LinearLayout
        val adresseTextView = currentLinearLayout?.getChildAt(1) as? TextView
        val adresseText = adresseTextView?.text?.split(" ")?.get(1)
        return adresseText
    }
}