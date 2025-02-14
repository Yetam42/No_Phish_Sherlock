package com.example.nophishsherlock.game.fragments

import android.content.ClipData
import android.graphics.Color
import android.os.Bundle
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
import org.json.JSONArray
import kotlin.properties.Delegates


class DragAndDropFragment : BaseGameFragment() {

    private lateinit var draggableItemsContainer: LinearLayout
    private lateinit var targetContainer: LinearLayout
    private lateinit var dropZone1: LinearLayout
    private lateinit var dropZone2: LinearLayout
    private lateinit var dropZone1Text: TextView
    private lateinit var dropZone2Text: TextView


    private var currentGameData = DragAndDropData()

    private val DEFAULT_TARGET_STRING = "Hier ablegen"
    private val DROPZONE_1_INDEX = 0
    private val DROPZONE_2_INDEX = 1

    private var itemCount by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_draganddrop, container, false)

        draggableItemsContainer = view.findViewById(R.id.draggableItemsContainer)
        targetContainer = view.findViewById(R.id.targetContainer)
        dropZone1 = view.findViewById(R.id.dropZone1)
        dropZone2 = view.findViewById(R.id.dropZone2)
        dropZone1Text = view.findViewById(R.id.dropZone1Text)
        dropZone2Text = view.findViewById(R.id.dropZone2Text)



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val empfangeneFrage: GameData? = arguments?.getParcelable<GameData>("game")

            currentGameData = empfangeneFrage?.let { parseGameData(it) } as DragAndDropData
            itemCount = currentGameData.itemToTargetMap.values.sumOf { it.size }
        }

        createDragItemViews()
        setupDropZone()


        handleUserSelection()
    }

    override fun handleUserSelection() {
        dropZone2.setOnDragListener(createDragListener())
        dropZone1.setOnDragListener(createDragListener())
        draggableItemsContainer.setOnDragListener(createDragListener())
    }

    override fun updateUI(isCorrect: Boolean) {
    }


    //TODO GSON
    override fun parseGameData(gameData: GameData): DragAndDropData {
        return try {
            val jsonArray = gameData.content
            var dragAndDropData = DragAndDropData()
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val itemToTargetMap = mutableMapOf<String, MutableList<String>>()
                    val targetNames = mutableListOf<String>()

                    val jsonTargetNames = jsonObject.getJSONArray("targetNames")
                    for (j in 0 until jsonTargetNames.length()) {
                        targetNames.add(jsonTargetNames.getString(j))
                    }


                    val jsonMap = jsonObject.getJSONObject("itemToTargetMap")
                    for (key in jsonMap.keys()) {
                        val values = jsonMap.getJSONArray(key)
                        itemToTargetMap[key] =
                            createDragItemStrings(values) //createDragAndDropItem(values)
                    }
                    dragAndDropData = DragAndDropData(targetNames, itemToTargetMap)

                }
            }


            return dragAndDropData
        } catch (e: Exception) {
            e.printStackTrace()
            DragAndDropData()
        }
    }

    private fun createDragItemStrings(values: JSONArray): MutableList<String> {
        val dragItemsList = mutableListOf<String>()
        for (i in 0 until values.length()) {
            dragItemsList.add(values.getString(i))
        }
        return dragItemsList
    }

    private fun createDragItemViews() {
        val textViewList = mutableListOf<TextView>()

        for (values in currentGameData.itemToTargetMap.values) {
            for (item in values) {
                val dragItemTextView = TextView(requireContext()).apply {
                    text = item
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 16, 8, 16)
                    }
                    setBackgroundColor(Color.LTGRAY)

                    setOnTouchListener(createTouchItemListener2())
                }
                textViewList.add(dragItemTextView)
            }
        }

        textViewList.shuffle()

        for (textView in textViewList) {
            draggableItemsContainer.addView(textView)
        }
    }

    private fun setupDropZone() {
        dropZone1Text.text =
            currentGameData.targetNames.getOrNull(DROPZONE_1_INDEX) ?: DEFAULT_TARGET_STRING
        dropZone2Text.text =
            currentGameData.targetNames.getOrNull(DROPZONE_2_INDEX) ?: DEFAULT_TARGET_STRING

    }

    private fun createDragListener(
    ): View.OnDragListener {
        return View.OnDragListener { v, event ->

            val draggedView = event.localState as View
            val originalParent = draggedView.getTag(R.id.draggableItemsContainer) as? ViewGroup

            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    return@OnDragListener true
                }


                DragEvent.ACTION_DRAG_ENTERED -> { //löscht die View aus dem aktuellen Container sobald sich diese außerhalb befindet
                    val currentParent = draggedView.parent as? ViewGroup
                    if (currentParent != null && currentParent != v) {
                        currentParent.removeView(draggedView)
                    }
                    v.setBackgroundColor(Color.LTGRAY)
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> { //wenn view nicht gedroppt wurde, wird die vorschau wieder gelöscht, außer es ist der draggableItemContainer
                    v.setBackgroundColor(Color.TRANSPARENT)
                    (draggedView.parent as? ViewGroup)?.removeView(draggedView)
                    if (originalParent != null) {
                        originalParent.addView(draggedView)
                        draggedView.visibility = View.VISIBLE
                    }
                    true
                }

                DragEvent.ACTION_DROP -> {
                    v.setBackgroundColor(Color.TRANSPARENT)

                    val sourceParent = draggedView.parent as? ViewGroup
                    sourceParent?.removeView(draggedView)

                    if (v is ViewGroup) {
                        v.addView(draggedView)
                        draggedView.visibility = View.VISIBLE
                        draggedView.setOnTouchListener(createTouchItemListener2())
                    }

                    allItemsPlaced()

                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    v.setBackgroundColor(Color.TRANSPARENT)
                    if (!event.result) {
                        val currentParent = draggedView.parent as? ViewGroup
                        currentParent?.removeView(draggedView)

                        if (originalParent != null) {
                            originalParent.addView(draggedView)
                            draggedView.visibility = View.VISIBLE
                        }
                    }
                    true
                }

                else -> false


            }

        }
    }


    private fun createTouchItemListener2(): View.OnTouchListener {
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

                MotionEvent.ACTION_UP -> {
                    v.visibility = View.VISIBLE
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    v.visibility = View.VISIBLE
                    v.performClick()
                    true
                }

                else -> false
            }

        }
    }

    private fun allItemsPlaced() {
        val dropZone1Items = dropZone1.childCount - 1
        val dropZone2Items = dropZone2.childCount - 1

        val itemCount = currentGameData.itemToTargetMap.values.sumOf { it.size }

        val allItemsPlaced = dropZone1Items + dropZone2Items == itemCount

        if (allItemsPlaced) {
            notifyActivity(checkItemPlacement())
        }
    }

    private fun checkItemPlacement(): Boolean {
        var allItemsPlaced = true
        for ((targetName, items) in currentGameData.itemToTargetMap) {
            val targetView = when (targetName) {
                "1" -> dropZone1
                "2" -> dropZone2
                else -> null // Handle cases where the targetName is invalid
            }
            val actualItems = targetView?.let { getItemsFromView(it) }

            if (actualItems != null) {
                if (!actualItems.containsAll(items) || !items.containsAll(actualItems)) {
                    allItemsPlaced = false

                }
            }
        }
        return allItemsPlaced
    }

    private fun getItemsFromView(view: ViewGroup): List<String> {
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

    private fun extractString(view: View): CharSequence? {
        val currentTextView = view as? TextView
        val text = currentTextView?.text

        if (text == dropZone1Text.text || text == dropZone2Text.text) {
            return null
        }

        return text
    }


    data class DragAndDropData(
        val targetNames: List<String> = emptyList(),
        val itemToTargetMap: Map<String, MutableList<String>> = emptyMap()
    )
}