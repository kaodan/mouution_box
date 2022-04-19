package com.srcbox.file.adapter

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.arialyy.aria.core.Aria
import com.bumptech.glide.Glide
import com.srcbox.file.R
import com.srcbox.file.application.EggApplication
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.MusicData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.util.MusicDownloadActivity
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.music_download_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.SocketTimeoutException

class MusicDownloadAdapter(val context: Context, private val musicDataArray: ArrayList<MusicData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.music_download_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return musicDataArray.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            EggUtil.loadIcon(
                context,
                AppSetting.colorStress,
                holder.itemView.music_download_icon,
                holder.itemView.music_play_icon
            )
            holder.bind(position)
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val musicData = musicDataArray[position]
            GlobalScope.launch(Dispatchers.Main) {
                val playUrl = getMusicData("url", musicData.downId)
                val lyric = getMusicData("lyric", musicData.lyricId)
                val pic = getMusicData("pic", musicData.pictureId)
                if (playUrl.isEmpty() || lyric.isEmpty() || pic.isEmpty()) {
                    return@launch
                }
                itemView.music_name.text = musicData.musicName
                var artist = ""
                musicData.artist.forEach {
                    artist += "$it "
                }
                itemView.music_artist_text.text = artist
                Glide.with(EggApplication.context).load(pic)
                    .placeholder(R.mipmap.placeholder)
                    .error(R.mipmap.music).into(itemView.music_picture)
                itemView.music_play_icon.setOnClickListener {
                    val mIntent = Intent(Intent.ACTION_VIEW)
                    mIntent.setDataAndType(
                        Uri.parse(playUrl),
                        "audio"
                    )
                    try {
                        (context as Activity).startActivity(mIntent)
                    } catch (e: ActivityNotFoundException) {
                        EggUtil.toast("对不起，您的手机无法预览此音频")
                    }
                }

                itemView.music_download_icon.setOnClickListener {
                    downDir =
                        File(
                            AppStorageData.getFileOutFile(),
                            "音乐下载/${musicData.musicName}"
                        )
//                    EggUtil.toast("音乐和歌词存储在：$file")
                    MusicDownloadActivity.asLoad.show()
                    Aria.download(context).load(playUrl)
                        .setFilePath(File(downDir?.path, "${musicData.musicName}.mp3").path)
                        .ignoreFilePathOccupy().create()
                    EggIO.writeFile(File(downDir, "歌词.txt"), lyric)
                }
            }
        }


        private suspend fun getMusicData(type: String, id: String): String {
            return withContext(Dispatchers.IO) {
                var result = ""
                try {
                    val client = OkHttpClient()
                    val body = FormBody.Builder()
                        .add("types", type)
                        .add("id", id)

                    if (MusicDownloadActivity.source != "netease") {
                        body.add("source", MusicDownloadActivity.source)
                    }

                    val request =
                        Request.Builder().url("http://www.gequdaquan.net/gqss/api.php")
                            .post(body.build())
                            .build()
                    val response = client.newCall(request).execute()
                    result = when (type) {
                        "lyric" -> {
                            JSON.parseObject(response.body!!.string())
                                .getString("lyric")
                        }
                        else -> {
                            JSON.parseObject(response.body!!.string())
                                .getString("url")
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    //超时
                } catch (e: Exception) {
                }
                result
            }
        }
    }

    companion object {
        const val TYPE_URL = "url"
        const val TYPE_ICON = "pic"
        const val TYPE_LYRIC = "lyric"
        var downDir: File? = null
    }
}

/*  val downLink =
              async {  }
          val pictureUrl =
              async { getMusicData("pic", musicData.pictureId) }
          val lyricUrl =
              async {  }*/
/*println(
    """
    下载链接:${downLink.await()}
    图片链接:${pictureUrl.await()}
    歌词:${lyricUrl.await()}
""".trimIndent()
)*/