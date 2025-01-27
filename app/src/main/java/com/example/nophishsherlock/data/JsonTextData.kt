package com.example.nophishsherlock.data

import java.io.Serializable

/**
 * Diese Klasse repräsentiert das JsonTextData
 *
 * @property section das Section des Textes
 * @property title der Titel des Textes
 * @property paragraphs die Paragraphen des Textes
 * @property media das MediaData was eingefügt werden soll
 */
data class JsonTextData (
    val section: String? = null,
    val title: String,
    val paragraphs: List<String>,
    val media: MediaData? = null,
) : Serializable

/**
 * Diese Klasse repräsentiert das MediaData
 *
 * @property type das Type des MediaData
 * @property description die Beschreibung des MediaData
 * @property source die Quelle des MediaData
 */
data class MediaData(
    val type: String,
    val description: String,
    val source: String,
)