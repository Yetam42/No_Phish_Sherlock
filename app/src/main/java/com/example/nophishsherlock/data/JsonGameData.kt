package com.example.nophishsherlock.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import org.json.JSONArray

/**
 * Diese Klasse repräsentiert das JsonGameData
 *
 * @property title das Titel des Spiels
 * @property taskDescription die Beschreibung der Aufgabe
 * @property game das Spiel was gespielt werden soll
 */
data class JsonGameData(
    val title: String,
    val taskDescription: String,
    val game: GameData?
)

/**
 * Diese Klasse repräsentiert das GameData
 *
 * @property type das Type des Spiels
 * @property content das Spiel was gespielt werden soll
 */
@Parcelize
data class GameData(
    val type: String,
    val content: @RawValue JSONArray? = null,
) : Parcelable