package com.example.nophishsherlock.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Diese Klasse repräsentiert das JsonTextData
 *
 * @property section das Section des Textes
 * @property title der Titel des Textes
 * @property paragraphs die Paragraphen des Textes
 * @property media das MediaData was eingefügt werden soll
 */
@Parcelize
data class JsonTextData(
    val section: String? = null,
    val title: String? = null,
    val paragraphs: List<String>? = null,
    val media: MediaData? = null,
    val imageText: ImageText? = null,
    val paragraphWithMedia: List<ParagraphWithImage>? = null
) : Parcelable

/**
 * Diese Klasse repräsentiert das MediaData
 *
 * @property type das Type des MediaData
 * @property description die Beschreibung des MediaData
 * @property source die Quelle des MediaData
 */
@Parcelize
data class MediaData(
    val type: String,
    val description: String? = null,
    val source: String,
) : Parcelable


@Parcelize
data class ImageText(
    val text: List<String>? = null,
    val imageSource: String,
    val imageFirst : Boolean = false
) : Parcelable

@Parcelize
data class ParagraphWithImage(
    val paragraphs: List<String>? = null,
    val media: MediaData? = null,
    val media2: MediaData? = null
) : Parcelable
