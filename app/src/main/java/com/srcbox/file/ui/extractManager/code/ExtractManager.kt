package com.srcbox.file.ui.extractManager.code

import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.alibaba.fastjson.JSONObject
import com.egg.extractmanager.ExtractInstance
import com.egg.extractmanager.ExtractListener
import java.io.File

object ExtractManager {
    private var isOnce: Boolean? = null
    var outDir = File("")
    var ruleJson: JSONObject? = null
    var type: Int = -1
    const val STATE_START = 0
    const val STATE_PAUSE = 1
    const val STATE_CANCEL = 2
    const val STATE_SUCCESS = 3
    const val HANDLER_PROGRESS = 0
    const val HANDLER_START = 1
    const val HANDLER_PAUSE = 2
    const val HANDLER_CANCEL = 3
    const val TYPE_FILE = 0
    const val TYPE_DIR = 1
    var handler: Handler? = null
    val extractTaskHashMap: HashMap<String, ExtractInstance> =
        HashMap<String, ExtractInstance>()

    fun syncLocal() {
        val newHash = HashMap<String, ExtractInstance>()
        ExtractCacheManager.getExtractMessages().forEach {
            when (type) {
                TYPE_FILE -> {
                    if (!extractTaskHashMap.contains(it.key)) {
                        val exApp = ExtractResourceFormApp(it.value)
                        newHash[it.key] = exApp
                        exApp.initData()
                        exApp.isNewTask = false
                    }
                }
            }
        }
        extractTaskHashMap.putAll(newHash)
    }

    //添加一个任务
    fun addTask(extractInstance: ExtractInstance) {
        if (extractInstance.getState() != STATE_START && extractInstance.getIsNewTask()) {
            val fileName = extractInstance.getMessages().file.path
            extractInstance.let {
                extractTaskHashMap[fileName] = it
                it.setIsNewTask(true)
                extractInstance.initData()
            }
        }
    }

    fun startAllTask() {
        extractTaskHashMap.forEach {
            println("检测到：${it.value.getIsNewTask()} ${it.value.getMessages().state}")
            if (!it.value.getIsNewTask() && it.value.getMessages().state == STATE_SUCCESS) {
                println(it.value.getIsNewTask())
                println("老任务")
            } else {
                listerTask(it.value, it.key)
                it.value.start()
            }

        }
    }

    fun list(): ArrayList<ExtractTaskMessage> {
        val newExtractMessage = ArrayList<ExtractTaskMessage>()
        extractTaskHashMap.forEach {
            val message = it.value.getMessages()
            if (it.value.getIsNewTask()) {
                newExtractMessage.add(message)
            }
        }
        return newExtractMessage
    }

    //暂停一个任务
    fun pauseTask(fileName: String) {
        val extractInstance: ExtractInstance? = extractTaskHashMap[fileName]
        if (extractInstance?.getState() == STATE_START) {
            extractInstance.pause()
        }
    }

    //删除一个任务
    fun removeTask(fileName: String) {
        extractTaskHashMap[fileName].let {
            it?.cancel()
            it?.setListener(null)
            extractTaskHashMap.remove(fileName)
            ExtractCacheManager.removeTaskData(fileName)
        }
    }

    //列表
    private fun listerTask(
        extractInstance: ExtractInstance,
        fileName: String
    ) {
        extractInstance.setListener(object : ExtractListener {
            override fun onProgress(float: Float) {
                val msg = Message()
                val bundle = Bundle()
                bundle.putString("tag", fileName)
                bundle.putString("value", float.toString())
                msg.what = HANDLER_PROGRESS
                msg.data = bundle
                handler?.sendMessage(msg)
                ExtractCacheManager.setExtractTaskData(
                    fileName,
                    extractTaskHashMap[fileName]?.getMessages()
                )
            }

            override fun onStart() {
                val msg = Message()
                val bundle = Bundle()
                bundle.putString("tag", fileName)
                msg.what = HANDLER_START
                msg.data = bundle
                handler?.sendMessage(msg)
                extractTaskHashMap[fileName]?.getMessages().let {
                    if (it != null) {
                        ExtractCacheManager.addExtractTaskData(it)
                    }
                }
            }

            override fun onPause() {
                val msg = Message()
                val bundle = Bundle()
                bundle.putString("tag", fileName)
                msg.data = bundle
                msg.what = HANDLER_PAUSE
                handler?.sendMessage(msg)
                ExtractCacheManager.setExtractTaskData(
                    fileName,
                    extractTaskHashMap[fileName]?.getMessages()
                )
            }

            override fun onCancel() {
                val msg = Message()
                val bundle = Bundle()
                bundle.putString("tag", fileName)
                msg.data = bundle
                msg.what = HANDLER_CANCEL
                handler?.sendMessage(msg)
                ExtractCacheManager.setExtractTaskData(
                    fileName,
                    extractTaskHashMap[fileName]?.getMessages()
                )
            }

            override fun onSuccess() {
                val suc = ExtractCacheManager.getSuccessTask()
                extractTaskHashMap[fileName]?.let {
                    suc[fileName] = it.getMessages()
                }
                ExtractCacheManager.setExtractTaskData(
                    fileName,
                    extractTaskHashMap[fileName]?.getMessages()
                )
                ExtractCacheManager.successTask(suc)
            }
        })
    }
}


/*val newArrayList = ArrayList<ExtractTaskMessage>()
extractTaskList.forEach { it ->
    it.getMessages()?.let { itm ->
        newArrayList.add(itm)
    }
}*/
//                ExtractCacheManager.saveExtractMessages(newArrayList)