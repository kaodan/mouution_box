package com.srcbox.file.data

import android.graphics.Bitmap
import com.srcbox.file.view.StokeRect

data class ShowIconData(
    val bitmap: Bitmap,
    val stokeRect: StokeRect,
    val resourceIdName: String
)