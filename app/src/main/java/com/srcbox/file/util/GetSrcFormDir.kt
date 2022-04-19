package com.srcbox.file.util

import android.app.Activity
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class GetSrcFormDir(
    val context: Activity,
    private val getZipFile: GetZipFile,
    private var extJsonO: JSONObject? = null,
    val getProgress: (fNum: Int, bool: Boolean) -> Unit
) {
    private var outDir: File = File("")
    var fNum: Int = 0
    fun setOutDir(outDir: File) {
        extJsonO = JSON.parseObject(getZipFile.extJsonMsg)
        this.outDir = outDir
    }

    fun getSrc(f: File) {
        if (!f.exists()) {
            context.runOnUiThread {
                getProgress(fNum, false)
            }
            return
        }
        if (f.isDirectory) {
            f.listFiles()?.forEach {
                getSrc(it.absoluteFile)
            }
        } else {
            val fIn = f.inputStream()
            val boolStr = getZipFile.filterFile(
                getZipFile.getFileByteHead(fIn),
                EggUtil.getPathExtend(f.name)
            )
            if (boolStr[0] == "false") return
            val fName = EggUtil.reNameExt(
                f.name,
                extJsonO!!.getJSONObject(boolStr[0])
                    .getString(boolStr[1])
            )
            makeFileFormInputSteam(File("$outDir/${boolStr[0]}/$fName"), fIn)
            getProgress(fNum, true)
        }
    }


    private fun makeFileFormInputSteam(
        f: File,
        inputSteam: InputStream
    ) {
        if (inputSteam.available() == 0) {
            return
        }
        if (!f.parentFile!!.exists()) f.parentFile!!.mkdirs()
        val fileOut = FileOutputStream(f)
        val byte = ByteArray(524)
        var len: Int
        fileOut.write(getZipFile.headByteBuff, 0, getZipFile.headByteI)
        while ((inputSteam.read(byte).apply { len = this }) != -1) {
            fileOut.write(byte, 0, len)
        }
        fileOut.close()
        inputSteam.close()
        fNum++
    }
}