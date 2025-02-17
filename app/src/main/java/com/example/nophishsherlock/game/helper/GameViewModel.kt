package com.example.nophishsherlock.game.helper

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val levelPreference = LevelPrefernce(application)

    private val _currentLevel = MutableLiveData<Int>()
    val currentLevel: LiveData<Int> get() = _currentLevel


    private val _currentChapter = MutableLiveData<Int>()
    val currentChapter: LiveData<Int> get() = _currentChapter

    private val _levelCompletion = MutableLiveData<MutableMap<Int, Boolean>>()
    val levelCompletion: LiveData<MutableMap<Int, Boolean>> get() = _levelCompletion

    private val totalLevels: Int

    init {
        _currentLevel.value = levelPreference.getCurrentLevel()

        _currentChapter.value = levelPreference.getCurrentChapter()

        val assetManager = getApplication<Application>().assets
        val chapters = assetManager.list("")?.filter { it.contains("chapter") } ?: listOf()
        totalLevels = chapters.size

        val map = mutableMapOf<Int, Boolean>()
        for (i in 1..totalLevels) {
            map[i] = levelPreference.isLevelCompleted(i)
        }
        _levelCompletion.value = map
    }

    fun setCurrentLevel(newLevel: Int) {
        if (newLevel <= totalLevels) {
            _currentLevel.value = newLevel
            levelPreference.setCurrentLevel(newLevel)
        }
    }

    fun setLevelCompleted(level: Int, isCompleted: Boolean) {
        val map = _levelCompletion.value ?: mutableMapOf()
        map[level] = isCompleted
        _levelCompletion.value = map

        levelPreference.setLevelCompleted(level, isCompleted)
    }

    fun setCurrentChapter(newChapter: Int) {
        if (newChapter <= totalLevels) {
            _currentChapter.value = newChapter
            levelPreference.setCurrentChapter(newChapter)
        }
    }
}