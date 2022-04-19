package com.srcbox.file.util.resource.extract

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.alibaba.fastjson.JSON
import com.srcbox.file.application.EggApplication
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.UserAppData
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.resource.extract.ResourceData.DEL_STATE
import com.srcbox.file.util.resource.extract.ResourceData.EXTRACT_TYPE_ICON
import com.srcbox.file.util.resource.extract.ResourceData.EXTRACT_TYPE_PACK
import com.srcbox.file.util.resource.extract.ResourceData.EXTRACT_TYPE_SRC
import com.srcbox.file.util.resource.extract.ResourceData.PAUSE_STATE
import com.srcbox.file.util.resource.extract.ResourceData.START_STATE
import com.srcbox.file.util.resource.extract.ResourceData.SUCCESS_STATE
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

class ResourceExtractTask(
    val context: Activity,
    private val filePath: File,
    private val userAppData: UserAppData
) {
    private val headByteNum = 3
    private val headByteBuff = ByteArray(headByteNum)
    private var currentState: Int = -1
    private var zipInputStream: ZipInputStream? = null
    private var extCi = 0
    var outDir = File("")

    @SuppressLint("SimpleDateFormat")
    fun startTask(progress: (num: Float) -> Unit) {

        println("提取类型" + ResourceData.extractType.toString())
        when (ResourceData.extractType) {
            EXTRACT_TYPE_SRC -> {
                val zipFile = ZipFile(filePath)
                val maxSize = zipFile.size()
                try {
                    var thisEntry = 0
                    if (ResourceData.outDir == null) return
                    outDir = File(
                        ResourceData.outDir,
                        "${userAppData.name}/${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())}"
                    )
                    currentState = START_STATE
                    val fileInputStream = FileInputStream(filePath)
                    val bufferedInputStream = BufferedInputStream(fileInputStream)
                    zipInputStream = ZipInputStream(bufferedInputStream)
                    var zn: ZipEntry

                    var thisEi = 0
                    var i = 0
                    while ((zipInputStream?.nextEntry.apply { zn = this!! }) != null) {
                        progress(computeProgress(maxSize, i))
                        if (!zn.isDirectory) {
                            selectFile(zn).let {
                                if (it.name.isNotEmpty()) {
                                    createFile(it)
                                }
                            }
                        }
                        i++
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                } finally {
                    progress(100F)
                    when (getCurrentState()) {

                        DEL_STATE -> {
                        }

                        PAUSE_STATE -> {
                        }

                        else -> {
                            currentState = SUCCESS_STATE
                            /*ExtTable.alterExtExtractMsg(extCi, "thisEntry", (0).toString())
                            ExtTable.alterExtExtractMsg(extCi, "state", getCurrentState().toString())
                            */
                        }
                    }
                }


            }

            EXTRACT_TYPE_ICON -> {
//                outDir = File(ResourceData.outDir, "图标")
                try {
                    outDir = File(ResourceData.outDir, "图标")
                    EggUtil.saveDrawable(
                        userAppData.appIcon,
                        File(
                            outDir,
                            "${SimpleDateFormat("yyyy-MM-dd-HH").format(Date())}/${userAppData.appPackageName}.png"
                        ),
                        EggApplication.context,
                        true
                    )
                    progress(100F)
                } catch (e: ClassCastException) {
                    progress(100F)
                }

            }

            EXTRACT_TYPE_PACK -> {
//                outDir = File(ResourceData.outDir, "安装包")
                outDir = File(ResourceData.outDir, "安装包")
                EggUtil.saveFile(
                    context,
                    userAppData.appSource.inputStream(),
                    File(
                        outDir,
                        "${SimpleDateFormat("yyyy-MM-dd-HH").format(Date())}/${userAppData.name}.apk"
                    )
                )

                progress(100F)

            }
        }
    }

    private fun computeProgress(max: Int, p: Int): Float {
        return String.format("%.2f", (p.toFloat() / max.toFloat()) * 100.0).toFloat()
    }

    private fun createFile(file: File) {
        file.parentFile!!.mkdirs()
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(headByteBuff, 0, headByteNum)
        var len: Int
        val buffByte = ByteArray(524)
        while ((zipInputStream?.read(buffByte).apply { len = this!! }) != -1) {
            fileOutputStream.write(buffByte, 0, len)
        }
        fileOutputStream.close()
        zipInputStream?.closeEntry()
    }

    private fun selectFile(zn: ZipEntry): File {
        zipInputStream?.read(headByteBuff, 0, headByteNum)
        val hexStr = byteToHexString(headByteBuff)
        ExtTable.getExtHeadJson().forEach {
            val keyJsonA = ExtTable.getExtHeadJson().getJSONArray(it.key)
            if (keyJsonA.contains(hexStr) && zn.size > ResourceData.fileSize) {
                return File(
                    outDir,
                    "${it.key}/${getFileNameNoEx(getPathName(zn.name))}.${ExtTable.getRelativeExtMsg()
                        .getString(hexStr)}"
                )
            }
        }
        return File("")
    }

    private fun getPathName(string: String): String {
        return string.split("/").let {
            it[it.size - 1]
        }
    }

    private fun getFileNameNoEx(filename: String?): String? {
        if (filename != null && filename.isNotEmpty()) {
            val dot = filename.lastIndexOf('.')
            if (dot > -1 && dot < filename.length) {
                return filename.substring(0, dot)
            }
        }
        return filename
    }

    fun pause() {
        currentState = PAUSE_STATE
//        ExtTable.alterExtExtractMsg(extCi, "state", currentState.toString())
    }

    fun removeTask() {
//        ExtTable.removeExtExtractMsg(extCi)
        currentState = DEL_STATE
    }

    fun getCurrentState(): Int {
        return currentState
    }

    private fun byteToHexString(byteArray: ByteArray): String? {
        var str = ""
        if (byteArray.isEmpty()) {
            return null
        }
        byteArray.forEach { it ->
            val v: Int = it.toInt() and 0xFF
            val hx = Integer.toHexString(v)
            if (hx.length < 2) {
                str += "0"
            }
            str += hx
        }
        return str
    }

}

/*if (thisEntry != thisEi) {
         zipInputStream?.nextEntry
         thisEi += 1
         continue
     }
     val zNum = (i + thisEi)
     if (getCurrentState() == PAUSE_STATE || getCurrentState() == DEL_STATE) {
         if (getCurrentState() == PAUSE_STATE) {
             ExtTable.alterExtExtractMsg(extCi, "thisEntry", zNum.toString())
             ExtTable.alterExtExtractMsg(extCi, "state", getCurrentState().toString())
         }
         break
     }*/

/*
//            val extCJson = ExtTable.getExtExtractMsg()
           var isNewF = true
           var isSuccess = false
          ExtTable.getExtExtractsFile().listFiles()!!.forEach {
               val j = JSON.parseObject(EggIO.readFile(it))
               if (j.getString("filePackage") == userAppData.appPackageName) {
                   val state = j.getIntValue("state")
                   if (state == ResourceData.SUCCESS_STATE) {
                       isSuccess = true
                       return@forEach
                   }
                   thisEntry = j.getInteger("thisEntry")
                   isNewF = false
                   return@forEach
               }
               extCi++
           }

           if (isSuccess) {
               progress(100F)
               return
           }

           if (isNewF) {
               ExtTable.addExtExtractMsg(userAppData)
           } else {
               ExtTable.alterExtExtractMsg(extCi, "state", currentState.toString())
           }*/

/*if (currentState != START_STATE) {
      return
  }*/