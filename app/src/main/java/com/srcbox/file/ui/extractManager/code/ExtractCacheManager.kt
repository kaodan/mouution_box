package com.srcbox.file.ui.extractManager.code

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import com.srcbox.file.application.EggApplication
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object ExtractCacheManager {
    fun getExtractTaskMessage(fileName: String): ExtractTaskMessage? {
        val e = getExtractMessages() ?: return null
        return e[fileName]
    }

    fun addExtractTaskData(extractTaskMessage: ExtractTaskMessage): Boolean {
        val e = getExtractMessages()
        e[extractTaskMessage.file.path] = extractTaskMessage
        saveExtractMessages(e)
        /*var isU = false
e.forEach {
    if (it.value.file.path == extractTaskMessage.file.path) {
        isU = true
    }
}
if (!isU){
    e.add(extractTaskMessage)
}*/
        return true
    }

    fun successTask(successHashMap: HashMap<String, ExtractTaskMessage>) {
        EggApplication.context.getSharedPreferences("data", Context.MODE_PRIVATE).edit {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val oos = ObjectOutputStream(byteArrayOutputStream)
            oos.writeObject(successHashMap)
            val byteStr = String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT))
            putString("successExtractTaskMessages", byteStr)
        }
    }

    fun getSuccessTask(): HashMap<String, ExtractTaskMessage> {
        val emptyHash = HashMap<String, ExtractTaskMessage>()
        val es = EggApplication.context.getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("successExtractTaskMessages", null)
            ?: return emptyHash
        val byteInputSystem = ByteArrayInputStream(Base64.decode(es.toByteArray(), Base64.DEFAULT))
        val objectInputS = ObjectInputStream(byteInputSystem)
        val objResult = objectInputS.readObject()
        objectInputS.close()
        byteInputSystem.close()
        return objResult as HashMap<String, ExtractTaskMessage>
    }

    fun removeTaskData(fileName: String) {
        val e = getExtractMessages()
        e.remove(fileName)
        saveExtractMessages(e)
        e.forEach {
            println("删除后的数据是${it.key} ${it.value.state}")
        }
    }

    fun setExtractTaskData(fileName: String, extractInstance: ExtractTaskMessage?): Boolean {
        val e = getExtractMessages() ?: return false
        if (extractInstance == null) return false
        e[fileName] = extractInstance
        saveExtractMessages(e)
        return true
    }

    private fun saveExtractMessages(hashMap: HashMap<String, ExtractTaskMessage>) {
        EggApplication.context.getSharedPreferences("data", Context.MODE_PRIVATE).edit {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val oos = ObjectOutputStream(byteArrayOutputStream)
            oos.writeObject(hashMap)
            val byteStr = String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT))
            putString("extractTaskMessages", byteStr)
            oos.close()
            byteArrayOutputStream.close()
        }
    }

    fun getExtractMessages(): HashMap<String, ExtractTaskMessage> {
        val emptyHash = HashMap<String, ExtractTaskMessage>()
        val es = EggApplication.context.getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("extractTaskMessages", null)
            ?: return emptyHash
        val byteInputSystem = ByteArrayInputStream(Base64.decode(es.toByteArray(), Base64.DEFAULT))
        val objectInputS = ObjectInputStream(byteInputSystem)
        val objResult = objectInputS.readObject()
        objectInputS.close()
        byteInputSystem.close()
        return objResult as HashMap<String, ExtractTaskMessage>
    }
}