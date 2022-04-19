package com.srcbox.file.ui.util.cartoonFormPicture.data

data class DocData(
    val from: Float,
    val to: Float,
    val at: Float,
    val anilistId: Int,
    val season: String,
    val episode: String,
    val similarity: Double,
    val anime: String,
    val fileName: String,
    val tokenThumb: String,
    val titleNative: String,
    val titleChinese: String,
    val titleEnglish: String
)