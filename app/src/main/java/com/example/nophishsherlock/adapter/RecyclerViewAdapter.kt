package com.example.nophishsherlock.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.nophishsherlock.R
import com.example.nophishsherlock.contentbuilder.ContentViewBuilder
import com.example.nophishsherlock.data.JsonTextData

/**
 * Der RecyclerViewAdapter für das Hauptmenü.
 *
 * @property cards Die Liste der Cards, die angezeigt werden sollen.
 * @constructor Erstellt ein neues RecyclerViewAdapter mit den angegebenen Cards.
 *
 * @param context Der Context, in dem das Adapter verwendet wird.
 */
class RecyclerViewAdapter(private val cards: List<JsonTextData>, context: Context) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var viewBuilder = ContentViewBuilder(context)
    private var allViews: List<View> = emptyList()

    init {
        allViews = viewBuilder.buildContent(cards)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = allViews[position]

        holder.bind(view)
    }

    override fun getItemCount(): Int {
        return cards.size
    }


    /**
     * Hier wird die Viewholder erstellt
     *
     * @constructor erstellt die Viewholder
     *
     * @param view die View die die ViewHolder enthält
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val contentContainer: LinearLayout = view.findViewById(R.id.linearLayout)

        fun bind(view: View) {
            contentContainer.removeAllViews()
            Log.d("ViewHolder", "Adding view: $view")
            contentContainer.addView(view)
        }
    }
}