package com.srcbox.file.util

import android.util.Log
import com.alibaba.fastjson.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.File
import java.net.URL
import java.util.regex.Pattern

class ParseDouyinWateMark {
    fun  getNoneWatermark(
        url: String,
        outDir: File,
        onProgress: (p: Float) -> Unit,
        onVideoUrl: (url: String) -> Unit
    ) {
        val pUrl: String = if (URL(url).host.contains("iesdouyin.com")) {
            url
        } else {
            val response = Jsoup.connect(url).followRedirects(true).execute()
            response.url().toString()
        }

        val jSoup = Jsoup.connect(pUrl)
            .userAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Mobile Safari/537.36")
            .get()
//        val itemId = subScriptVal(jSoup.data(), "itemId")
        var itemId: String? = ""
        val pattern = Pattern.compile("video/(.\\d+)\\?previous")
            .matcher(pUrl)
        if (pattern.find()) {
            itemId = pattern.group(1)
        }

//        val itemId = Pattern.compile("video/(.\\d+)////").matcher(pUrl).group(1)
//        https://www.iesdouyin.com/aweme/v1/web/aweme/detail/?aweme_id=6985533128563494151

        val lUrl =
            "https://www.iesdouyin.com/aweme/v1/web/aweme/detail/?aweme_id=${itemId}"
        val okHttp1 = OkHttpClient()
        val json = JSON.parseObject(
            okHttp1.newCall(Request.Builder().url(lUrl).build()).execute().body!!.string()
        )
        Log.d("抖音链接", lUrl)
        Log.d("抖音JSON", json.toJSONString())
        val video =
            json.getJSONObject("aweme_detail").getJSONObject("video").getJSONArray("bit_rate")
                .getJSONObject(0).getJSONObject("play_addr").getJSONArray("url_list").getString(0)
        onVideoUrl(video)
        val f = File(outDir, "${itemId}.mp4")
        EggUtil.downloadFile(video, true, f, onProgress)
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