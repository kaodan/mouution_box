package com.srcbox.file.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.srcbox.file.R
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.about.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.concurrent.thread

class AboutActivity : AppCompatActivity() {
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
        GlobUtil.changeTitle(this)


        Glide.with(this).load("http://q1.qlogo.cn/g?b=qq&nk=1970284668&s=640")
            .placeholder(R.mipmap.placeholder).error(R.mipmap.error).into(about_egg_icon)
        Glide.with(this).load("http://q1.qlogo.cn/g?b=qq&nk=3441152376&s=640")
            .placeholder(R.mipmap.placeholder).error(R.mipmap.error).into(about_wugui_icon)
        Glide.with(this).load("http://q1.qlogo.cn/g?b=qq&nk=1720794953&s=640")
            .placeholder(R.mipmap.placeholder).error(R.mipmap.error).into(about_san_icon)
        about_egg_blog.setOnClickListener {
            EggUtil.goBrowser(this, "https://kaodan.gitee.io")
        }

        about_wugui_blog.setOnClickListener {
            EggUtil.goBrowser(this, "http://rate520.cn")

        }

        about_san_blog.setOnClickListener {
            EggUtil.goBrowser(this, "http://atzinx.xyz/")
        }

        GlobUtil.changeTheme(about_egg_blog, about_wugui_blog, about_san_blog)
        thread {
            try {
                val friendOkHttpClient = OkHttpClient()
                val friendExecute = friendOkHttpClient.newCall(
                    Request.Builder()
                        .url("https://juyi-1253946182.cos.ap-chengdu.myqcloud.com/mouu/json/friends.json")
                        .build()
                ).execute()
                val friendStr = friendExecute.body?.string()
                friendStr?.let {
                    val friendJsonArr = JSON.parseArray(it)
                    val viewSize =
                        (QMUIDisplayHelper.getScreenWidth(this) / 3) - QMUIDisplayHelper.dp2px(this, 15)
                    friendJsonArr.forEach { friendJson ->
                        friendJson as JSONObject
                        val view =
                            LayoutInflater.from(this)
                                .inflate(R.layout.view_item_friend_app, apps, false)
                        view.findViewById<TextView>(R.id.appName).text = friendJson.getString("name")
                        val appIconView = view.findViewById<ImageView>(R.id.appIcon)
                        runOnUiThread {
                            try {
                                Glide.with(this).load(friendJson.getString("icon")).error(R.mipmap.error)
                                    .placeholder(R.mipmap.placeholder).into(appIconView)
                            }catch (e:Exception){}

                            apps.addView(view)
                            view.setOnClickListener {
                                val vLayPar = view.layoutParams
                                vLayPar.width = viewSize
                                vLayPar.height = viewSize
                                EggUtil.goBrowser(this, friendJson.getString("onlink"))
                            }
                        }

                    }
                }
            }catch (e:Exception){}

        }
    }
}