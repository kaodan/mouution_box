package com.srcbox.file.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MediaParser(private val url: String) {
    private val phoneUserAgent =
        "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Mobile Safari/537.36"
    var donTitle = ""


    fun getKuaiShouImages(): ArrayList<String> {


        val urlList = ArrayList<String>()
        val okHttp = OkHttpClient()
        val request = Request.Builder().url(url).header(
            "User-Agent",
            "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Mobile Safari/537.36"
        ).build()
        val res = okHttp.newCall(request).execute()
        val bodyStr = res.body?.string() ?: "{}"
        donTitle = Jsoup.parse(bodyStr).title()

        val jsonStr = getMiddle(bodyStr, "window.pageData=", "</script><script>")
        val images = JSON.parseObject(jsonStr).getJSONObject("video").getJSONArray("images")
        images.forEach {
            it as JSONObject
            val url = "https://tx2.a.kwimgs.com" + it.getString("path")
            urlList.add(url)
            println(url)
        }
        return urlList
    }

    fun getDouYinImages(): ArrayList<String> {
        val urlList = ArrayList<String>()
        val okHttp = OkHttpClient()
        val request = Request.Builder().url(
            "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=${
                getItemId(
                    Jsoup.connect(url).execute().url().toString()
                )
            }"
        ).build()
        val res = okHttp.newCall(request).execute()
        val jsonStr = res.body?.string() ?: "{}"
        donTitle = Jsoup.parse(jsonStr).title()

        val images = JSON.parseObject(jsonStr).getJSONArray("item_list").getJSONObject(0)
            .getJSONArray("images")
        images.forEach {
            it as JSONObject
            val url = it.getJSONArray("url_list").getString(0)
            urlList.add(url)
        }
        return urlList
    }


    fun getPpxVideo(): Array<String> {
        val ppxSource = Jsoup.connect(url).userAgent(phoneUserAgent).followRedirects(true).execute()
        val item = getPpxItemId(ppxSource.url().toString())
        val ppxVideoSourceLink =
            "https://h5.pipix.com/bds/webapi/item/detail/?item_id=$item&source=share"
        val ppxItemSourceBody = Jsoup.connect(ppxVideoSourceLink)
            .ignoreContentType(true)
            .get()

        val url = JSON.parseObject(ppxItemSourceBody.body().text()).getJSONObject("data")
            .getJSONObject("item")
            .getJSONObject("video")
            .getJSONObject("video_high").getJSONArray("url_list").getJSONObject(0)
            .getString("url")
        return arrayOf(url, "${System.currentTimeMillis()}")
    }

    fun getKgVideo(): Array<String?> {
        val kgSource = Jsoup.connect(url).userAgent(phoneUserAgent).followRedirects(true).get()
//        val kgScript = kgSource.body().select("script")[0].html()
        val kgUrlMatcher =
            Pattern.compile("playurl_video\\u0022:\\u0022(.+)\\u0022,\\u0022poi")
                .matcher(kgSource.toString())
        val url = if (kgUrlMatcher.find()) {
            kgUrlMatcher.group(1)
        } else {
            null
        }
        return arrayOf(url, kgSource.title())
    }

    fun getKgMusic(): Array<String?> {
        val kgSource = Jsoup.connect(url).userAgent(phoneUserAgent).followRedirects(true).get()
//        val kgScript = kgSource.body().select("script")[0].html()
        val kgUrlMatcher =
            Pattern.compile("playurl\\u0022:\\u0022(.+)\\u0022,\\u0022playurl_video\\u0022:\\u0022\\u0022,\\u0022poi")
                .matcher(kgSource.toString())
        val url = if (kgUrlMatcher.find()) {
            kgUrlMatcher.group(1)
        } else {
            null
        }
        return arrayOf(url, kgSource.title())
    }

    fun getAcFunCover(): MediaData {
        val acFunJ = getAcFunJson()
        return MediaData(acFunJ.getString("title"), acFunJ.getString("coverUrl"))
    }

    fun getBiliBiliCover(): MediaData {
        val biliBiliJson = getBiliBiliJson()
        var url = ""
        var title = ""
        biliBiliJson?.getJSONObject("video")?.getJSONObject("viewInfo")?.apply {
            url = getString("pic")
            title = getString("title")
        }
        return MediaData(title, url)
    }

    fun getAcFunVideo(): MediaData {
        var title = ""
        var url = ""
        getAcFunJson().apply {
//            val ksPlayJson = JSON.parseObject(getString("ksPlayJso"))
            val ksPlayJson = getJSONObject("currentVideoInfo").getJSONObject("ksPlayJson")
            url = ksPlayJson.getJSONObject("adaptationSet").getJSONArray("representation")
                .getJSONObject(0).getJSONArray("backupUrl").getString(0)
            title = getString("title")
        }
        return MediaData(title, url)
    }

    fun getBiliBiliVideo(): MediaData {
        val biliBiliJson = getBiliBiliJson()
        var url: String = ""
        var title: String = ""
        println(biliBiliJson)
        biliBiliJson?.getJSONObject("video")?.apply {

            url = getJSONArray("playUrlInfo")?.getJSONObject(0)!!.getString("url")
            title = getJSONObject("viewInfo").getString("title")
        }
        return MediaData(title, url)
    }

    fun getPpxPlVideo(): MediaData {
        val ppxCommentHttp = Jsoup.connect(url).followRedirects(true).execute()
        val ppxCommentUrl = ppxCommentHttp.url().toString()
        val cellId = getUrlParamVal(ppxCommentUrl, "cell_id")
        val ppxCommentJson = JSON.parseObject(
            Jsoup.connect("https://h5.pipix.com/bds/webapi/cell/detail/?cell_id=$cellId&cell_type=8&source=share")
                .ignoreContentType(true).get().body().html()
        )
        var url = ""
        var title = ""

        ppxCommentJson.getJSONObject("data").getJSONObject("comment").apply {
            url = getJSONObject("video").getJSONArray("url_list").getJSONObject(0).getString("url")
            title = getString("text")
        }
        return MediaData(title, url)
    }

    fun getUrlParamVal(url: String, params: String): String? {
        val pattern = Pattern.compile("$params=(\\d*?)&").matcher(url)
        return if (pattern.find()) {
            pattern.group(1)
        } else {
            ""
        }
    }

    private fun trimEmptyStr(string: String): String {
        val pattern = Pattern.compile("\\n\\r|\\s").matcher(string)
        return if (pattern.find()) {
            pattern.replaceAll("")
        } else {
            string
        }
    }

    private fun getPpxItemId(str: String): String? {
        val matcher = Pattern.compile("item/(\\d+)\\u003f").matcher(str)
        if (matcher.find()) {
            return matcher.group(1)
        }
        return null
    }

    private fun getMiddle(source: String, left: String, right: String): String? {
        val regex = "$left(\\u007b.*?\\u007d)$right"
        val scriptPattern = Pattern.compile(regex).matcher(trimEmptyStr(source))
        return if (scriptPattern.find()) {
            scriptPattern.group(1)
        } else {
            ""
        }
    }

    fun getItemId(str: String): String? {
        val matcher = Pattern.compile("video/(\\d+)/\\u003f").matcher(str)
        if (matcher.find()) {
            return matcher.group(1)
        }
        return null
    }

    private fun getAcFunJson(): JSONObject {
        val biliBiliJSoupDoc = Jsoup.connect(checkSerialNumber().toLowerCase(Locale.ROOT)).get()
        return JSON.parseObject(getScriptVar(biliBiliJSoupDoc.toString(), "window.videoInfo"))
    }

    private fun getBiliBiliJson(): JSONObject? {
        var checkSerialNumber = ""
        checkSerialNumber().apply {
            val beforeTow = substring(0, 2)
            checkSerialNumber = replace(beforeTow, beforeTow.toUpperCase(Locale.ROOT))
        }
        val biliBiliJSoupDoc =
            Jsoup.connect(checkSerialNumber).userAgent(phoneUserAgent)
                .get()
//        val scriptSource = biliBiliJSoupDoc.select("script")[7].html()
        return JSON.parseObject(
            getScriptVar(
                biliBiliJSoupDoc.toString(),
                "window.__INITIAL_STATE__"
            )
        )
    }

    private fun getScriptVar(
        sourceStr: String,
        varName: String
    ): String? {
        /*var space = ""
        if (isSpace) space = " "
        */
        val regex = "$varName=(\\u007b.*?\\u007d);"
        val scriptPattern = Pattern.compile(regex).matcher(trimEmptyStr(sourceStr))
        return if (scriptPattern.find()) {
            scriptPattern.group(1)
        } else {
            ""
        }
    }

    private fun checkSerialNumber(): String {
        val strLower = url
        if ((strLower.contains("AV") || strLower.contains("BV") || strLower.contains("bv") || strLower.contains(
                "av"
            )) && !strLower.contains("http")
        ) {
            return "https://www.bilibili.com/video/${strLower}"
        } else {
            if ((strLower.contains("ac") || strLower.contains("AC")) && !strLower.contains("http")) {
                return "https://www.acfun.cn/v/$strLower"
            }
            return strLower
        }
    }


    data class MediaData(val title: String, val url: String)
}

fun main() {
    MediaParser("https://v.kuaishou.com/aH1Zta").getKuaiShouImages()
}