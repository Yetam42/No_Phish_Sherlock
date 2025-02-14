package com.example.nophishsherlock.game.helper

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class GameViewModel(application: Application) : AndroidViewModel(application) {

    // Wrap your shared preferences helper
    private val levelPreference = LevelPrefernce(application)

    // LiveData for the current level
    private val _currentLevel = MutableLiveData<Int>()
    val currentLevel: LiveData<Int> get() = _currentLevel


    private val _currentChapter = MutableLiveData<Int>()
    val currentChapter: LiveData<Int> get() = _currentChapter

    // LiveData for the completion status of levels (e.g., level -> completed)
    private val _levelCompletion = MutableLiveData<MutableMap<Int, Boolean>>()
    val levelCompletion: LiveData<MutableMap<Int, Boolean>> get() = _levelCompletion

    // Suppose you have a fixed number of levels (adjust as needed)
    private val totalLevels: Int

    init {
        // Load the current level from shared preferences
        _currentLevel.value = levelPreference.getCurrentLevel()

        _currentChapter.value = levelPreference.getCurrentChapter()

        val assetManager = getApplication<Application>().assets
        val chapters = assetManager.list("")?.filter { it.contains("chapter") } ?: listOf()
        totalLevels = chapters.size
        Log.d("Level", "Total levels: $totalLevels")

        // Initialize level completion map from shared preferences
        val map = mutableMapOf<Int, Boolean>()
        for (i in 1..totalLevels) {
            map[i] = levelPreference.isLevelCompleted(i)
        }
        _levelCompletion.value = map
    }

    fun setCurrentLevel(newLevel: Int) {
        Log.d("Level", "Setting current level to $newLevel")
        if (newLevel <= totalLevels) {
            _currentLevel.value = newLevel
            levelPreference.setCurrentLevel(newLevel)
        }
    }

    fun setLevelCompleted(level: Int, isCompleted: Boolean) {
        // Update the LiveData map...
        val map = _levelCompletion.value ?: mutableMapOf()
        map[level] = isCompleted
        _levelCompletion.value = map

        // ... and persist the change to SharedPreferences.
        levelPreference.setLevelCompleted(level, isCompleted)
    }

    fun setCurrentChapter(newChapter: Int) {
        if (newChapter <= totalLevels) {
            _currentChapter.value = newChapter
            levelPreference.setCurrentChapter(newChapter)
        }
    }
}