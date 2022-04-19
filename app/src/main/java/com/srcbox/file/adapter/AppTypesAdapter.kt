package com.srcbox.file.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.srcbox.file.R
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.AppList
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.home_listv_item.view.*

class AppTypesAdapter(
    val context: Context,
    private val jsonMapIterable: JSONObject
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val mData: ArrayList<Map.Entry<String, Any>> = ArrayList()
//        private val mData: ArrayList<String> = ArrayList()
    }

    init {

/*        mData.add("ccc")
        mData.add("ccc")
        mData.add("ccc")
        mData.add("ccc")
        mData.add("ccc")
        mData.add("ccc")
        mData.add("ccc")
        mData.add("ccc")*/
        mData.clear()
        jsonMapIterable.innerMap.entries
//        val ha = HashMap<String, String>().entries
        mData.addAll(jsonMapIterable.innerMap.entries)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.home_listv_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("CheckResult")
        fun bind(position: Int) {
            val itemMap = mData[position]
            val nameAndIconSplit = itemMap.key.split("$")
            itemView.fun_name.text = nameAndIconSplit[0]
            itemView.fun_img.text = nameAndIconSplit[1]
            if (!itemView.fun_name.text.toString().contains("全部") && !itemView.fun_name.text.toString().contains("系统")) {
                itemView.fun_tui_img.visibility = View.GONE
            }
            EggUtil.loadIcon(context, AppSetting.colorStress, itemView.fun_img)

            itemView.setOnClickListener {
                AppList.typeAppsMessage = itemMap.value as JSONArray
                if (AppList.typeAppsMessage == null) {
                    EggUtil.toast("失败，请重试")
                    return@setOnClickListener
                }
                context.startActivity(Intent(context, AppList::class.java))
            }
//            nameAndIconSplit[1]
            /*val itemMap = jsonMapIterable.next()
            val nameAndIconSplit = itemMap.key.split("$")
            itemView.fun_name.text = nameAndIconSplit[0]
            itemView.fun_img.text = nameAndIconSplit[1]
            EggUtil.loadIcon(context, AppSetting.colorStress, itemView.fun_img)
//            nameAndIconSplit[1]


            itemView.setOnClickListener {
                AppList.typeAppsMessage = itemMap.value as JSONArray
                if (AppList.typeAppsMessage == null) {
                    EggUtil.toast("失败，请重试")
                    return@setOnClickListener
                }
                context.startActivity(Intent(context, AppList::class.java))
            }
            */
        }
    }
}