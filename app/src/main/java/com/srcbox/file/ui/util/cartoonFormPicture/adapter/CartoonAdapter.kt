package com.srcbox.file.ui.util.cartoonFormPicture.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.XPopupCallback
import com.srcbox.file.R
import com.srcbox.file.ui.PlayVideoActivity
import com.srcbox.file.ui.popup.SelectAllOrPartPopup
import com.srcbox.file.ui.util.cartoonFormPicture.data.DocData
import kotlinx.android.synthetic.main.cartoon_doc.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CartoonAdapter(val context: Context, val list: ArrayList<DocData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cartoon_doc, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MyViewHolder
        val doc = list[position]
        holder.itemView.doc_title.text = "片名:${doc.titleNative}"
        holder.itemView.doc_title_chinese.text = "中文片名:${doc.titleChinese}"
        holder.itemView.doc_season.text = doc.season
        holder.itemView.doc_similarity.text = "匹配度：${String.format("%.2f", doc.similarity * 100)}%"
        if (doc.episode.isEmpty()) {
            holder.itemView.doc_episode.text = "未知集"
        } else {
            holder.itemView.doc_episode.text = "第${doc.episode}集"
        }
        holder.itemView.doc_atTime.text = "第${(doc.at / 60).toInt()}分钟"
        Glide.with(context)
            .load("https://trace.moe/thumbnail.php?anilist_id=${doc.anilistId}&file=${doc.fileName}&t=${doc.at}&token=${doc.tokenThumb}")
            .placeholder(R.mipmap.placeholder).error(R.mipmap.placeholder)
            .into(holder.itemView.doc_image)
        holder.itemView.doc_play_video.setOnClickListener {
            /*val selectAllOrPartPopup = SelectAllOrPartPopup(context)
            val partU = "https://trace.moe/preview.php?anilist_id=${doc.anilistId}&file=${doc.fileName}&t=${doc.at}&token=${doc.tokenThumb}"
            XPopup.Builder(context).setPopupCallback(object :XPopupCallback{
                override fun onBackPressed(): Boolean {
                    return false
                }

                override fun onDismiss() {}

                override fun beforeShow() {}

                override fun onCreated() {
                    selectAllOrPartPopup.setContent(partU)
                }

                override fun onShow() {}

            }).asCustom(selectAllOrPartPopup)
*/
            val intent = Intent(context, PlayVideoActivity::class.java)
            intent.putExtra(
                "url",
                "https://media.trace.moe/video/${doc.anilistId}/${doc.fileName}?t=${doc.at}&token=${doc.tokenThumb}"
            )
            (context as Activity).startActivity(intent)
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}