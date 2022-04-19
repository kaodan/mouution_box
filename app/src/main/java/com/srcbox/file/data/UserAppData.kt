package com.srcbox.file.data

import android.graphics.drawable.Drawable
import java.io.File

data class UserAppData(
    val appSource:File,
    val appIcon: Drawable,
    val name: String,
    val appSize: Int,
    val appPackageName: String?,
    val appInstallTime: Long,
    val isOsApp: Boolean
)