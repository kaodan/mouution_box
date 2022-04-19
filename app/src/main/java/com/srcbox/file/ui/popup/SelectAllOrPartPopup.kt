package com.srcbox.file.ui.popup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import com.lxj.xpopup.core.CenterPopupView
import com.srcbox.file.R
import com.srcbox.file.ui.PlayVideoActivity

class SelectAllOrPartPopup(context: Context) : CenterPopupView(context) {
    private var urlAll = ""
    private var urlPart = ""
    override fun onCreate() {
        super.onCreate()
        val selectAll = findViewById<LinearLayout>(R.id.selectAll)
        val selectPart = findViewById<LinearLayout>(R.id.selectPart)
        selectAll.setOnClickListener {
            if (urlAll.isEmpty()) {
                return@setOnClickListener
            }
            val intent = Intent(context, PlayVideoActivity::class.java)
            intent.putExtra("url", urlAll)
            (context as Activity).startActivity(intent)
        }

        selectPart.setOnClickListener {
            if (urlPart.isEmpty()) {
                return@setOnClickListener
            }
            val intent = Intent(context, PlayVideoActivity::class.java)
            intent.putExtra("url", urlPart)
            (context as Activity).startActivity(intent)
        }
    }

    fun setContent(allUrl: String, partUrl: String) {
        this.urlAll = allUrl
        this.urlPart = partUrl
    }

    override fun getImplLayoutId(): Int {
        return R.layout.select_video_all_or_part
    }
}