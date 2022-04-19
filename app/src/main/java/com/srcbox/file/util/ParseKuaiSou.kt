package com.srcbox.file.util

import com.alibaba.fastjson.JSON
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern


class ParseKuaiSou {
    fun getVideo(
        webUrl: String,
        file: File,
        onProgress: (Float) -> Unit,
        onVideoUrl: (String) -> Unit
    ) {
        val jSoup = Jsoup.connect(webUrl).get()
        val lUrl = jSoup.location()
//        photoId
        var photoId: String? = ""
        val pattern = Pattern.compile("photoId=(.*?)&")
            .matcher(lUrl)
        if (pattern.find()) {
            photoId = pattern.group(1)
        }
        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType = "application/json".toMediaTypeOrNull()
        val body: RequestBody = RequestBody.create(
            mediaType,
            "{\"operationName\":\"visionVideoDetail\",\"variables\":{\"photoId\":\"${photoId}\",\"page\":\"detail\"},\"query\":\"query visionVideoDetail(\$photoId: String, \$type: String, \$page: String, \$webPageArea: String) {\\n  visionVideoDetail(photoId: \$photoId, type: \$type, page: \$page, webPageArea: \$webPageArea) {\\n    status\\n    type\\n    author {\\n      id\\n      name\\n      following\\n      headerUrl\\n      __typename\\n    }\\n    photo {\\n      id\\n      duration\\n      caption\\n      likeCount\\n      realLikeCount\\n      coverUrl\\n      photoUrl\\n      liked\\n      timestamp\\n      expTag\\n      llsid\\n      viewCount\\n      videoRatio\\n      stereoType\\n      croppedPhotoUrl\\n      manifest {\\n        mediaType\\n        businessType\\n        version\\n        adaptationSet {\\n          id\\n          duration\\n          representation {\\n            id\\n            defaultSelect\\n            backupUrl\\n            codecs\\n            url\\n            height\\n            width\\n            avgBitrate\\n            maxBitrate\\n            m3u8Slice\\n            qualityType\\n            qualityLabel\\n            frameRate\\n            featureP2sp\\n            hidden\\n            disableAdaptive\\n            __typename\\n          }\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    tags {\\n      type\\n      name\\n      __typename\\n    }\\n    commentLimit {\\n      canAddComment\\n      __typename\\n    }\\n    llsid\\n    danmakuSwitch\\n    __typename\\n  }\\n}\\n\"}"
        )
        val request: Request = Request.Builder()
            .url("https://www.kuaishou.com/graphql")
            .method("POST", body)
            .addHeader(
                "sec-ch-ua",
                "\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"91\", \"Chromium\";v=\"91\""
            )
            .addHeader("accept", "*/*")
            .addHeader("sec-ch-ua-mobile", "?0")
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
            )
            .addHeader("content-type", "application/json")
            .addHeader("Sec-Fetch-Site", "same-origin")
            .addHeader("Sec-Fetch-Mode", "cors")
            .addHeader("Sec-Fetch-Dest", "empty")
            .addHeader(
                "Cookie",
                "clientid=3; did=web_4674133be9ca450806f651e1a00b5bb9; didv=1626618986000; kpf=PC_WEB; kpn=KUAISHOU_VISION"
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSON.parseObject(response.body?.string() ?: "")
                println(json)
                val video = json.getJSONObject("data").getJSONObject("visionVideoDetail")
                    .getJSONObject("photo").getString("photoUrl")
                onVideoUrl(video)

                EggUtil.downloadFile(
                    video,
                    false,
                    File(file, "${System.currentTimeMillis()}.mp4"),
                    onProgress
                )
            }
        })

        /*println(u)
        println(pId)
        val jSoupHtml = jSoup.html()
//        println(jSoupHtml)
        val playI = jSoupHtml.indexOf("\"playUrl\"")
        val endPlayI = jSoupHtml.indexOf(".mp4", playI + 1)
        val video = "${'$}}{
            jSoupHtml.substring(playI, endPlayI)
                .replace("\"playUrl\":\"", "")
        }.mp4".replace("u002F", "")
            .replace("jsmov2.a.yximgs.com", "txmov2.a.yximgs.com")
        EggUtil.downloadFile(
            video,
            false,
            File(file, "${System.currentTimeMillis()}.mp4"),
            onProgress
        )
        onVideoUrl(video)*/
    }

}

fun main() {

}




/*    val h =
        HttpCookie.parse("[clientid=3; expires=Thu, 16 Jun 2022 16:04:35 GMT; domain=kuaishou.com; path=/; httponly, did=web_54e8bf2e4896023576c886732d8b4df1; expires=Thu, 16 Jun 2022 16:04:35 GMT; domain=kuaishou.com; path=/; httponly]")
    println(h)*/
