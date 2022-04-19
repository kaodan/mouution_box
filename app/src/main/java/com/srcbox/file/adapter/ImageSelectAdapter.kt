package com.srcbox.file.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.XPopupImageLoader
import com.srcbox.file.R
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.image_select_item.view.*
import java.io.File

class ImageSelectAdapter(val context: Context, val arrayList: List<File>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.image_select_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
/*
    override fun getItemViewType(position: Int): Int {
        return position
    }*/

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            Glide.with(context).load(arrayList[position]).placeholder(R.mipmap.placeholder)
                .error(R.mipmap.error).into(itemView.img)

            itemView.setOnClickListener {
                XPopup.Builder(context)
                    .asImageViewer(itemView.img, arrayList[position], ImageLoader()).show()
            }
        }
    }

    inner class ImageLoader : XPopupImageLoader {
        override fun loadImage(position: Int, uri: Any, imageView: ImageView) {
            uri as File
            Glide.with(context).load(uri).placeholder(R.mipmap.placeholder).error(R.mipmap.error)
                .into(imageView)
        }

        override fun getImageFile(context: Context, uri: Any): File? {

            uri as File
            println(uri.absolutePath)
//            return File("")
            return Glide.with(context).downloadOnly().load(uri).submit().get();
        }
    }
}