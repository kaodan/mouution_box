package com.srcbox.file.util

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Node
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.URL
import java.util.regex.Pattern

class WebAllPageSource(private val url: URL, var baseRoot: File) {
    private val phoneUserAgent =
        "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Mobile Safari/537.36"

    fun getSource() {

        var htmlText = ""

        val dom = Jsoup.connect(url.toString()).userAgent(phoneUserAgent).get()
        htmlText = dom.toString()
        baseRoot = File(baseRoot, dom.title())
        baseRoot.mkdirs()
        val pa =
            Pattern.compile("[\\u0022|\\u0027]((http?:|//|/).*?)[\\u0022|\\u0027]")
                .matcher(htmlText)
        var matcherStart = 0
        while (pa.find(matcherStart)) {
            try {
                val link = pa.group(1)
                link?.let {
                    val beforeTwoStr = it.substring(0, 2)
                    var newLink = if (beforeTwoStr.startsWith("//")) {
                        "http:$link"
                    } else {
                        link
                    }

                    if (beforeTwoStr.startsWith("/") && !beforeTwoStr.startsWith("//")) {
                        newLink = "${url.protocol}://${url.host}$it"
                    }

                    val newUrl = URL(newLink.trim())
                    val filePath = newUrl.file

                    download(
                        newLink,
                        File(baseRoot, filePath)
                    )
                    val v = filePath.substring(1, filePath.length)
                    htmlText = htmlText.replaceFirst(it, v)
                }
            } catch (e: Exception) {
            }
            matcherStart = pa.end()

        }
        writeContent(File(baseRoot, "index.html"), htmlText)
    }


    fun wipeEscape(string: String): String {
        val pattern = Pattern.compile("\\s*|\\t|\\r|\\n").matcher(string)
        return pattern.replaceAll("")
    }


    fun getBaseUrl(): String {
        url.host.split(".").apply {
            var doMain = "${get(size - 2)}.${get(size - 1)}"
            if (doMain.contains("ac.cn") || doMain.contains("com.cn") || doMain.contains("net.cn")) {

                doMain = "${get(size - 3)}.${get(size - 2)}.${get(size - 1)}"
            }
            return doMain
        }
    }

    fun getFileName(string: String): String {
        val p = Pattern.compile("[\\u003d|\\u002f|\\u0026]").split(string)
        return p[p.size - 1]
    }


    fun writeContent(file: File, content: String) {
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
//        println(file)
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


fun main() {
    val time1 = System.currentTimeMillis()
    val url = "https://www.coolapk.com/apk/com.srcbox.file"
    WebAllPageSource(URL(url), File("files")).getSource()
    println(System.currentTimeMillis() - time1)
}


/*

//sccc
/aavv


 */
//    println("aavvv".substring(0, 2))
/*if ("//s".startsWith("/")) {
    println("2")
}*/


//        val domBody = dom.body()
//                        val name = getFileName(newUrl.toString())
//                        println(newUrl.query)
//    download("http://my.chinaz.com/avatar/user.png",File("m.png"))


//                    val link = it.attr("abs:${attributeKey}")
//                    println(link)
//            println(it.attributes())
//            val childSize = it.childNodeSize()
//        val topDoMains = ".com|.com.cn|.cn|.ac.cn|.top|.me|.net|"
//                        println(link)
//                        println(File(url.file).name)
//                        download(newLink, File(baseRoot,""))
/*println(domBody)
getNode(domBody)*/


/*private fun getNode(node: Node) {
        node.childNodes().forEach {
            it.attributes().forEach { attribute ->
                val attributeKey = attribute.key
                val attributeVal = attribute.value
//                println(attributeKey)

                if (attributeVal.isNotEmpty()) {
                    if (attributeVal.startsWith("//") || attributeVal.startsWith("http")) {
                        val link = URL(it.attr("abs:${attributeKey}"))
//                        println(link)
                    }
                }
            }
            getNode(it)
        }
    }*/

//<img src="" height="120" width="120" onerror="javascript:this.src='//my.chinaz.com/avatar/user.png';">
