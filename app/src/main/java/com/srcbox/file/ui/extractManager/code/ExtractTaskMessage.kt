package com.srcbox.file.ui.extractManager.code

import java.io.File
import java.io.Serializable

data class ExtractTaskMessage(
    val file: File,
    val appName: String,
    var state: Int = -1,
    var total: Int = 0,
    var thisFilePos: Int = 0
) :
    Serializable