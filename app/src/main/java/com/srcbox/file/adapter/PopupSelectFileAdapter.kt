package com.srcbox.file.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srcbox.file.R
import com.srcbox.file.ui.popup.filter
import kotlinx.android.synthetic.main.popup_select_file_item.view.*
import java.io.File

class PopupSelectFileAdapter(public val arrayList: ArrayList<File>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.popup_select_file_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            itemView.psi_title.text = arrayList?.get(position)?.name
            itemView.setOnClickListener {
                currentFile = arrayList?.get(position)
                println(currentFile)
                arrayList?.clear()
                val fl = filter(currentFile?.listFiles()?.toList())
                fl?.let {
                    arrayList?.addAll(it)
                    notifyDataSetChanged()
                }


            }
        }
    }

    companion object {
        var currentFile: File? = null
    }
}