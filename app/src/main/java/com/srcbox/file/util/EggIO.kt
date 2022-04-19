package com.srcbox.file.util

import android.content.Context
import java.io.*
import java.lang.Exception

object EggIO {
    private const val buffer = 524


    fun copyFileTo(inO: InputStream, outP: OutputStream) {
        var len = 0
        val bytes = ByteArray(buffer)
        while (inO.read(bytes).apply { len = this } != -1) {
            outP.write(bytes, 0, len)
        }
        outP.flush()
        outP.close()
        inO.close()
    }

    /***
     * @use 将Assets里的文件拷贝到SD里
     * @param assetsFileName assets文件名字
     * @param toPath 目标路径
     */

    fun assetsFileToSdPath(context: Context, assetsFile: String, toPath: File): Boolean {
        val isD = checkDirExist(toPath.parentFile!!)
        if (!isD) return false
        val inP = context.assets.open(assetsFile)
        val outP = FileOutputStream(toPath)
        val bytes = ByteArray(buffer)
        var len = 0
        while (inP.read(bytes).apply { len = this } != -1) {
            outP.write(bytes, 0, len)
        }
        outP.flush()
        outP.close()
        inP.close()
        return true
    }

    /***
     * @use 检查文件是否存在，不存在则创建
     */

    private fun checkDirExist(file: File): Boolean {
        return if (!file.exists()) {
            file.mkdirs()
        } else {
            true
        }
    }

    /***
     * @use 读取文件
     * @param file 文件对象
     */
    fun readFile(file: File): String {
        if (!file.exists()) {
            return ""
        }
        val strB = StringBuilder()
        val fileIn = FileInputStream(file)
        val buffByte = ByteArray(buffer)
        var len: Int
        while ((fileIn.read(buffByte, 0, buffer).apply { len = this }) != -1) {
            strB.append(String(buffByte, 0, len))
        }
        return strB.toString().trimIndent()
    }

 /*   fun readFileBytes(file: File): ByteArray {
        if (!file.exists()) {
            return
        }
        val strB = StringBuilder()
        val fileIn = FileInputStream(file)
        val buffByte = ByteArray(buffer)
        var len: Int
        while ((fileIn.read(buffByte, 0, buffer).apply { len = this }) != -1) {
            strB.append(String(buffByte, 0, len))
        }
        return strB
    }*/

    /***
     * @use 读取文件
     * @param fileIn 文件输入流
     */

    fun readFile(fileIn: InputStream): String {
        val strB = StringBuilder()
        val buffByte = ByteArray(buffer)
        val isr = InputStreamReader(fileIn)
        val br = BufferedReader(isr)
        try {
            while (br.ready()) {
                strB.append(br.readLine() + "\n")
            }
        } catch (e: Exception) {
            println(e.message + "错误")
        }
//        println(strB.toString().trimIndent())
        return strB.toString().trimIndent()
    }

    /***
     * @use 创建文件
     * @param file 文件对象
     * @param content 文件内容
     */

    fun writeFile(file: File, content: String): Boolean {
        var isMkdirOk = true
        println(file)
        if (!file.parentFile!!.exists()) {
            isMkdirOk = file.parentFile!!.mkdirs()
        }
        if (isMkdirOk) {
            val fileOutSm = FileOutputStream(file)
            fileOutSm.write(content.toByteArray())
            fileOutSm.flush()
            fileOutSm.close()
            return true
        }
        return false
    }


    /*fun writeFile(fileOutputStream: FileOutputStream, content: String): Boolean {
        val fileO = File(filePath)
        var isMkdirOk = true
        if (!fileO.parentFile!!.exists()) {
            isMkdirOk = fileO.parentFile!!.mkdirs()
        }
        if (isMkdirOk) {
            val fileOutSm = FileOutputStream(fileO)
            fileOutSm.write(content.toByteArray())
            fileOutSm.flush()
            fileOutSm.close()
            return true
        }
        return false
    }*/
}
/*

var fileType = 0
if (!file.isFile) {
    fileType = 1
}
if (file.exists()) {
    return true
} else {
    if(fileType == 0){
        if(!file.parentFile!!.exists()){
            if(!file.parentFile!!.mkdirs()) return false
        }
        return false
    }
}
return false*/