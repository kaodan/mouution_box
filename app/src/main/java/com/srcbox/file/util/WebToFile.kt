package com.srcbox.file.util

import android.annotation.SuppressLint
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class WebToFile(var path: File, private val url: String) {
    @SuppressLint("SimpleDateFormat")
    suspend fun getSrc(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                var date = ""
                val jH = Jsoup.connect(url).get()
                date = if (Build.VERSION.SDK_INT >= 24) {
                    SimpleDateFormat("YYYY MM dd hh:mm:ss").format(System.currentTimeMillis())
                } else {
                    SimpleDateFormat("yyyy MM dd hh:mm:ss").format(System.currentTimeMillis())
                }
                val title = jH.title() ?: date

                path = File(path, "${URL(url).host}/${title}")
                val imgs = jH.getElementsByTag("img")
                val links = jH.getElementsByTag("link")
                val scripts = jH.getElementsByTag("script")

                imgs.forEach {
                    kotlin.run {
                        try {
                            val link = it.attr("abs:src") ?: return@run
                            val urlO = URL(link)
                            val file = File(path, urlO.file)
                            file.parentFile?.mkdirs()
                            val fi = urlO.file.substring(1)
                            it.attr("src", fi)
                            download(link, file)
                        } catch (e: Exception) {
                            println(e.message)
                        }
                    }
                }

                scripts.forEach {
                    val link = it.attr("abs:src")
                    if (link.isNotEmpty()) {
                        val urlO = URL(link)
                        val file = File(path, urlO.file)
                        file.parentFile?.mkdirs()
                        val fi = urlO.file.substring(1)
                        it.attr("src", fi)
                        download(link, file)
                    }
                }

                links.forEach {
                    val href = it.attr("abs:href")
                    val urlx = URL(href)
                    val urlF = urlx.file
                    if (urlF.isNotEmpty()) {
                        val fi = urlF.substring(1)
                        it.attr("href", fi)
                        val file = File(path, urlF)
                        file.parentFile?.mkdirs()
                        download(href, file)
                    } else {
                        it.attr("href", href)
                    }
                }

                val file = File(path, "index.html")
                file.parentFile?.mkdirs()
                val fileOutStream = FileOutputStream(file)
                fileOutStream.write(jH.html().toByteArray(Charset.forName("UTF-8")))
                fileOutStream.close()
                val file2 = File(path, "说明——配置必看.txt")
                file2.parentFile?.mkdirs()
                val fileOutStream2 = FileOutputStream(file2)
                fileOutStream2.write(
                    """
                    此源码由《山盒》APP获取，软件下载地址：https://www.coolapk.com/apk/com.srcbox.file
                """.trimIndent().toByteArray(Charset.forName("UTF-8"))
                )
                fileOutStream2.close()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun download(link: String, file: File) {
        try {
            val wI = file.absoluteFile.path.indexOf("?")
            var f = file
            if (wI != -1) {
                f = File(file.absoluteFile.path.substring(0, wI))
            }
            val url = URL(link)
            val connection = url.openConnection()

            connection.connectTimeout = 5000
            val inputInputStream = connection.getInputStream()
            val bufferedOutputStream = BufferedOutputStream(FileOutputStream(f))
            val byte = ByteArray(524)
            var len = 0
            while ((inputInputStream.read(byte).apply { len = this }) != -1) {
                bufferedOutputStream.write(byte, 0, len)
            }
            bufferedOutputStream.close()
            inputInputStream.close()
        } catch (e: Exception) {
            println(e.message)
        }
    }
}


fun main() {
    GlobalScope.launch {
        WebToFile(File("fks"), "https://www.coolapk.com/apk/com.srcbox.file").getSrc()
    }
    Thread.sleep(1000)
}