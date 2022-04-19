package com.srcbox.file.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.srcbox.file.R
import com.srcbox.file.data.UserTogetherData

class MeData(val arrayList: ArrayList<UserTogetherData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.me_data_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val numberText = itemView.findViewById<TextView>(R.id.number_text)
            val measureWordText = itemView.findViewById<TextView>(R.id.measure_word_text)
            numberText.text = arrayList[position].numberText
            measureWordText.text = arrayList[position].measureWordText
        }
    }
}