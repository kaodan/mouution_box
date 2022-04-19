package com.srcbox.file.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.srcbox.file.R
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.popup.ChangeThemePopup
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.SpTool
import kotlinx.android.synthetic.main.change_theme_item.view.*


class ChangeThemeItem(val changeThemePopup: ChangeThemePopup, val arrayL: JSONArray) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.change_theme_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayL.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val color = arrayL.getString(position)
            itemView.theme_text.text = color
            itemView.theme_text.setTextColor(Color.parseColor(color))
            itemView.setOnClickListener {
                val rgbS = EggUtil.toRgba(Color.parseColor(color))
                val rgbInt = Color.argb(22, rgbS[0], rgbS[1], rgbS[2])
                SpTool.putSettingString("themeColor", color)
                SpTool.putSettingString("themeTransColor", rgbInt.toString())
                AppSetting.colorStress = color
                AppSetting.colorTransTress = "#${rgbInt.toString(16)}"
                changeThemePopup.dismiss()
            }
        }
    }
}