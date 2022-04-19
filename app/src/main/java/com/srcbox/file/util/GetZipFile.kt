package com.srcbox.file.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipInputStream

class GetZipFile(private var zipFilePath: String, var extJsonMsg: String) {
    var outputDir: String? = null
    var headByteI = 3
    val headByteBuff = ByteArray(headByteI)
    private val bufferNum: Int = 1024
    private fun initCheck(): Boolean {
        if (outputDir?.isEmpty()!!) {
            println("The path cannot be empty!")
            return false
        }
        val outputDirFile = File(outputDir!!)
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs()
        }
        val fileObj = File(zipFilePath)
        if (!fileObj.isFile) {
            println("not found File${outputDir}${fileObj.name}")
            return false
        }
        return true
    }

    fun goExtractFile(progress: (currentFile: Int, isOk: Boolean) -> Unit): Boolean {
        try {
            if (!initCheck()) return false
            val zipInFile = ZipInputStream(FileInputStream(zipFilePath))
            var zipEntry: ZipEntry?
            var currentFile = 1
            do {
                zipEntry = zipInFile.nextEntry
                if (zipEntry != null) {
                    progress(currentFile, false)
                    if (!zipEntry.isDirectory) {
                        val fileName = zipEntry.name.split("/")[zipEntry.name.split("/").size - 1]
//                        extJsonMsg = deleteExtTab(extJsonMsg)
                        val fileUseType =
                            filterFile(getFileByteHead(zipInFile), EggUtil.getPathExtend(fileName))
                        if (fileUseType[0] == "false") continue
                        val fName = EggUtil.reNameExt(
                            fileName,
                            JSON.parseObject(extJsonMsg)!!.getJSONObject(fileUseType[0])
                                .getString(fileUseType[1])
                        )
                        val fileObj = File("$outputDir${fileUseType[0]}/$fName")
                        createFile(zipInFile, fileObj)

                        currentFile++
                    }
                } else {
                    break
                }
            } while (true)
            progress(0xfffffff, true)
            return true
        } catch (e: ZipException) {
            println(e.message)
        }
        return false
    }

    private fun createFile(zipInputStream: ZipInputStream, fileObj: File) {
        try {
            if (zipInputStream.available() == 0) {
                return
            }

            if (fileObj.parentFile?.exists() != true) fileObj.parentFile?.mkdirs()
            val fileOutputStream = FileOutputStream(fileObj)
            val buffer = ByteArray(bufferNum)
            fileOutputStream.write(headByteBuff, 0, headByteI)
            var len: Int
            while ((zipInputStream.read(buffer, 0, bufferNum).apply { len = this }) != -1) {
                fileOutputStream.write(buffer, 0, len)
            }
            fileOutputStream.close()
            zipInputStream.closeEntry()
        } catch (e: ZipException) {
            println(e.message)
        }
    }

    private fun checkByteHead(extendMap: JSONObject, fileByteHead: String?): Boolean {
        return extendMap.containsKey(fileByteHead)
    }

    private fun checkExtend(extendMap: JSONObject, extend: String): Boolean {
        return extendMap.containsValue(extend)
    }

    private fun useCheckMethods(): String {
        return "byteHead"
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

    public fun getFileByteHead(zipInFile: InputStream): String? {
        zipInFile.read(headByteBuff, 0, headByteBuff.size)
        return byteToHexString(headByteBuff)
    }

/*
    private fun getPathExtend(name: String): String {
        return name.split(".")[name.split(".").size - 1]
    }*/

    private fun deleteExtTab(extend: String): String {
        val jsonO = JSON.parseObject(extend)
        val jsonOC = JSON.parseObject(extend)
        jsonO.forEach {
            //            println(JSON.parseObject(it.value.toString()).getBoolean("on"))
            if (!JSON.parseObject(it.value.toString()).getBoolean("on")) jsonOC.remove(it.key)
        }
        return jsonOC.toString()
    }

    fun filterFile(byteHeadStr: String?, extend: String): ArrayList<String?> {
        val arrList = ArrayList<String?>()

        val jsonObject = JSON.parseObject(extJsonMsg)
//        println(jsonObject.toJSONString())
        jsonObject.forEach {
            val jsonItem = JSON.parseObject(it.value.toString())
//            println(jsonItem.toJSONString())
//            println(it.key)
            val b = jsonItem.getBoolean("on")
            println(b)
            if (b){
                arrList.add(0, it.key)
                arrList.add(1, byteHeadStr)
                if (checkByteHead(jsonItem, byteHeadStr)) return arrList
            }

            /*if (!JSON.parseObject(it.value.toString()).getBoolean("on")) {
                arrList.add(0, "false")
                return arrList
            }
            when (useCheckMethods()) {
                "extend" -> {
                    arrList.add(0, it.key)
                    arrList.add(1, extend)
                    if (checkExtend(jsonItem, extend)) return arrList
                }
                "byteHead" -> {
                    arrList.add(0, it.key)
                    arrList.add(1, byteHeadStr)
                    if (checkByteHead(jsonItem, byteHeadStr)) return arrList
                }
                else -> {
                    arrList.add(0, "false")
                    return arrList
                }
            }*/
        }
        arrList.add(0, "false")
        return arrList
    }
}