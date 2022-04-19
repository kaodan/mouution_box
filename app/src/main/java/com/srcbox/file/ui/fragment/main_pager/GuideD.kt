package com.srcbox.file.ui.fragment.main_pager

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.srcbox.file.R
import com.srcbox.file.application.EggApplication
import com.srcbox.file.ui.MainActivity
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.SpTool
import com.tencent.smtt.sdk.WebView
import www.linwg.org.lib.LCardView
import kotlin.concurrent.thread
import kotlin.system.exitProcess


class GuideD : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var isShow = false
        val v = LayoutInflater.from(context).inflate(R.layout.guide_page_d, container, false)
        Glide.with(this).load(R.mipmap.banner_protocol)
            .into(v.findViewById<ImageView>(R.id.title_img))
        v.findViewById<LCardView>(R.id.agree).setOnClickListener {
            if (!isShow) {
                EggUtil.toast("请查看协议")
                return@setOnClickListener
            }

            SpTool.putSettingString("once", true.toString())
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }

        v.findViewById<LCardView>(R.id.refuse).setOnClickListener {
            exitProcess(0)
        }

        v.findViewById<Button>(R.id.show_xy).setOnClickListener {
            val tenWbe = v.findViewById<WebView>(R.id.ten_web)

            tenWbe.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            thread {
                val c = EggIO.readFile((EggApplication.context.assets.open("html/userXy.html")))
                activity?.runOnUiThread {
                    /*tenWbe.loadData(
                        c,
                        "text/html",
                        "utf-8"
                    )*/
                    tenWbe.loadDataWithBaseURL(null, c, "text/html", "utf-8", null)
                }
            }
            v.findViewById<LCardView>(R.id.show_xy_container).visibility = View.GONE
            tenWbe.visibility = View.VISIBLE
            isShow = true
        }
        return v
    }
}