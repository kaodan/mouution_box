package com.srcbox.file.ui.searchimginfo.data

data class ImgInfoData(
    val title: String,
    val imgLink: String,
    val miscInfo: String,
    val similarityInfo: String,
    val contentColumns: ArrayList<ContentColumnData>, val onLink: String
)