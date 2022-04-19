package com.srcbox.file.util

import com.alibaba.fastjson.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class GetResourceFormFile(
    file: File,
    private val outFile: File,
    private val filterJson: JSONObject,
    val onProgress: (progress: Float) -> Unit
) {
    private val bufferSize = 3
    private val bufferByte = ByteArray(bufferSize)
    private val fileArr = ArrayList<File>()
    private lateinit var zipFile: ZipFile
    private var isUFile = false

    init {
        Thread {
            val fileArray = ArrayList<FileData>()
            //验证输出路径并进行优化处理
            if (!file.exists()) {
//                throw RuntimeException("找不到文件：${file}")
                onProgress(-1F)
                return@Thread
            }
            if (file.isDirectory) {
                val fArr = listFile(file)
                fArr.forEach {
                    println(it)
                    if (it.isFile) {
                        fileArray.add(FileData(it.name, null, it))
                    }
                }
            } else {
                zipFile = ZipFile(file)
                val entries = zipFile.entries()
                while (entries.hasMoreElements()) {
                    val zipEntry = entries.nextElement() as ZipEntry
                    fileArray.add(FileData(zipEntry.name, zipEntry, null))
                }
            }
            if (fileArray.size == 0) {
                onProgress(-1F)
                return@Thread
            }
            getFileBytes(fileArray)
            if (!isUFile) {
                onProgress(-1F)
            }
        }.start()
    }

    //得到文件内部的文件流集合
    private fun getFileBytes(arrayList: ArrayList<FileData>) {
        var i = 0
        arrayList.forEach {
            getNextFile(it)
            i++
            onProgress(
                String.format("%.2f", ((i.toFloat() / (arrayList.size).toFloat()) * 100)).toFloat()
            )
        }

    }

    //得到下一个文件
    private fun getNextFile(fileData: FileData) {
        try {
            var inPutSystem: InputStream? = null
            val file = fileData.file
            val zipEntry = fileData.zipEntry
            if (file != null) {
                inPutSystem = file.inputStream()
            }
            if (zipEntry != null) {
                inPutSystem = zipFile.getInputStream(zipEntry)
            }

            val hashMap = filterFile(inPutSystem!!)
            if (hashMap["nameExt"] == "0" || hashMap["nameType"] == "0") return
            val name = getFileNameNoEx(getPathName(fileData.fileName))
            val outFile = File(
                outFile,
                "${hashMap["nameType"]}/${name}.${hashMap["nameExt"]}"
            )

            if (outFile.parentFile != null) {
                if (!outFile.parentFile!!.exists()) {
                    val ism = outFile.parentFile?.mkdirs()
                    if (ism == null)
                        return
                    else {
                        if (!ism)
                            return
                    }
                }
            } else {
                return
            }

            val outPutStream = FileOutputStream(outFile)
            outPutStream.write(bufferByte)
            var len = 0
            val bufferByte = ByteArray(524)
            while (inPutSystem.read(bufferByte).apply { len = this } != -1) {
                outPutStream.write(bufferByte, 0, len)
            }
            outPutStream.close()
            inPutSystem.close()
            /*
            outPutStream.use {
                it.write(inPutSystem.readBytes())
            }*/
            isUFile = true
        } catch (e: FileNotFoundException) {
            return
        }
    }

    private fun filterFile(fileIn: InputStream): HashMap<String, String> {
        val hashMap = HashMap<String, String>()
        hashMap["nameExt"] = "0"
        hashMap["nameType"] = "0"
//        val hex = byteToHexString(byteArray.copyOf(bufferSize))
        fileIn.read(bufferByte, 0, bufferSize)
        val hex = byteToHexString(bufferByte)
        filterJson.forEach {
            val it1ValJsonObject = (it.value as JSONObject)
            if (!it1ValJsonObject.getBoolean("on")){
                return@forEach
            }

            it1ValJsonObject.forEach { its ->
                if (its.key == hex) {
                    hashMap["nameExt"] = its.value.toString()
                    hashMap["nameType"] = it.key
                    return hashMap
                }
            }
        }
        return hashMap
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

    private fun listFile(file: File): ArrayList<File> {
        if (file.isDirectory) {
            file.listFiles()!!.forEach {
                listFile(it)
            }
        }
        if (file.isDirectory) {
            fileArr.addAll(file.listFiles()!!)
        } else {
            fileArr.add(file)
        }
        return fileArr
    }

    data class FileData(val fileName: String, val zipEntry: ZipEntry?, val file: File?)
}