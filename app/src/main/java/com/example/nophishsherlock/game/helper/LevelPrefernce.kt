package com.example.nophishsherlock.game.helper

import android.content.Context
import android.content.SharedPreferences

class LevelPrefernce(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("LevelPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    private val CURRENT_LEVEL = "current_level"
    private val LEVEL_COMPLETED_PREFIX = "level_completed_"
    private val CURRENT_CHAPTER = "current_chapter"


    fun getCurrentLevel(): Int {
        return prefs.getInt(CURRENT_LEVEL, 1)
    }

    fun setCurrentLevel(level: Int) {
        editor.putInt(CURRENT_LEVEL, level)
        editor.apply()
    }

    fun isLevelCompleted(level: Int): Boolean {
        return prefs.getBoolean(LEVEL_COMPLETED_PREFIX + level, false)
    }

    fun setLevelCompleted(level: Int, completed: Boolean) {
        editor.putBoolean(LEVEL_COMPLETED_PREFIX + level, completed)
        editor.apply()
    }

    fun getCurrentChapter(): Int {
        return prefs.getInt(CURRENT_CHAPTER, 1)
    }

    fun setCurrentChapter(chapter: Int) {
        editor.putInt(CURRENT_CHAPTER, chapter)
        editor.apply()
    }

}