package com.srcbox.file.ui.popup

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.lxj.xpopup.core.BottomPopupView
import com.qmuiteam.qmui.layout.QMUILinearLayout
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.srcbox.file.R
import com.srcbox.file.adapter.WebResourceBoxAdapter
import com.srcbox.file.data.ResourceData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.popup_web_resource_box.view.*
import java.lang.Exception

class WebResourceBoxPopup(context: Context) : BottomPopupView(context) {
    companion object {
        val resourcesArrList = ArrayList<ResourceData>()
    }

    override fun onCreate() {
        super.onCreate()
        val rootLayPar = root.layoutParams as FrameLayout.LayoutParams
        noData.visibility = View.GONE
        resourcesList.visibility = View.VISIBLE
        rootLayPar.height =
            (QMUIDisplayHelper.getScreenHeight(context) / 2) + (QMUIDisplayHelper.getScreenHeight(
                context
            ) / 3)

        root.radius = QMUIDisplayHelper.dp2px(context, 10)

        try {
            isListEmpty()
        } catch (e: Exception) {
            EggUtil.toast("失败，请重试")
            dismiss()
            return
        }

//      resourcesList.layoutManager = GridLayoutManager(context, 1)
        val webResourceBoxAdapter = WebResourceBoxAdapter(context, resourcesArrList)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        resourcesList.layoutManager = linearLayoutManager

        resourcesList.adapter = webResourceBoxAdapter
        var currentSelItem = fileTypeAllText

        fileTypeAll.setOnClickListener {
            val selItem = fileTypeAllText
            if (currentSelItem == selItem) {
                return@setOnClickListener
            } else {
                currentSelItem.setTextColor(Color.parseColor(AppSetting.colorGray))
                selItem.setTextColor(Color.parseColor(AppSetting.colorStress))
                currentSelItem = selItem
            }
            WebResourceBoxAdapter.fileType = arrayOf("")
            isListEmpty()
            resourcesList.adapter?.notifyDataSetChanged()
        }

        fileTypeText.setOnClickListener {
            val selItem = fileTypeTextText
            if (currentSelItem == selItem) {
                return@setOnClickListener
            } else {
                currentSelItem.setTextColor(Color.parseColor(AppSetting.colorGray))
                selItem.setTextColor(Color.parseColor(AppSetting.colorStress))
                currentSelItem = selItem
            }
            WebResourceBoxAdapter.fileType = arrayOf("text")
            isListEmpty()
            resourcesList.adapter?.notifyDataSetChanged()
        }

        fileTypeMusic.setOnClickListener {
            val selItem = fileTypeMusicText
            if (currentSelItem == selItem) {
                return@setOnClickListener
            } else {
                currentSelItem.setTextColor(Color.parseColor(AppSetting.colorGray))
                selItem.setTextColor(Color.parseColor(AppSetting.colorStress))
                currentSelItem = selItem
            }
            WebResourceBoxAdapter.fileType = arrayOf("audio")
            isListEmpty()
            resourcesList.adapter?.notifyDataSetChanged()
        }

        fileTypeVideo.setOnClickListener {
            val selItem = fileTypeVideoText
            if (currentSelItem == selItem) {
                return@setOnClickListener
            } else {
                currentSelItem.setTextColor(Color.parseColor(AppSetting.colorGray))
                selItem.setTextColor(Color.parseColor(AppSetting.colorStress))
                currentSelItem = selItem
            }
            WebResourceBoxAdapter.fileType = arrayOf("video", "octet-stream", "mp4")
            isListEmpty()
            resourcesList.adapter?.notifyDataSetChanged()
        }

        fileTypeImage.setOnClickListener {
            val selItem = fileTypeImageText
            if (currentSelItem == selItem) {
                return@setOnClickListener
            } else {
                currentSelItem.setTextColor(Color.parseColor(AppSetting.colorGray))
                selItem.setTextColor(Color.parseColor(AppSetting.colorStress))
                currentSelItem = selItem
            }

            WebResourceBoxAdapter.fileType = arrayOf("image")
            isListEmpty()
            resourcesList.adapter?.notifyDataSetChanged()
        }
//        root.right = QMUIDisplayHelper.dp2px(context, 15)
    }

    private fun isListEmpty() {

       /* var isNo = true
        try {
            WebResourceBoxPopup.resourcesArrList.forEach { resourceDara ->
                WebResourceBoxAdapter.fileType.forEach {
                    if (resourceDara.mediaType.toString().contains(it)) {
                        isNo = false
                    }
                }
            }

            if (isNo) {
                noData.visibility = View.VISIBLE
                resourcesList.visibility = View.GONE
            } else {
                noData.visibility = View.GONE
                resourcesList.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            EggUtil.toast("失败，请重试2${e.message}")
            dismiss()
        }*/

    }

    override fun getImplLayoutId(): Int {
        return R.layout.popup_web_resource_box
    }

    override fun onShow() {
        super.onShow()
        resourcesList.adapter?.notifyDataSetChanged()
    }
}