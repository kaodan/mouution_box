package com.srcbox.file.ui.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.srcbox.file.R
import com.srcbox.file.application.EggApplication
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.util.MediaParser
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.popup_media_parser.view.*
import kotlinx.android.synthetic.main.popup_media_parser.view.pasteText
import net.m3u8.download.M3u8DownloadFactory
import net.m3u8.listener.DownloadListener
import net.m3u8.utils.Constant
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread


class MediaParserPopup(context: Context) : CenterPopupView(context) {
    private var type: Type? = null
    override fun onCreate() {
        super.onCreate()
        edit_container.radius = QMUIDisplayHelper.dp2px(context, 8)
//        val mediaDownload = media_download.text.toString()

        pasteText.setOnClickListener {
            try {
                edit.setText(EggUtil.getClipVal(context))
            } catch (e: Exception) {
                EggUtil.toast("粘贴失败，请重试")
            }
        }

        when (type) {


            Type.DOUYIN_IMAGES -> {
                title.text = "抖音图集"
                media_download.setOnClickListener {

                    if (edit.text.isEmpty()) {
                        EggUtil.toast("不能为空")
                        return@setOnClickListener
                    }
                    media_download.text = "准备中..."

                    val mediaParser = MediaParser(EggUtil.filterSpecialStr(edit.text.toString()))
                    thread {
                        try {
                            val strArr = mediaParser.getDouYinImages()
                            changeProgress(
                                strArr,
                                File(
                                    AppStorageData.getFileOutFile(),
                                    "网络解析/抖音图集/${
                                        mediaParser.donTitle ?: (333333..9999999).random()
                                            .toString()
                                    }"
                                )
                            )
                        } catch (e: Exception) {
                            toastErrorInfo(e)
                        }

                    }
                }
            }

            Type.KUAISHOU_IMAGES -> {
                title.text = "快手图集"
                media_download.setOnClickListener {

                    if (edit.text.isEmpty()) {
                        EggUtil.toast("不能为空")
                        return@setOnClickListener
                    }
                    media_download.text = "准备中..."

                    val mediaParser = MediaParser(EggUtil.filterSpecialStr(edit.text.toString()))
                    thread {
                        try {
                            val strArr = mediaParser.getKuaiShouImages()
                            changeProgress(
                                strArr,
                                File(
                                    AppStorageData.getFileOutFile(),
                                    "网络解析/快手图集/${
                                        mediaParser.donTitle ?: (333333..9999999).random()
                                            .toString()
                                    }"
                                )
                            )
                        } catch (e: Exception) {
                            toastErrorInfo(e)
                        }

                    }
                }
            }

            //皮皮虾视频
            Type.PPX_VIDEO -> {
                title.text = "皮皮虾无水印视频解析"
                media_download.setOnClickListener {
                    if (edit.text.isEmpty()) {
                        EggUtil.toast("不能为空")
                        return@setOnClickListener
                    }
                    val mediaParser = MediaParser(edit.text.toString())
                    media_download.text = "解析中"
                    thread {
                        try {
                            val strArr = mediaParser.getPpxVideo()
                            changeProgress(
                                strArr[0],
                                File(AppStorageData.getFileOutFile(), "网络解析/皮皮虾视频/${strArr[1]}.mp4")
                            )
                        } catch (e: Exception) {
                            toastErrorInfo(e)
                        }

                    }
                }
            }

