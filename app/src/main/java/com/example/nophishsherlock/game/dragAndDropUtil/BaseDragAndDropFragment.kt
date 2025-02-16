package com.example.nophishsherlock.game.dragAndDropUtil

import android.content.ClipData
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.no_phishing_yannick.games.helper.BaseGameFragment
import com.example.nophishsherlock.R
import com.example.nophishsherlock.data.GameData
import com.google.android.material.color.MaterialColors
import org.json.JSONArray

abstract class BaseDragAndDropFragment : BaseGameFragment() {

    protected lateinit var draggableItemsContainer: LinearLayout
    private lateinit var dropZone1: LinearLayout
    private lateinit var dropZone2: LinearLayout
    protected lateinit var dropZone1Text: TextView
    protected lateinit var dropZone2Text: TextView

    protected var currentGameData = DragAndDropData()

    private val DEFAULT_TARGET_STRING = "Hier ablegen"
    private val DROPZONE_1_INDEX = 0
    private val DROPZONE_2_INDEX = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_draganddrop, container, false)

        draggableItemsContainer = view.findViewById(R.id.draggableItemsContainer)
        dropZone1 = view.findViewById(R.id.dropZone1)
        dropZone2 = view.findViewById(R.id.dropZone2)
        dropZone1Text = view.findViewById(R.id.dropZone1Text)
        dropZone2Text = view.findViewById(R.id.dropZone2Text)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<GameData>("game")?.let {
            currentGameData = parseGameData(it)
        }

        createDragItemViews()
        setupDropZone()
        handleUserSelection()
    }

    override fun handleUserSelection() {
        dropZone1.setOnDragListener(createDragListener())
        dropZone2.setOnDragListener(createDragListener())
        draggableItemsContainer.setOnDragListener(createDragListener())
    }

    private fun createDragItemStrings(values: JSONArray): MutableList<String> {
        val dragItemsList = mutableListOf<String>()
        for (i in 0 until values.length()) {
            dragItemsList.add(values.getString(i))
        }
        return dragItemsList
    }

    private fun setupDropZone() {
        dropZone1Text.text =
            currentGameData.targetNames.getOrNull(DROPZONE_1_INDEX) ?: DEFAULT_TARGET_STRING
        dropZone2Text.text =
            currentGameData.targetNames.getOrNull(DROPZONE_2_INDEX) ?: DEFAULT_TARGET_STRING
    }

    private fun createDragListener(): View.OnDragListener {
        return View.OnDragListener { v, event ->
            val draggedView = event.localState as View
            val originalParent = draggedView.getTag(R.id.draggableItemsContainer) as? ViewGroup

            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> true

                DragEvent.ACTION_DRAG_ENTERED -> {
                    (draggedView.parent as? ViewGroup)?.removeView(draggedView)
                    if (v.id != draggableItemsContainer.id) {
                        v.setBackgroundColor(
                            MaterialColors.getColor(
                                v,
                                com.google.android.material.R.attr.colorPrimaryContainer
                            )
                        )
                    }
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    if (v.id != draggableItemsContainer.id) {
                        v.setBackgroundColor(
                            MaterialColors.getColor(
                                v,
                                com.google.android.material.R.attr.colorPrimaryContainer
                            )
                        )
                    }
                    (draggedView.parent as? ViewGroup)?.removeView(draggedView)
                    true
                }

                DragEvent.ACTION_DROP -> {
                    if (v.id != draggableItemsContainer.id) {
                        v.setBackgroundColor(
                            MaterialColors.getColor(
                                v,
                                com.google.android.material.R.attr.colorPrimaryContainer
                            )
                        )
                    }
                    (draggedView.parent as? ViewGroup)?.removeView(draggedView)
                    (v as? ViewGroup)?.addView(draggedView)
                    draggedView.visibility = View.VISIBLE
                    draggedView.setOnTouchListener(createTouchItemListener())
                    allItemsPlaced()
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    if (v.id != draggableItemsContainer.id) {
                        v.setBackgroundColor(
                            MaterialColors.getColor(
                                v,
                                com.google.android.material.R.attr.colorPrimaryContainer
                            )
                        )
                    }
                    if (!event.result) {
                        (draggedView.parent as? ViewGroup)?.removeView(draggedView)
                        originalParent?.addView(draggedView)
                        draggedView.visibility = View.VISIBLE
                    }
                    true
                }

                else -> false
            }
        }
    }

    protected fun createTouchItemListener(): View.OnTouchListener {
        return View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (v.getTag(R.id.draggableItemsContainer) == null) {
                        v.setTag(R.id.draggableItemsContainer, v.parent)
                    }
                    val shadowBuilder = View.DragShadowBuilder(v)
                    v.startDragAndDrop(ClipData.newPlainText("", ""), shadowBuilder, v, 0)
                    v.visibility = View.INVISIBLE
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.visibility = View.VISIBLE
                    v.performClick()
                    true
                }

                else -> false
            }
        }
    }

    private fun allItemsPlaced() {

        val itemCount = currentGameData.itemToTargetMap.values.sumOf { it.size }

        val allItemsPlaced = (dropZone1.childCount - 1 + dropZone2.childCount - 1) == itemCount
        if (allItemsPlaced) {
            notifyActivity(checkItemPlacement())
        }
    }

    private fun checkItemPlacement(): Boolean {
        var correctPlacement = true
        for ((targetName, items) in currentGameData.itemToTargetMap) {
            val targetView = when (targetName) {
                "1" -> dropZone1
                "2" -> dropZone2
                else -> null
            }
            val actualItems = targetView?.let { getItemsFromView(it) }
            Log.d("DragAndDropFragment", "actualItems: $actualItems")

            if (actualItems == null || !actualItems.containsAll(items) || !items.containsAll(
                    actualItems
                )
            ) {
                correctPlacement = false
            }
        }
        return correctPlacement
    }


    override fun updateUI(isCorrect: Boolean) {
        TODO("Not yet implemented")
    }

    abstract fun createDragItemViews()

    abstract fun getItemsFromView(view: ViewGroup): List<String>

    abstract fun extractString(view: View): CharSequence?

    override fun parseGameData(gameData: GameData): DragAndDropData {
        return try {
            val jsonArray = gameData.content
            var dragAndDropData = DragAndDropData()
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val itemToTargetMap = mutableMapOf<String, MutableList<String>>()
                    val targetNames = mutableListOf<String>()
                    val itemToOrganisation = mutableMapOf<String, String>()

                    val jsonTargetNames = jsonObject.getJSONArray("targetNames")
                    for (j in 0 until jsonTargetNames.length()) {
                        targetNames.add(jsonTargetNames.getString(j))
                    }

                    val jsonMap = jsonObject.getJSONObject("itemToTargetMap")
                    for (key in jsonMap.keys()) {
                        val values = jsonMap.getJSONArray(key)
                        itemToTargetMap[key] = createDragItemStrings(values)
                    }

                    if (jsonObject.has("itemToOrganisation")) {
                        val jsonItemToOrganisation = jsonObject.getJSONObject("itemToOrganisation")
                        for (key in jsonItemToOrganisation.keys()) {
                            val values = jsonItemToOrganisation.getString(key)
                            itemToOrganisation[key] = values
                        }
                    }

                    dragAndDropData =
                        DragAndDropData(targetNames, itemToTargetMap, itemToOrganisation)
                }
            }
            dragAndDropData
        } catch (e: Exception) {
            e.printStackTrace()
            DragAndDropData()
        }
    }

    data class DragAndDropData(
        val targetNames: List<String> = emptyList(),
        val itemToTargetMap: Map<String, MutableList<String>> = emptyMap(),
        val itemToOrganisation: Map<String, String> = emptyMap()
    )
}