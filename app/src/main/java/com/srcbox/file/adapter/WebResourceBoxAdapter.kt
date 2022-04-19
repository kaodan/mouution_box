package com.srcbox.file.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.XPopup
import com.srcbox.file.R
import com.srcbox.file.data.ResourceData
import com.srcbox.file.ui.popup.SelectDownOrShowPopup

class WebResourceBoxAdapter(val context: Context, val resources: ArrayList<ResourceData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
//        resources.reversed()
    }

    companion object {
        var fileType = arrayOf("")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_web_resource_box, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return resources.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val resourceData = resources[position]
//        (holder.itemView as QMUILinearLayout).radius = 30

        holder as ViewHolder
        holder.link.text = resourceData.url
        holder.title.text = resourceData.title
        holder.contentType.text = resourceData.mediaType.toString()

        holder.itemView.setOnClickListener {
            val selectDownOrShowPopup = SelectDownOrShowPopup(context)
            selectDownOrShowPopup.resourceData = resourceData
            XPopup.Builder(context).asCustom(selectDownOrShowPopup).show()
        }

        when (resourceData.mediaType.type) {
            "video" -> {
                holder.fileTypeImage.setImageDrawable(context.getDrawable(R.drawable.video))
            }

            "text" -> {
                holder.fileTypeImage.setImageDrawable(context.getDrawable(R.drawable.text))
            }

            "image" -> {
                holder.fileTypeImage.setImageDrawable(context.getDrawable(R.drawable.image))

            }

            "audio" -> {
                holder.fileTypeImage.setImageDrawable(context.getDrawable(R.drawable.music))

            }

            else -> {
                holder.fileTypeImage.setImageDrawable(context.getDrawable(R.drawable.file))
            }
        }
        val conLayParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        fileType.forEach {
            if (!resourceData.mediaType.toString().contains(it)) {
                conLayParams.height = 0
                return@forEach
            } else {
                println(it)
                conLayParams.height = RecyclerView.LayoutParams.WRAP_CONTENT
            }
        }
    }

/*
    fun filterType(fileType: String) {
        this.fileType = fileType
    }*/

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val link: TextView = itemView.findViewById<TextView>(R.id.link)
        val title: TextView = itemView.findViewById<TextView>(R.id.title)
        val contentType: TextView = itemView.findViewById<TextView>(R.id.contentType)
        val fileTypeImage: ImageView = itemView.findViewById<ImageView>(R.id.fileTypeImg)
    }
}