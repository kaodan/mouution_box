package com.srcbox.file.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.SimpleCallback
import com.lxj.xpopup.interfaces.XPopupCallback
import com.rengwuxian.materialedittext.MaterialEditText
import com.srcbox.file.R
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.popup.SignAProtocolPopup
import com.srcbox.file.ui.popup.UpdateAppPopup
import okhttp3.*
import www.linwg.org.lib.LCardView
import java.io.IOException

class GlobUtil {
    companion object {
        fun changeTheme(vararg v: View, isTrans: Boolean = false) {
            v.forEach {
                when (it) {
                    is TextView -> {
                        println("已设置")
                        if (!isTrans) {
                            it.setTextColor(Color.parseColor(AppSetting.colorStress))
                        } else {
                            it.setTextColor(Color.parseColor(AppSetting.colorTransTress))
                        }
                    }

                    is LinearLayout -> {
                    }

                    is LCardView -> {
                        if (!isTrans) {
                            it.cardBackgroundColor = Color.parseColor(AppSetting.colorStress)
                            it.shadowColor = Color.parseColor(AppSetting.colorTransTress)
                        } else {
                            it.cardBackgroundColor = Color.parseColor(AppSetting.colorTransTress)
                        }
                    }

                    is MaterialEditText -> {
                        println("已设置")
                        it.setPrimaryColor(Color.parseColor(AppSetting.colorStress))
//                        it.setBaseColor(Color.parseColor(AppSetting.colorStress))
                    }
                }
            }
        }

        fun changeTitle(context: Activity, isUseTitleBar: Boolean = true) {
            if (isUseTitleBar) {

                context.findViewById<TitleBar>(R.id.title_bar)?.let {
                    EggUtil.loadIcon(
                        context,
                        AppSetting.colorStress,
                        it.leftView
                    )

                    it.setOnTitleBarListener(object : OnTitleBarListener {
                        override fun onLeftClick(v: View?) {
                            context.finish()
                        }

                        override fun onRightClick(v: View?) {}
                        override fun onTitleClick(v: View?) {}
                    })
                }

            }

            ImmersionBar.with(context)!!.barColor("#ffffff")
                .autoDarkModeEnable(true, 0.2f)
                .statusBarView(R.id.statusSplitView).init()
        }

        fun checkIsNewVer(context: Context, onNoVer: (() -> Unit)? = null) {
            val client = OkHttpClient()
            val request = Request.Builder().url(AppSetting.updateLink).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {


                    try {
                        val jo = JSON.parseObject(response.body!!.string())
                        val newVersionCode = jo.getLong("version_code")
                        val newVersionName = jo.getString("version_name")
                        val newContent = jo.getString("content")
                        val newLink = jo.getString("link")
                        val currentVersion =
                            context.packageManager.getPackageInfo(
                                context.packageName,
                                0
                            ).versionCode.toLong()
                        if (newVersionCode > currentVersion) {
                            val uap = UpdateAppPopup(context)
                            val xp = XPopup.Builder(context)
                                .setPopupCallback(object : SimpleCallback() {
                                    override fun onCreated() {
                                        uap.setContent(newVersionName, newContent, newLink)
                                    }
                                }).asCustom(uap)
                            xp.show()
                        } else {
                            if (onNoVer != null) {
                                onNoVer()
                            }
                        }
                    }catch (e:Exception){

                    }





                }
            })
        }

        fun signAProtocol(activity: Activity, isShow: Boolean = false) {
            val isProtocol = SpTool.getSettingString("isProtocol", false.toString())
            if ((!isProtocol.toBoolean()) || isShow) {
                XPopup.Builder(activity)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .asCustom(SignAProtocolPopup(activity))
                    .show()
            }
        }
    }
}
