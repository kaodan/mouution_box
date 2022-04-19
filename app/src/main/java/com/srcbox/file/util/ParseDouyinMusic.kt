package com.srcbox.file.util

import com.alibaba.fastjson.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.File
import java.net.URL
import java.util.regex.Pattern

class ParseDouyinMusic {
    fun getNoneWatermark(
        url: String,
        outDir: File,
        onProgress: (p: Float) -> Unit,
        onVideoUrl: (url: String) -> Unit
    ) {
        /*val response = Jsoup.connect(url).followRedirects(true).execute()
        val jSoup = Jsoup.connect(response.url().toString())
            .userAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Mobile Safari/537.36")
            .get()
        val itemId = subScriptVal(jSoup.data(), "itemId")
        val dytk = subScriptVal(jSoup.data(), " dytk")*/

        val pUrl: String
        pUrl = if (URL(url).host.contains("iesdouyin.com")) {
            url
        } else {
            val response = Jsoup.connect(url).followRedirects(true).execute()
            response.url().toString()
        }

        /*Jsoup.connect(pUrl)
            .userAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Mobile Safari/537.36")
            .get()*/
//        val itemId = subScriptVal(jSoup.data(), "itemId")
        var itemId: String? = ""
        val pattern = Pattern.compile("video/(.\\d+)/")
            .matcher(pUrl)
        if (pattern.find()) {
            itemId = pattern.group(1)
        }



        val lUrl =
            "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=$itemId&dytk="
        val okHttp1 = OkHttpClient()
        val json = JSON.parseObject(
            okHttp1.newCall(Request.Builder().url(lUrl).build()).execute().body!!.string()
        )
        val musicJson =
            json.getJSONArray("item_list").getJSONObject(0).getJSONObject("music")
        val musicTitle = musicJson.getString("title")
        val musicPlayUri = musicJson.getJSONObject("play_url").getString("uri")
        onVideoUrl(musicPlayUri)
        val f = File(outDir, "${musicTitle}.mp3")
        EggUtil.downloadFile(musicPlayUri, true, f, onProgress)
    }

    private fun subScriptVal(source: String, key: String): String {
        val i1 = source.indexOf(key)
        val i2 = source.indexOf("\n", i1 + 1)
        if (i1 == -1 || i2 == -1) {
            return ""
        }
        return source.substring(i1, i2).replace("$key: \"", "").replace("\"", "").replace("}", "")
            .replace(")", "").replace(";", "").replace(",", "").trim()
    }
}