package com.srcbox.file.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.XPopupCallback
import com.srcbox.file.R
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.PlayVideoActivity
import com.srcbox.file.ui.popup.*
import com.srcbox.file.ui.searchimginfo.SearchImgInfoActivity
import com.srcbox.file.ui.util.*
import com.srcbox.file.ui.util.cartoonFormPicture.CartoonFormPictureActivity
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.home_listv_item.view.*
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.config.AbstractFileFilter
import me.rosuh.filepicker.config.FilePickerManager
import org.json.JSONObject

class MainHomeFunAdapter(val context: Context, private val funJSONObject: JSONObject) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.home_listv_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return funJSONObject.length()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val funTitleName = funJSONObject.names()!![position].toString()
            val funImgU = funJSONObject[funTitleName].toString()

            val title = funTitleName.split("$")[0]
            val isTui = funTitleName.split("$")[1]
            if (isTui != "true") {
                itemView.fun_tui_img.visibility = View.GONE
            }
            itemView.fun_name.text = title
            itemView.fun_img.text = funImgU
            EggUtil.loadIcon(context, AppSetting.colorStress, itemView.fun_img)

            itemView.setOnClickListener {
                when (title) {
                    "壁纸获取" -> {
                        context.startActivity(Intent(context, WallPaper::class.java))
                    }
                    "文章生成" -> {
                        context.startActivity(Intent(context, ShitSmooth::class.java))
                    }

                    "好好说话" -> {
                        context.startActivity(Intent(context, CanSpeak::class.java))
                    }

                    "Bv转Av" -> {
                        XPopup.Builder(context).asCustom(Bv2AvPopup(context)).show()
                    }

                    "音乐下载" -> {
                        context.startActivity(Intent(context, MusicDownloadActivity::class.java))
                    }

                    "图片压缩" -> {
                        XPopup.Builder(context).asCustom(CompressImagePopup(context)).show()
                    }

                    "抖音解析" -> {
                        XPopup.Builder(context).asCustom(ParseDouYinPopup(context)).show()
                    }

                    "快手解析" -> {
                        XPopup.Builder(context).asCustom(ParseKuaiShouPopup(context)).show()
                    }

                    "抖音音乐" -> {
                        XPopup.Builder(context).asCustom(ParseDouYinMusicPopup(context)).show()
                    }

                    "以图搜番" -> {
                        (context as Activity).startActivity(
                            Intent(
                                context,
                                CartoonFormPictureActivity::class.java
                            )
                        )
//                        XPopup.Builder(context).asCustom(PlayWebVideoPopup(context)).show()
                    }

                    "K歌视频" -> {
                        showMediaParserPopup(MediaParserPopup.Type.KG_VIDEO)
                    }

                    "K歌音乐" -> {
                        showMediaParserPopup(MediaParserPopup.Type.KG_MUSIC)
                    }

                    "皮虾视频" -> {
                        showMediaParserPopup(MediaParserPopup.Type.PPX_VIDEO)
                    }

                    "皮虾评论" -> {
                        showMediaParserPopup(MediaParserPopup.Type.PPX_COMMENT)
                    }

                    "A站封面" -> {
                        showMediaParserPopup(MediaParserPopup.Type.A_COVER)
                    }

                    "B站封面" -> {
                        showMediaParserPopup(MediaParserPopup.Type.B_COVER)
                    }

                    "A站视频" -> {
                        showMediaParserPopup(MediaParserPopup.Type.A_VIDEO)
                    }

                    "B站视频" -> {
                        showMediaParserPopup(MediaParserPopup.Type.B_VIDEO)
                    }

                    "抖音图集" -> {
                        showMediaParserPopup(MediaParserPopup.Type.DOUYIN_IMAGES)

                    }

                    "快手图集" -> {
                        showMediaParserPopup(MediaParserPopup.Type.KUAISHOU_IMAGES)

                    }

                    "锁屏提取" -> {
                        context.startActivity(Intent(context, LockScreenActivity::class.java))
                    }

                    "查图信息" -> {
                        context.startActivity(Intent(context, SearchImgInfoActivity::class.java))
                    }

                    "进制转换器" -> {
                        context.startActivity(Intent(context, HexToBinaryActivity::class.java))
                    }



                    "汉语字典" -> {
                        XPopup.Builder(itemView.context).asInputConfirm("输入字词", "") {
                            val intent = Intent(context, FindWordActivity::class.java)
                            intent.putExtra("word", it)
                            context.startActivity(intent)
                        }.show()
                    }


                    "视频消音"->{
                        FilePickerManager.from(itemView.context as Activity)
                            .maxSelectable(1)
                            .filter(object : AbstractFileFilter() {
                                override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
                                    return ArrayList(listData.filter { item ->
                                        (item.isDir) || item.fileName.endsWith("mp4") || item.fileName.endsWith(
                                            "flv"
                                        ) || item.fileName.endsWith(
                                            "avi"
                                        ) || item.fileName.endsWith("mpeg")
                                    })
                                }

                            })
                            .forResult(6)
                    }

                    "视频转音频" -> {
                        FilePickerManager.from(itemView.context as Activity)
                            .maxSelectable(1)
                            .filter(object : AbstractFileFilter() {
                                override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
                                    return ArrayList(listData.filter { item ->
                                        (item.isDir) || item.fileName.endsWith("mp4") || item.fileName.endsWith(
                                            "flv"
                                        ) || item.fileName.endsWith(
                                            "avi"
                                        ) || item.fileName.endsWith("mpeg")
                                    })
                                }

                            })
                            .forResult(5)
                    }
                }
            }
        }

        private fun showMediaParserPopup(type: MediaParserPopup.Type) {
            val mediaParserPopup = MediaParserPopup(context)
            mediaParserPopup.setType(type)
            XPopup.Builder(context).asCustom(mediaParserPopup).show()
        }
    }
}