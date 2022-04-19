package com.srcbox.file.util

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.math.abs

//        val imgs = webAll.select("img")

class WebAllPageSourceX(
    val url: String,
    private var ourFile: File,
    private val cookies: HashMap<String, String> = HashMap()
) {
    private val phoneUserAgent =
        "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Mobile Safari/537.36"

    companion object {
        private val urls = ArrayList<String>()
    }

    fun getSource() {
        try {
     /*       if (urls.contains(url)) {
                return
            }*/
            urls.add(url)
            ourFile.mkdirs()
            val webAll = Jsoup.connect(url).userAgent(phoneUserAgent).cookies(cookies).get()
            ourFile = File(ourFile, webAll.title())
            alertNodesLink(webAll, "img")
            alertNodesLink(webAll, "script")
            alertNodesLink(webAll, "link")


            /*webAll.select("a").forEach {
                val aAbsHref = it.attr("abs:href")
                if (aAbsHref.contains(url)) {
                    val aHref = it.attr("href")
                    WebAllPageSourceX(aAbsHref, File(ourFile, aHref)).getSource()
                }

                *//*val uuRlFile = URL(url).file
                if (aAbsHref.isNotEmpty()) {
                    println("$url   $aAbsHref")
                    val absUrlFile = URL(aAbsHref).file
                    println("$uuRlFile   $absUrlFile")
                    val aHref = it.attr("href")
//                    it.attr()
                    WebAllPageSourceX(aAbsHref, File(ourFile, aHref)).getSource()
                }*//*
            }*/


            writeContent(File(ourFile, "index.html"), webAll.toString())
        } catch (e: Exception) {
            println(e.message)
        }

    }


    private fun alertNodesLink(document: Document, tag: String) {
        document.select(tag).forEach { element ->
            val srcLink = element.attr("abs:src")
            val hrefLink = element.attr("abs:href")

            srcLink?.let {
                try {
                    if (it.trim().isNotEmpty()) {
                        val file = URL(srcLink).file
                        download(srcLink, File(ourFile, file))
                        element.attr("src", with(file) {
                            substring(1, length)
                        })
                    }
                } catch (e: Exception) {
                }
            }

            hrefLink?.let {
                try {
                    if (it.trim().isNotEmpty()) {
                        val file = URL(hrefLink).file
                        download(hrefLink, File(ourFile, file))
                        element.attr("href", with(file) {
                            substring(1, length)
                        })
                    }
                } catch (e: Exception) {
                }
            }

        }
    }

    /*fun getNodes(document: Document, tag: String): Int {
        return document.select(tag).size
    }*/

    private fun writeContent(file: File, content: String) {
        val inputStream = content.byteInputStream()
        file.createNewFile()
        val outputStream = FileOutputStream(file)
        var len = 0
        val byteBuff = ByteArray(524)
        while ((inputStream.read(byteBuff).apply { len = this }) != -1) {
            outputStream.write(byteBuff, 0, len)
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }

    private fun download(url: String, file: File) {
        var newFile = file
        file.absolutePath.apply {
            val whySymbol = indexOf("?")
            if (whySymbol != -1) {
                newFile = File(substring(0, whySymbol))
            }
        }
        try {
            newFile.parentFile?.let {
                if (!it.exists()) {
                    it.mkdirs()
                }
            }
            val okHttpClient = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val okHttpExecute = okHttpClient.newCall(request).execute()
            val inputStream = okHttpExecute.body?.byteStream()
            val outPutStream = FileOutputStream(newFile)
            var len = 0
            val buffByte = ByteArray(524)
            inputStream?.let {
                while ((inputStream.read(buffByte).apply { len = this }) != -1) {
                    outPutStream.write(buffByte, 0, len)
                }

                outPutStream.flush()
                outPutStream.close()
                inputStream.close()
            }
        } catch (e: Exception) {
            println(e.message)
        }

    }
}
