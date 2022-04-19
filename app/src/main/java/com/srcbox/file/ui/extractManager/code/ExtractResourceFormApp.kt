package com.srcbox.file.ui.extractManager.code

import com.alibaba.fastjson.JSONObject
import com.egg.extractmanager.ExtractInstance
import com.egg.extractmanager.ExtractListener
import com.srcbox.file.util.EggUtil
import kotlinx.coroutines.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ExtractResourceFormApp(
    private var extractTaskMessage: ExtractTaskMessage,
    private var extractListener: ExtractListener? = null
) : ExtractInstance {
    private val headSize = 3
    private val headBuffer = ByteArray(headSize)
    private var zipFile: ZipFile? = null
    private var job: Job? = null
    private var position: Int? = null
    var isNewTask: Boolean = false

    companion object {
        val jobHashMap = HashMap<String, Job>()
    }

    override fun start() {
        isNewTask = false
        if (ExtractManager.outDir.path.isEmpty()) {
            return
        }
        if (jobHashMap[extractTaskMessage.file.path] != null) {
            println("已退出")
            return
        }
        val fileName = extractTaskMessage.file.path

        //启动
        extractTaskMessage.state = ExtractManager.STATE_START
        extractListener?.onStart()
        job = GlobalScope.launch(Dispatchers.IO) {
            try {
                job?.let {
                    jobHashMap[fileName] = it
                }
                var thisPos = 0
                zipFile = ZipFile(extractTaskMessage.file)
                zipFile?.let { it ->
                    val zipFileSize = it.size()
                    val entries = it.entries()
                    extractTaskMessage.total = zipFileSize
                    if (extractTaskMessage.thisFilePos == zipFileSize) {
                        extractTaskMessage.thisFilePos = 0
                    }
                    if (extractTaskMessage.thisFilePos != 0) {
                        extractTaskMessage.thisFilePos -= 1
                        for (index in 0 until extractTaskMessage.thisFilePos) {
                            thisPos++
                            entries.nextElement()
                        }
                    }
                    while (entries.hasMoreElements() && isActive) {
                        thisPos++
                        extractTaskMessage.thisFilePos = thisPos
                        val zipEntry = entries?.nextElement()
                        val ins = it.getInputStream(zipEntry)
                        extractListener?.onProgress(EggUtil.computeProgress(thisPos, zipFileSize))
                        val f = filter(ins, zipEntry!!) ?: continue
                        saveFile(
                            ins,
                            File(File(ExtractManager.outDir, extractTaskMessage.appName), f)
                        )
                    }
                    if (extractTaskMessage.state != ExtractManager.STATE_PAUSE) {
                        extractTaskMessage.state = ExtractManager.STATE_SUCCESS
                    }
                }
            } catch (e: Exception) {
                println("错误是${e.message}")
                extractTaskMessage.state = ExtractManager.STATE_CANCEL
            } finally {
                jobHashMap.remove(fileName)
                ExtractManager.extractTaskHashMap.remove(fileName)
                if (extractTaskMessage.state != ExtractManager.STATE_PAUSE) {
                    println("不等于")
                    extractListener?.onSuccess()
                }
                println("完成后的状态是：${extractTaskMessage.state}")
            }
        }
    }

    override fun pause() {
        //暂停
        extractTaskMessage.state = ExtractManager.STATE_PAUSE
        job?.cancel()
        extractListener?.onPause()
    }

    override fun cancel() {
        //关闭
        extractTaskMessage.state = ExtractManager.STATE_CANCEL
        job?.cancel()
        jobHashMap.remove(extractTaskMessage.file.path)
        ExtractCacheManager.removeTaskData(extractTaskMessage.file.path)
        extractListener = null
        extractTaskMessage.thisFilePos = 0
        extractTaskMessage.total = 0
        extractListener?.onCancel()
    }

    override fun setListener(extractListener: ExtractListener?) {
        this.extractListener = extractListener
    }

    override fun getListener(): ExtractListener? {
        return extractListener
    }

    override fun getState(): Int {
        return extractTaskMessage.state
    }

    override fun getMessages(): ExtractTaskMessage {
        return extractTaskMessage
    }

    override fun getIsNewTask(): Boolean {
        return isNewTask
    }

    override fun setIsNewTask(boolean: Boolean) {
        isNewTask = boolean
    }

    override fun setPosition(int: Int) {
        this.position = int
    }

    override fun initData() {
        ExtractCacheManager.getExtractTaskMessage(extractTaskMessage.file.path)?.let {
            this.extractTaskMessage = it
        }
    }

    private fun filter(input: InputStream, zipEntry: ZipEntry): String? {
        input.read(headBuffer, 0, headSize)
        val headByte = byteToHexString(headBuffer)
        ExtractManager.ruleJson?.forEach { it1 ->
            val it1ValJsonObject = (it1.value as JSONObject)
            if (!it1ValJsonObject.getBoolean("on")){
                return@forEach
            }
            it1ValJsonObject.forEach {
                if (it.key == headByte) {
                    return "${it1.key}/${getFileNameNoEx(getPathName(zipEntry.name))}.${it.value}"
                }
            }
        }
        return null
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

    private fun saveFile(input: InputStream, file: File): Boolean {
        try {
            file.parentFile?.mkdirs()
            val fos = FileOutputStream(file)
            var len = 0
            val buffer = ByteArray(524)
            fos.write(headBuffer)
            while (input.read(buffer).apply { len = this } != -1) {
                fos.write(buffer, 0, len)
            }


            fos.flush()
            fos.close()
            input.close()
        } catch (e: FileNotFoundException) {
            file.parentFile?.mkdirs()
            return false
        }
        return true
    }
}