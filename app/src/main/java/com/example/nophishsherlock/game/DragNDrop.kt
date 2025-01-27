package com.example.nophishsherlock.game

import android.content.ClipData
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nophishsherlock.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


/**
 * Diese Klasse repräsentiert ein Drag and Drop Item
 *
 * @property id die ID des Items
 * @property type der Typ des Items
 * @property text der Text des Items
 */
@Serializable
data class DragItem(val id: Int, val type: String, val text: String? = null)

/**
 * Diese Klasse repräsentiert das Drag and Drop Spielentwurf
 * sie ist aktuell noch nicht in der Hauptspielanwendung verwendet
 * Das wird aber noch implementiert
 */
class DragNDrop : AppCompatActivity() {

    //hier werden die Elemente der Activity initialisiert
    private lateinit var draggableItem1: TextView
    private lateinit var draggableItem2: TextView
    private lateinit var dropZone1: LinearLayout
    private lateinit var dropZone2: LinearLayout
    private lateinit var checkButton: Button

    //hier werden die Variablen initialisiert
    private var originalDropZone1Text = ""
    private var originalDropZone2Text = ""
    private val droppedItemsZone1 = mutableListOf<DragItem>()
    private val droppedItemsZone2 = mutableListOf<DragItem>()
    private var dropCountZone1 = 0
    private var dropCountZone2 = 0
    private val dragItems = mutableListOf<DragItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_draganddrop)

        draggableItem2 = findViewById(R.id.draggableItem2)
        draggableItem1 = findViewById(R.id.draggableItem1)
        dropZone2 = findViewById(R.id.dropZone2)
        dropZone1 = findViewById(R.id.dropZone1)

        checkButton = findViewById(R.id.checkButton)

        checkButton.setOnClickListener {
            checkZone()
        }


        originalDropZone1Text =
            dropZone1.getChildAt(0).let { if (it is TextView) it.text.toString() else "" }
        originalDropZone2Text =
            dropZone2.getChildAt(0).let { if (it is TextView) it.text.toString() else "" }

        dragItems.add(DragItem(1, "Text", draggableItem1.text.toString()))
        dragItems.add(DragItem(2, "Text", draggableItem2.text.toString()))

        draggableItem1.tag = dragItems[0].id
        draggableItem2.tag = dragItems[1].id

        val dragStartListener = View.OnLongClickListener { view ->
            val dragItem = dragItems.find { it.id == view.tag }
            val clipData = ClipData.newPlainText("drag_item_json", Json.encodeToString(dragItem))
            val shadow = View.DragShadowBuilder(view)
            val originalVisibility = view.visibility
            view.startDragAndDrop(clipData, shadow, Pair(view, originalVisibility), 0)
            view.visibility = View.INVISIBLE
            true
        }

        draggableItem1.setOnLongClickListener(dragStartListener)
        draggableItem2.setOnLongClickListener(dragStartListener)

        val dragListener = View.OnDragListener { view, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val clipData = event.clipData
                    val dragItemJson = clipData?.getItemAt(0)?.text.toString()

                    val dragItem: DragItem? = try {
                        Json.decodeFromString(dragItemJson)
                    } catch (e: Exception) {
                        Log.e("Deserialization Error", "Error deserializing DragItem", e)
                        return@OnDragListener true
                    }
                    if (dragItem == null) return@OnDragListener true

                    val dropTarget = view as LinearLayout
                    val droppedItemsList =
                        if (dropTarget == dropZone1) droppedItemsZone1 else droppedItemsZone2
                    var dropCount = if (dropTarget == dropZone1) dropCountZone1 else dropCountZone2

                    droppedItemsList.add(dragItem)
                    dropCount++

                    val droppedTextView = TextView(this).apply {
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(8, 16, 8, 16)
                        }
                        layoutParams = params

                        text = dragItem.text
                        setBackgroundColor(Color.LTGRAY)
                        setTypeface(typeface, Typeface.BOLD)
                        setOnClickListener {
                            dropTarget.removeView(this)
                            droppedItemsList.remove(dragItem)
                            dropCount--
                            when (dragItem.id) {
                                1 -> draggableItem1.visibility = View.VISIBLE
                                2 -> draggableItem2.visibility = View.VISIBLE
                            }
                        }
                    }

                    dropTarget.addView(droppedTextView)
                    if (dropTarget == dropZone1) dropCountZone1 = dropCount else dropCountZone2 =
                        dropCount
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    val sourceData = event.localState as? Pair<*, *>
                    val sourceView = sourceData?.first as? View
                    val originalVisibility = sourceData?.second as? Int
                    if (!event.result && sourceView != view) {
                        sourceView?.visibility = originalVisibility ?: View.VISIBLE
                    }
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            this@DragNDrop,
                            android.R.color.holo_blue_light
                        )
                    )
                    true
                }

                else -> true
            }
        }

        dropZone1.setOnDragListener(dragListener)
        dropZone2.setOnDragListener(dragListener)
        dropZone1.setOnLongClickListener(null)
        dropZone2.setOnLongClickListener(null)
    }

    /**
     * Diese Funktion überprüft, ob die Zones korrekt sind
     */
    private fun checkZone() {
        dropZone1 = findViewById(R.id.dropZone1)
        dropZone2 = findViewById(R.id.dropZone2)
        draggableItem1 = findViewById(R.id.draggableItem1)
        draggableItem2 = findViewById(R.id.draggableItem2)

        val expectedItemsZone1 = mutableListOf<DragItem>()
        val expectedItemsZone2 = mutableListOf<DragItem>()

        expectedItemsZone1.add(dragItems[0])
        expectedItemsZone2.add(dragItems[1])

        val isZone1Correct = droppedItemsZone1 == expectedItemsZone1
        val isZone2Correct = droppedItemsZone2 == expectedItemsZone2

        if (isZone1Correct && isZone2Correct) {
            // Alle Zonen sind korrekt
            dropZone1.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.holo_green_light
                )
            )
            dropZone2.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.holo_green_light
                )
            )
            Toast.makeText(this, "Alle Zonen korrekt!", Toast.LENGTH_SHORT).show()
        } else {
            // Mindestens eine Zone ist falsch
            dropZone1.setBackgroundColor(
                if (isZone1Correct) ContextCompat.getColor(
                    this,
                    android.R.color.holo_green_light
                ) else ContextCompat.getColor(this, android.R.color.holo_red_light)
            )
            dropZone2.setBackgroundColor(
                if (isZone2Correct) ContextCompat.getColor(
                    this,
                    android.R.color.holo_green_light
                ) else ContextCompat.getColor(this, android.R.color.holo_red_light)
            )
            Toast.makeText(this, "Mindestens eine Zone ist falsch!", Toast.LENGTH_SHORT).show()
            droppedItemsZone1.clear()
            droppedItemsZone2.clear()
            dropCountZone1 = 0
            dropCountZone2 = 0
            while (dropZone1.childCount > 1) {
                dropZone1.removeViewAt(dropZone1.childCount - 1)
            }
            while (dropZone2.childCount > 1) {
                dropZone2.removeViewAt(dropZone2.childCount - 1)
            }
            draggableItem1.visibility = View.VISIBLE
            draggableItem2.visibility = View.VISIBLE
        }
    }
}