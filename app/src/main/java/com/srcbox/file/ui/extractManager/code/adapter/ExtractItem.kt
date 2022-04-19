package com.srcbox.file.ui.extractManager.code.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.egg.extractmanager.ExtractInstance
import com.srcbox.file.R
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.extractManager.code.ExtractManager
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.extract_item_x.view.*


class ExtractItem(private val context: Context, private val hashMap: Map<String, ExtractInstance>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mData: ArrayList<Map.Entry<String, ExtractInstance>> = ArrayList()

    init {
        mData.addAll(hashMap.entries)
//        Set<Map.Entry<K, V>>
//        Set<Map.Entry<K, V>>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.extract_item_x, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bind(position)
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            EggUtil.loadIcon(
                context,
                AppSetting.colorStress,
                itemView.extract_item_x_cancel
            )
            val extractIn = mData[position]
//            hashMap.keys.toTypedArray()
            val progress = EggUtil.computeProgress(
                extractIn.value.getMessages().thisFilePos,
                extractIn.value.getMessages().total
            )

            when (extractIn.value.getState()) {
                ExtractManager.STATE_START -> {
                    itemView.extract_item_x_start_or_pause.text = "\ue640"
                }

                ExtractManager.STATE_PAUSE -> {
                    itemView.extract_item_x_start_or_pause.text = "\uebcd"
                }

                ExtractManager.STATE_CANCEL -> {
                    itemView.extract_item_x_start_or_pause.text = "\uebcd"
                }

                ExtractManager.STATE_SUCCESS -> {
                    itemView.extract_item_x_start_or_pause.text = "\uebcd"
                }

            }
            EggUtil.loadIcon(
                context,
                AppSetting.colorStress,
                itemView.extract_item_x_start_or_pause
            )
            itemView.extract_item_x_start_or_pause.setOnClickListener {
                it as TextView
                println("当前的状态是：${extractIn.value.getState()}")
                when (extractIn.value.getState()) {
                    ExtractManager.STATE_START -> {
                        extractIn.value.pause()
                    }

                    ExtractManager.STATE_PAUSE -> {
                        extractIn.value.start()
                    }

                    ExtractManager.STATE_CANCEL -> {
                        extractIn.value.start()
                    }

                    ExtractManager.STATE_SUCCESS -> {
                        extractIn.value.start()
                    }
                }
            }

            itemView.extract_item_x_cancel.setOnClickListener {
                extractIn.value.cancel()
                ExtractManager.extractTaskHashMap.remove(extractIn.key)
                mData.removeAt(position)
                notifyDataSetChanged()
                println("已关闭")
            }
            itemView.extract_title.text = extractIn.value.getMessages().appName
            itemView.extract_progress.progress = progress.toInt()
        }
    }
}