            //K歌视频
            Type.KG_VIDEO -> {
                title.text = "全民K歌无水印视频解析"
                media_download.setOnClickListener {
                    if (edit.text.isEmpty()) {
                        EggUtil.toast("不能为空")
                        return@setOnClickListener
                    }
                    val mediaParser = MediaParser(edit.text.toString())
                    media_download.text = "解析中"

                    thread {
                        try {
                            val strArr = mediaParser.getKgVideo()
                            strArr[0].let { it1 ->
                                if (it1 == null) {
                                    (context as Activity).runOnUiThread {
                                        media_download.text = "下载"
                                        EggUtil.toast("失败，请重试")
                                    }
                                    return@thread
                                }
                                changeProgress(
                                    it1,
                                    File(
                                        AppStorageData.getFileOutFile(),
                                        "网络解析/K歌视频/${strArr[1]}.mp4"
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            toastErrorInfo(e)
                        }
                    }
                }
            }

            //K歌音乐
            Type.KG_MUSIC -> {
                title.text = "全民K歌无损音乐解析"
                media_download.setOnClickListener {
                    if (edit.text.isEmpty()) {
                        EggUtil.toast("不能为空")
                        return@setOnClickListener
                    }
                    val mediaParser = MediaParser(edit.text.toString())
                    media_download.text = "解析中"

                    thread {
                        try {
                            val strArr = mediaParser.getKgMusic()
                            strArr[0].let { it1 ->
                                if (it1 == null) {
                                    (context as Activity).runOnUiThread {
                                        media_download.text = "下载"
                                        EggUtil.toast("失败，请重试")
                                    }
                                    return@thread
                                }
                                changeProgress(
                                    it1,
                                    File(
                                        AppStorageData.getFileOutFile(),
                                        "网络解析/K歌音乐/${strArr[1]}.m4a"
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            toastErrorInfo(e)
                        }

                    }
                }
            }

            //A站封面
            Type.A_COVER -> {
                title.text = "A站封面"
                edit.hint = "AC号或者链接"
                media_download.setOnClickListener {
                    val mediaParser = MediaParser(edit.text.toString())
                    media_download.text = "解析中"
                    thread {
                        try {
                            val acFunCoverMediaData = mediaParser.getAcFunCover()
                            changeProgress(
                                acFunCoverMediaData.url, File(
                                    AppStorageData.getFileOutFile(),
                                    "网络解析/A站封面/${acFunCoverMediaData.title}.jpg"
                                )
                            )
                        } catch (e: Exception) {
                            toastErrorInfo(e)
                        }
                    }
                }
            }

            //B站封面
            Type.B_COVER -> {
                title.text = "B站封面"
                edit.hint = "BV/AV号或者链接"
                media_download.setOnClickListener {
                    val mediaParser = MediaParser(edit.text.toString())
                    media_download.text = "解析中"
                    thread {
                        try {
                            val biliBiliCoverMediaData = mediaParser.getBiliBiliCover()
                            changeProgress(
                                biliBiliCoverMediaData.url, File(
                                    AppStorageData.getFileOutFile(),
                                    "网络解析/B站封面/${biliBiliCoverMediaData.title}.jpg"
                                )
                            )
                        } catch (e: Exception) {
                            toastErrorInfo(e)
                        }
                    }
                }
            }

            //A站视频
            Type.A_VIDEO -> {
                title.text = "A站无水印视频"
                edit.hint = "AC号或者链接"
                media_download.setOnClickListener {
                    val mediaParser = MediaParser(edit.text.toString())
                    media_download.text = "解析中"
                    thread {
                        try {
                            val acFunCoverMediaData = mediaParser.getAcFunVideo()
                            /*val outFile = File(
                                AppStorageData.getFileOutFile(),
                                "网络解析/B站视频/${acFunCoverMediaData.title}.mp4"
                            )*/
                            println(acFunCoverMediaData.url)
                            val m3u8Download =
                                M3u8DownloadFactory.getInstance(acFunCoverMediaData.url)
                            m3u8Download.dir =
                                "${AppStorageData.getFileOutFile().absolutePath}/网络解析/A站视频"
                            m3u8Download.fileName = acFunCoverMediaData.title
                            m3u8Download.threadCount = 10
                            m3u8Download.retryCount = 100
                            m3u8Download.timeoutMillisecond = 10000L

                            m3u8Download.setLogLevel(Constant.DEBUG)
                            m3u8Download.setInterval(500L)
                            m3u8Download.addListener(object : DownloadListener {
                                override fun start() {
                                    (context as Activity).runOnUiThread {
                                        media_download.text = "开始下载"
                                    }
                                }

                                @SuppressLint("SetTextI18n")
                                override fun process(
                                    downloadUrl: String,
                                    finished: Int,
                                    sum: Int,
                                    percent: Float
                                ) {
                                    println("下载网址：" + downloadUrl + "\t已下载" + finished + "个\t一共" + sum + "个\t已完成" + percent + "%")
                                    (context as Activity).runOnUiThread {
                                        media_download.text = "$percent%"
                                    }
                                }

                                override fun speed(speedPerSecond: String) {
                                    println("下载速度：$speedPerSecond")
                                }

                                override fun end() {
                                    (context as Activity).runOnUiThread {
                                        media_download.text = "解析"
                                        XPopup.Builder(context).asConfirm(
                                            "提示",
                                            "保存路径：${m3u8Download.dir}/${m3u8Download.fileName}",
                                            null
                                        ).show()
                                    }
                                }
                            })
                            m3u8Download.start()
                        } catch (e: Exception) {
                            toastErrorInfo(e)
                        }

                    }
                }

            }

            //B站视频
            Type.B_VIDEO -> {
                title.text = "B站无水印视频"
                edit.hint = "BV/AV号或者链接"
                media_download.setOnClickListener {
                    val mediaParser = MediaParser(edit.text.toString())
                    media_download.text = "解析中"
                    thread {
                        try {
                            val biliBiliCoverMediaData = mediaParser.getBiliBiliVideo()
                            changeProgress(
                                biliBiliCoverMediaData.url, File(
                                    AppStorageData.getFileOutFile(),
                                    "网络解析/B站视频/${biliBiliCoverMediaData.title}.mp4"
                                )
                            )
                        } catch (e: Exception) {
                            toastErrorInfo(e)
                        }

                    }
                }
            }

            Type.PPX_COMMENT -> {
                title.text = "皮皮虾评论视频"
                media_download.setOnClickListener {
                    val mediaParser = MediaParser(edit.text.toString())
                    media_download.text = "解析中"
                    thread {
                        try {
                            val ppxPlMediaData = mediaParser.getPpxPlVideo()
                            changeProgress(
                                ppxPlMediaData.url, File(
                                    AppStorageData.getFileOutFile(),
                                    "网络解析/皮皮虾评论/${ppxPlMediaData.title}.mp4"
                                )
                            )
                        } catch (e: Exception) {
                            toastErrorInfo(e)
                        }

                    }
                }
            }
            else -> {
            }
        }
    }


    private fun toastErrorInfo(e: Exception) {
        (context as Activity).runOnUiThread {
            EggUtil.toast("失败，请重试${e.message}")
            media_download.text = "下载"
        }
    }

    @SuppressLint("SetTextI18n")
    fun changeProgress(url: String, file: File) {
        var isT = false
        EggUtil.downloadFile(url, true, file) {
            println(it)
            (context as Activity).runOnUiThread {
                media_download.text = "已下载$it"
                if (it == 100f) {
                    if (!isT) {
                        isT = true
                        media_download.text = "下载"
                        XPopup.Builder(context).asConfirm("提示", "保存路径：$file", null).show()
                    }
                }
            }
        }
    }

    private fun changeProgress(urlList: ArrayList<String>, file: File) {
//        var isT = false
        var size = 0;
        urlList.forEach {
            EggUtil.downloadFile(
                it,
                true,
                File(file, (5555..99999).random().toString() + ".png")
            ) { pro ->
                println(pro)
                (context as Activity).runOnUiThread {
                    media_download.text = "正在下载$size $pro"
                }
            }

            size++
        }

        media_download.text = "已完成"
        XPopup.Builder(context).asConfirm("提示", "保存路径：$file", null).show()

    }

    override fun getImplLayoutId(): Int {
        return R.layout.popup_media_parser
    }

    fun setType(type: Type) {
        this.type = type
    }

    fun download(url: String, file: File) {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = okHttpClient.newCall(request).execute()
        val inputStream = response.body?.byteStream()
        val outputStream = FileOutputStream(file)
        val byteBuff = ByteArray(524)
        var len: Int = 0

        while ((inputStream!!.read(byteBuff, 0, byteBuff.size)).apply { len = this } != -1) {
            outputStream.write(byteBuff, 0, len)
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
        EggUtil.notifyDCIM(EggApplication.context, file)
    }

    enum class Type {
        PPX_VIDEO,
        PPX_COMMENT,
        KG_VIDEO,
        KG_MUSIC,
        A_COVER,
        B_COVER,
        A_VIDEO,
        B_VIDEO,
        DOUYIN_IMAGES,
        KUAISHOU_IMAGES,
    }
}