package com.srcbox.file.data

import android.content.Context
import android.os.Environment
import com.srcbox.file.application.EggApplication
import com.srcbox.file.data.`object`.AppSetting
import java.io.File
import java.io.InputStream

class AppStorageData() {


    companion object {
        fun getAppConfigStorage(): File {
            return File(EggApplication.context.filesDir, "config")
        }

        fun getFileOutFile(): File {
            return File(Environment.getExternalStorageDirectory(), AppSetting.appFileOut)
        }

        fun getAssetsIn(fileN: String): InputStream {
            return EggApplication.context.assets.open(fileN)
        }
    }
}