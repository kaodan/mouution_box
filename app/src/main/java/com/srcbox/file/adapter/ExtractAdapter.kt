package com.srcbox.file.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srcbox.file.R
import com.srcbox.file.data.ExtractData
import kotlinx.android.synthetic.main.extract_item.view.*

class ExtractAdapter(private val arrayList: ArrayList<ExtractData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.extract_item,parent,false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder){
            holder.bind(position)
        }
    }

    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bind(position: Int){
            val arrayO = arrayList[position]
            itemView.ext_text.text = arrayO.fileName
            itemView.ext_progress.max = arrayO.fileSize.toInt()
            itemView.ext_progress.progress = arrayO.fileProgressThis.toInt()
        }
    }
}