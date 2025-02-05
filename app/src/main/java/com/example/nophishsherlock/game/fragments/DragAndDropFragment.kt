package com.example.nophishsherlock.game.fragments

import android.content.ClipData
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.no_phishing_yannick.games.helper.BaseGameFragment
import com.example.nophishsherlock.R
import com.example.nophishsherlock.data.GameData
import org.json.JSONArray
import org.json.JSONObject

data class DragAndDropItem(val id: Int, val text: String)

class DragAndDropFragment : BaseGameFragment() {

    private lateinit var draggableItemsContainer: LinearLayout
    private lateinit var targetContainer: LinearLayout
    private lateinit var dropZone1: LinearLayout
    private lateinit var dropZone2: LinearLayout
    private lateinit var dropZone1Text: TextView
    private lateinit var dropZone2Text: TextView


    private val droppedItems = mutableListOf<MutableList<DragAndDropItem>>()
    private var dropCounts = mutableListOf<Int>()



    private var currentGameData = DragAndDropData()

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
        }

        Log.d("DragAndDropFragment", "currentGameData: $currentGameData")
        createDragItemViews()
        setupDropZone()


        handleUserSelection()
    }

    override fun handleUserSelection() {
        dropZone1.setOnDragListener(createDropZoneListener(dropZone1, DROPZONE_1_INDEX))
        dropZone1.setOnLongClickListener(null)
        dropZone2.setOnDragListener(createDropZoneListener(dropZone2, DROPZONE_2_INDEX))
        dropZone2.setOnLongClickListener(null)
    }

    override fun updateUI(isCorrect: Boolean) {
    }


    override fun parseGameData(gameData: GameData): DragAndDropData {
        return try {
            val jsonArray = gameData.content
            var dragAndDropData = DragAndDropData()
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val itemToTargetMap = mutableMapOf<String, MutableList<DragAndDropItem>>()
                    val targetNames = mutableListOf<String>()

                    val jsonTargetNames = jsonObject.getJSONArray("targetNames")
                    for (j in 0 until jsonTargetNames.length()) {
                        targetNames.add(jsonTargetNames.getString(j))
                    }


                    val jsonMap = jsonObject.getJSONObject("itemToTargetMap")
                    for (key in jsonMap.keys()) {
                        val values = jsonMap.getJSONArray(key)
                        itemToTargetMap[key] = createDragAndDropItem(values)
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

    private fun createDragAndDropItem(values: JSONArray): MutableList<DragAndDropItem> {
        val dragItemsList = mutableListOf<DragAndDropItem>()
        Log.d("DragAndDropFragment", "List size: ${dragItemsList.size}")
        for (i in 0 until values.length()) {
            Log.d("DragAndDropFragment", "Adding item: ${values.getString(i)}, with id: ${dragItemsList.size + 1}")
            dragItemsList.add(DragAndDropItem(i, values.getString(i)))
        }
        return dragItemsList
    }

    private fun createDragItemViews() {
        val textViewList = mutableListOf<TextView>()

        for (values in currentGameData.itemToTargetMap.values) {
            for (item in values) {
                val dragItemTextView = TextView(requireContext()).apply {
                    text = item.text
                    tag = item.text
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 16, 8, 16)
                    }
                    setBackgroundColor(Color.LTGRAY)

                    setOnLongClickListener(createDragItemListener(item))
                }
                textViewList.add(dragItemTextView)
            }
        }

        textViewList.shuffle()

        for (textView in textViewList) {
            draggableItemsContainer.addView(textView)
        }
    }

    private fun setupDropZone(){
        dropZone1Text.text = currentGameData.targetNames.getOrNull(DROPZONE_1_INDEX) ?: DEFAULT_TARGET_STRING
        dropZone2Text.text = currentGameData.targetNames.getOrNull(DROPZONE_2_INDEX) ?: DEFAULT_TARGET_STRING

        droppedItems.add(mutableListOf())
        droppedItems.add(mutableListOf())

        dropCounts.add(0)
        dropCounts.add(0)

    }

    private fun createDropZoneListener(dropZone: LinearLayout, dropZoneIndex: Int) : View.OnDragListener {
        return View.OnDragListener{ view, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val droppedItem = getDroppedItem(event)
                    if (droppedItem != null) {
                        updateDropCount(dropZoneIndex)
                        droppedItems[dropZoneIndex].add(droppedItem)
                        updateDropZoneUI(dropZoneIndex, dropZone, droppedItem)
                        checkDropZones()
                    }
                    else {
                        return@OnDragListener false
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    val sourceData = event.localState as? Pair<View, Int>
                    val sourceView = sourceData?.first
                    val originalVisibility = sourceData?.second
                    if (!event.result && sourceView != view) {
                        sourceView?.visibility = originalVisibility ?: View.VISIBLE
                    }
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.holo_blue_light
                        )
                    )
                    true
                }


                else -> true
            }


        }

    }

    private fun getDroppedItem(event : DragEvent) : DragAndDropItem? {
        val clipData = event.clipData
        val dragItemJson = clipData.getItemAt(0).text.toString()
        val jsonObject = JSONObject(dragItemJson)
        val id = jsonObject.getInt("id")
        val text = jsonObject.getString("text")

        val dragItem: DragAndDropItem? = try {
            DragAndDropItem(
                id = id,
                text = text
            )
        } catch (e: Exception) {
            null
        }

        return dragItem
    }

    private fun updateDropCount(dropZoneIndex: Int) {
        var currentDropCount = dropCounts[dropZoneIndex]
        currentDropCount++
        dropCounts[dropZoneIndex] = currentDropCount
    }

    private fun updateDropZoneUI(dropZoneIndex: Int, dropZoneView: LinearLayout, droppedItem: DragAndDropItem) {
        val droppedItemsList = droppedItems[dropZoneIndex]
        val droppedTextView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 16, 8, 16)
            }
            text = droppedItem.text
            setBackgroundColor(Color.LTGRAY)
            setOnClickListener {
                dropZoneView.removeView(this)
                droppedItemsList.remove(droppedItem)
                dropCounts[dropZoneIndex]--
                val draggableTextView =
                    draggableItemsContainer.findViewWithTag(droppedItem.text) as? TextView
                draggableTextView?.visibility = View.VISIBLE

            }
        }

        if (dropCounts[DROPZONE_1_INDEX] == 0 && dropCounts[DROPZONE_2_INDEX] == 0) {
            dropZone1.setBackgroundColor(getResources().getColor(R.color.purple_200))
            dropZone2.setBackgroundColor(getResources().getColor(R.color.purple_200))
        }

        dropZoneView.addView(droppedTextView)
    }

    private fun checkDropZones() {
        val alleElementeZugeordnet =
            droppedItems.sumOf { it.size } == draggableItemsContainer.childCount

        if (alleElementeZugeordnet) {
            notifyActivity(isCorrectAssignment())
        }
    }

    private fun isCorrectAssignment(): Boolean {
        val expectedItemsZone1 = currentGameData.itemToTargetMap["1"]?.sortedBy { it.id }
        val expectedItemsZone2 = currentGameData.itemToTargetMap["2"]?.sortedBy { it.id }

        val isZone1Correct = droppedItems[DROPZONE_1_INDEX].sortedBy { it.id } == expectedItemsZone1
        val isZone2Correct = droppedItems[DROPZONE_2_INDEX].sortedBy { it.id } == expectedItemsZone2



        return isZone1Correct && isZone2Correct
    }

    private fun createDragItemListener(dragItem: DragAndDropItem): View.OnLongClickListener {
        return View.OnLongClickListener { v ->
            val jsonObject = JSONObject().apply {
                put("id", dragItem.id)
                put("text", dragItem.text)
            }
            val clipData = ClipData.newPlainText("drag_item_json", jsonObject.toString())
            val shadow = View.DragShadowBuilder(v)
            val originalVisibility = v.visibility
            v.startDragAndDrop(clipData, shadow, Pair(v, originalVisibility), 0)
            v.visibility = View.INVISIBLE
            true
        }
    }

    data class DragAndDropData(
        val targetNames: List<String> = emptyList(),
        val itemToTargetMap: Map<String, MutableList<DragAndDropItem>> = emptyMap()
    )
}