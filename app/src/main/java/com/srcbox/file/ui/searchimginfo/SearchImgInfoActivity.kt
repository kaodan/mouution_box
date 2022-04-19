package com.srcbox.file.ui.searchimginfo

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.donkingliang.imageselector.utils.ImageSelector
import com.lxj.xpopup.XPopup
import com.qmuiteam.qmui.layout.QMUILayoutHelper
import com.qmuiteam.qmui.util.QMUIDeviceHelper
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.srcbox.file.R
import com.srcbox.file.ui.searchimginfo.data.ContentColumnData
import com.srcbox.file.ui.searchimginfo.data.ImgInfoData
import com.srcbox.file.util.GlobUtil
import com.srcbox.file.util.ToastUtil
import kotlinx.android.synthetic.main.activity_search_img_info.*
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.lang.Exception


class SearchImgInfoActivity : AppCompatActivity() {
    private val REQUEST_IMAGE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_img_info)
        GlobUtil.changeTitle(this)
        searchAdd.setOnClickListener {
            ImageSelector.builder()
                .useCamera(true) // 设置是否使用拍照
                .setSingle(true)  //设置是否单选
                .canPreview(false) //是否可以预览图片，默认为true
                .start(this, REQUEST_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val asLoading = XPopup.Builder(this).asLoading()
        if (requestCode == REQUEST_IMAGE) {
            asLoading.show()
            println("已选择")
            val images = data?.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
            val path = images?.get(0)
            path?.let {
                val okHttpClient = OkHttpClient()
                val file = File(it)
                val multipartBody =
                    MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.name, file.asRequestBody())
                        .build()
                val request =
                    Request.Builder().url("https://saucenao.com/search.php").post(multipartBody)
                        .build()
                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            ToastUtil(this@SearchImgInfoActivity).longShow("失败${e.message}")
                            asLoading.dismiss()
                            println(e.message)
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        try {
                            val html = response.body?.string()
                            html?.let { str ->
                                println(str)
                                val imgInfo = ArrayList<ImgInfoData>()
                                var imgLink = ""
                                val jSoupResult =
                                    Jsoup.parse(str).getElementsByClass("resulttable")
                                        .forEach { t ->

                                            val linkify =
                                                t.getElementsByClass("resultimage")[0]?.getElementsByClass(
                                                    "linkify"
                                                )
                                            val linkOnHref =
                                                linkify?.select("a")?.attr("href") ?: ""

                                            linkify
                                                ?.select("img")?.let { img ->
                                                    val dataSrc: String
                                                    val src: String
                                                    if (img.attr("data-src")
                                                            .apply { dataSrc = this }
                                                            .isNotEmpty()
                                                    ) {
                                                        imgLink = dataSrc
                                                    } else {
                                                        src = img.attr("src")
                                                        imgLink = src
                                                    }
                                                }
                                            println("图片链接是$imgLink")
                                            val similarityInfo =
                                                t.getElementsByClass("resultsimilarityinfo")[0].text()
                                            val miscInfo =
                                                t.getElementsByClass("resultmiscinfo")[0].text()
                                            val title =
                                                t.getElementsByClass("resulttitle")[0].text()
                                            println(title)
                                            println(miscInfo)
                                            println(similarityInfo)
                                            val contentColumns = ArrayList<ContentColumnData>()
                                            t.getElementsByClass("resultcontentcolumn")[0].getElementsByTag(
                                                "strong"
                                            ).forEach { ele ->
                                                try {
                                                    val name = ele.text()
                                                    ele.nextElementSibling().apply {
                                                        val number = text()
                                                        val link = attr("href")
                                                        contentColumns.add(
                                                            ContentColumnData(
                                                                name,
                                                                number,
                                                                link
                                                            )
                                                        )
                                                    }
                                                } catch (e: Exception) {
                                                    /*runOnUiThread {
                                                        ToastUtil(this@SearchImgInfoActivity).longShow(
                                                            "发生错误，请重试"
                                                        )
                                                    }*/
                                                }

                                            }

                                            imgInfo.add(
                                                ImgInfoData(
                                                    title,
                                                    imgLink,
                                                    miscInfo,
                                                    similarityInfo,
                                                    contentColumns, linkOnHref
                                                )
                                            )
                                        }
                                runOnUiThread {
                                    asLoading.dismiss()
                                    imgInfoList.layoutManager =
                                        LinearLayoutManager(this@SearchImgInfoActivity)
                                    imgInfoList.adapter =
                                        SearchImgInfoListAdapter(
                                            this@SearchImgInfoActivity,
                                            imgInfo
                                        )
                                }
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                ToastUtil(this@SearchImgInfoActivity).longShow("发生错误，请重试")
                            }
                        }
                    }
                })
            }
        }
    }
}

fun main() {
    println("请求中")
    val okHttpClient = OkHttpClient()
    val file = File("H:\\image\\qq.jpg")
    val multipartBody =
        MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody())
            .build()
    val request =
        Request.Builder().url("https://saucenao.com/search.php").post(multipartBody)
            .build()
    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("失败")
            println(e.message)
        }

        override fun onResponse(call: Call, response: Response) {
            val html = response.body?.string()
            html?.let { str ->
                val jSoupResult = Jsoup.parse(str).getElementsByClass("result")[0]
                val p = jSoupResult.select("tr")
            }
        }
    })
}