package com.example.nophishsherlock.game

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.nophishsherlock.R

/**
 * Diese Klasse ist die Activity für das Spielmenü
 */
class GameSelectionActivty : AppCompatActivity(){

    lateinit var chapter1Button: Button
    lateinit var dragAndDropButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_selection_screen)

        chapter1Button = findViewById<Button>(R.id.chapter1game)
        dragAndDropButton = findViewById<Button>(R.id.draganddrop)

        chapter1Button.setOnClickListener {
            val intent = Intent(this, MainGameActivity::class.java)
            startActivity(intent)
        }

        dragAndDropButton.setOnClickListener {
            val intent = Intent(this, DragNDrop::class.java)
            startActivity(intent)
        }

    }
}