package com.srcbox.file.util

import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.regex.Pattern

class ParseUrlUtil(val url: String) {
    fun isBiliBili() = url.contains("bilibili.com")
    fun isAcFun() = url.contains("acfun.cn")
    fun isBiliBiliVideo() = url.contains("80000000") || url.contains("40000000")
    fun getBiliBiliVideo(): String? {
        val pattern = Pattern.compile("(http(.*)){2}").matcher(url)
        return if (pattern.find()) {
            pattern.group(1)
        } else {
            url
        }
    }

    fun requestHeader(): okhttp3.MediaType? {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()
        return okHttpClient.newCall(request).execute().body!!.contentType()
    }

    val contentTypeTable = mapOf<String, String>(
        "jpg" to "application/x-jpg",
        "jpeg" to "image/jpeg",
        "avi" to "video/avi",
        "jpg" to "image/jpeg",
        "m3u" to "audio/mpegurl",
        "mp3" to "audio/mp3",
        "png" to "image/png",
        "mp4" to "video/mp4"
    )
}