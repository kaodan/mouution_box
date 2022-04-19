package com.srcbox.file.ui.util

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.task.DownloadTask
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import com.srcbox.file.R
import com.srcbox.file.adapter.MusicDownloadAdapter
import com.srcbox.file.data.MusicData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.music_download_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.SocketTimeoutException

class MusicDownloadActivity : AppCompatActivity() {
    companion object {
        var source = "netease"
        lateinit var asLoad: LoadingPopupView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_download_activity)
        Aria.download(this).register()
        GlobUtil.changeTitle(this)
        EggUtil.loadIcon(this, AppSetting.colorStress, music_search, title_bar.rightView)
        asLoad = XPopup.Builder(this).asLoading()

        music_search.textSize = EggUtil.dp2px(this, 8f).toFloat()
        title_bar.rightView.setOnClickListener {
            XPopup.Builder(this).atView(it)
                .asAttachList(arrayOf("网易云", "酷狗", "虾米"), null) { _: Int, s: String ->
                    when (s) {
                        "网易云" -> {
                            source = "netease"
                        }

                        "酷狗" -> {
                            source = "kugou"
                        }

                        "虾米" -> {
                            source = "xiami"
                        }
                    }
                }.show()
        }

        searchMusic("薛之谦", source) {
            println("搜索到的数据是：${it.size}")
            music_download_list.layoutManager = LinearLayoutManager(this@MusicDownloadActivity)
            music_download_list.adapter =
                MusicDownloadAdapter(this@MusicDownloadActivity, it)
        }

        music_search.setOnClickListener {
            if (key_word.text.isNotEmpty()) {
                searchMusic(key_word.text.toString(), source) {
//                    key_word.setText("")
                    EggUtil.hideKeyboard(this)
                    if (it.size == 0) {
                        EggUtil.toast("没有相关歌曲")
                        return@searchMusic
                    }
                    music_download_list.layoutManager =
                        LinearLayoutManager(this@MusicDownloadActivity)
                    music_download_list.adapter =
                        MusicDownloadAdapter(this@MusicDownloadActivity, it)
                }
            } else {
                EggUtil.toast("请不要为空")
            }
        }
    }

    @Download.onTaskComplete
    fun taskComplete(task: DownloadTask) {
        asLoad.dismiss()
        println("已完成")
        MusicDownloadAdapter.downDir.let {
            if (it != null) {
                EggUtil.toast(it.path)
            } else {
                EggUtil.toast("失败")
            }
        }
    }

    private fun searchMusic(
        keyWord: String,
        source: String = "netease",
        onSuccess: (musicDataArr: ArrayList<MusicData>) -> Unit
    ) {
        asLoad.show()
        val link =
            "http://www.gequdaquan.net/gqss/api.php"
        val okHttpClient = OkHttpClient()
        val body = FormBody.Builder()
            .add("types", "search")
            .add("count", "20")
            .add("source", source)
            .add("pages", "1")
            .add("name", keyWord)
            .build()
        GlobalScope.launch(Dispatchers.Main) {
            val musicArrayList = ArrayList<MusicData>()
            withContext(Dispatchers.IO) {
                try {
                    val request = Request.Builder().url(link).post(body).build()
                    val response = okHttpClient.newCall(request).execute()
                    if (response.code == 200) {
                        response.body?.let { itj ->
                            val musics = JSON.parseArray(itj.string())
                            musics.forEach { it ->
                                val jso = it as JSONObject
                                musicArrayList.add(
                                    MusicData(
                                        jso.getString("id"),
                                        jso.getString("name"),
                                        jso.getString("pic_id"),
                                        jso.getString("url_id"),
                                        jso.getString("lyric_id"),
                                        jso.getString("source"),
                                        jso.getJSONArray("artist").toArray().toList()
                                    )
                                )
                            }
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    EggUtil.toast("加载失败")
                }
            }
            asLoad.dismiss()
            onSuccess(musicArrayList)
        }
    }
}
