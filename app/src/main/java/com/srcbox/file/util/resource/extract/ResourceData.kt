package com.srcbox.file.util.resource.extract

import com.alibaba.fastjson.JSONObject
import java.io.File

object ResourceData {
    var outDir: File? = null
    var fileSize:Long = 1 * 1024 * 5
    const val START_STATE = 0
    const val PAUSE_STATE = 1
    const val SUCCESS_STATE = 2
    const val DEL_STATE = 6
    const val EXTRACT_TYPE_SRC = 3
    const val EXTRACT_TYPE_ICON = 4
    const val EXTRACT_TYPE_PACK = 5
    const val EXTRACT_TYPE_LOCATION = 6
    var extractType = EXTRACT_TYPE_SRC
}


/*
{
    "filePath":"apk",
    "thisEntry": 2,
    "state": "success"
}*/
