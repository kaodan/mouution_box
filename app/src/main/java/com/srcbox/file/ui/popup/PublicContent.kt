package com.srcbox.file.ui.popup

import android.app.Activity
import android.content.Context
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.CenterPopupView
import com.srcbox.file.R
import com.srcbox.file.data.`object`.AppSetting
import okhttp3.*
import www.linwg.org.lib.LCardView
import java.io.IOException
import java.lang.Exception

class PublicContent(context: Context) : CenterPopupView(context) {
    override fun onCreate() {
        super.onCreate()
        findViewById<LCardView>(R.id.public_content_on).setOnClickListener {
            dismiss()
        }
        val publicContentText = findViewById<TextView>(R.id.public_content_text)
        publicContentText.text = "获取公告中..."
        val client = OkHttpClient()
        val request = Request.Builder().url(AppSetting.publicContentUrl).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                publicContentText.text = "获取失败，请检查网络"
            }

            override fun onResponse(call: Call, response: Response) {
                try {

                    val jsonO = JSON.parseObject(response.body!!.string())
                    (context as Activity).runOnUiThread {
                        publicContentText.text = jsonO.getString("con")
                    }
                } catch (e: Exception) {

                }
            }
        })
    }

    override fun getImplLayoutId(): Int {
        return R.layout.public_content_popup
    }
}