package com.srcbox.file.ui.popup

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.lxj.xpopup.core.BottomPopupView
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.ui.util.GetWebResource
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.web_input_popup.view.*

class WebSourceGetPopup(context: Context) : BottomPopupView(context) {
    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        super.onCreate()

        findViewById<TextView>(R.id.web_out_dir).text =
            "图片保存至：${AppStorageData.getFileOutFile()}/网页图片"
        val arrayView = ArrayList<View>()
        rootView.findViewsWithText(arrayView, "theme_tag", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
        arrayView.forEach {
            GlobUtil.changeTheme(it)
        }
        findViewById<TextView>(R.id.yes_link).setOnClickListener {
            link_edit.text.toString().let {
                if (it.contains("http://") || it.contains("https://")) {
                    val intent = Intent(context, GetWebResource::class.java)
                    intent.putExtra("link", it)
                    context.startActivity(intent)
                } else {
                    EggUtil.toast("链接格式错误")
                }
            }
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.web_input_popup
    }
}