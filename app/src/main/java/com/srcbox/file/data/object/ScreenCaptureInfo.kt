package com.srcbox.file.data.`object`

import android.content.Intent
import com.srcbox.file.util.ScreenCaptureUtil

object ScreenCaptureInfo {
    var screenCaptureUtilInterface: ScreenCaptureUtil? = null
    var resultCode = 0
    var intentData: Intent? = null
    val CODE = 1
    var isStart = false
}