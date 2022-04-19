package com.srcbox.file.ui.searchimginfo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.srcbox.file.R
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.searchimginfo.data.ImgInfoData
import com.srcbox.file.util.EggUtil

class SearchImgInfoListAdapter(val context: Context, val imgsInfo: ArrayList<ImgInfoData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_img, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return imgsInfo.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        val imgInfo = imgsInfo[position]
        Glide.with(context).load(imgInfo.imgLink).placeholder(R.mipmap.placeholder)
            .error(R.mipmap.placeholder).into(holder.img)
        holder.similarity.text = "相似度：${imgInfo.similarityInfo}"
        holder.title.text = imgInfo.title
        holder.img.setOnClickListener {
            EggUtil.goBrowser(context,imgInfo.onLink)
        }
        if (holder.content.size == 0) {
            imgInfo.contentColumns.forEach { content ->
                val textV = TextView(context)
                textV.setOnClickListener {
                    EggUtil.goBrowser(context, content.link)
                }
                textV.text = "${content.name} ${content.number}"
                textV.setTextColor(Color.parseColor(AppSetting.colorStress))
                textV.paint.isAntiAlias = true
                textV.paint.flags = Paint.UNDERLINE_TEXT_FLAG
                textV.textSize = 15f

                holder.content.addView(textV)
                (textV.layoutParams as LinearLayout.LayoutParams).topMargin =
                    QMUIDisplayHelper.dp2px(context, 15)
            }
        }
    }

    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById<ImageView>(R.id.img)
        val content: LinearLayout = itemView.findViewById<LinearLayout>(R.id.content)
        val title: TextView = itemView.findViewById<TextView>(R.id.title)
        val similarity: TextView = itemView.findViewById<TextView>(R.id.similarity)
    }
}