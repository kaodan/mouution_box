package com.srcbox.file.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.XPopup
import com.srcbox.file.R
import com.srcbox.file.data.`object`.CapData
import com.srcbox.file.data.`object`.CapturedImageConfig
import com.srcbox.file.data.`object`.ScreenCaptureInfo
import com.srcbox.file.service.CapturedImagesServer
import com.srcbox.file.ui.popup.WebSourceGetPopup
import com.srcbox.file.ui.popup.WebToFileZipPopup
import com.srcbox.file.ui.screen.Screenshot
import com.srcbox.file.ui.util.ResourceExtractActivity
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.ScreenCaptureUtil
import kotlinx.android.synthetic.main.home_util_item.view.*

class HomeUtilAdapter(val context: Context, val arrayList: Array<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var screenshot: Screenshot? = null

    init {
        screenshot = Screenshot(context as Activity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.home_util_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val content = arrayList[position]
            val title = content.split("$")[0]
            val isVip = content.split("$")[1]
            if (isVip == "true") {
                itemView.vip_icon.visibility = View.VISIBLE
            } else {
                itemView.vip_icon.visibility = View.GONE
            }

            itemView.home_util_title.text = title
//            itemView.home_util_title.setTextColor(Color.parseColor(AppSetting.colorStress))
            itemView.setOnClickListener {
                when (title) {
                    "屏幕资源采集" -> {
                        CapData.type = CapData.TYPE_NO_SHOW
                        openScreen()
                    }

                    "可视资源采集" -> {
                        Screenshot.type = Screenshot.TYPE_SCREEN
                        screenshot?.show()
                    }

                    "悬浮取色" -> {
                        Screenshot.type = Screenshot.TYPE_FLOAT_COLOR
                        screenshot?.show()
                    }

                    "网页图片采集" -> {
                        XPopup.Builder(context).asCustom(WebSourceGetPopup(context)).show()
                    }

                    "网站源码打包" -> {
                        openWebSource()
                    }

                    "资源嗅探" -> {
                        (context as Activity).startActivity(
                            Intent(
                                context,
                                ResourceExtractActivity::class.java
                            )
                        )
                    }
                }
            }
        }

        private fun openWebSource() {
            XPopup.Builder(context).asCustom(WebToFileZipPopup(context)).show()
        }

        private fun openScreen() {

            context.startService(
                Intent(
                    context,
                    CapturedImagesServer::class.java
                )
            )

            if (!ScreenCaptureInfo.isStart) {

                if (!CapturedImageConfig.isServerPermission) {
                    ScreenCaptureUtil.initAccessibilityPermissions(context)
                    return
                }

                if (CapturedImageConfig.isServerStart) {
                    ScreenCaptureUtil.readyCapture(
                        context as Activity,
                        ScreenCaptureInfo.CODE
                    )
                } else {
                    EggUtil.toast("服务未启动")
                    //激活启动
                    ScreenCaptureUtil.initAccessibilityPermissions(context)
                }
            } else {
                EggUtil.toast("当前悬浮窗已存在，请关闭。")
            }
        }
    }
}


/*val asLoading = XPopup.Builder(context).asLoading()
                      asLoading.show()
                      if (AVUser.currentUser() == null) {
                          EggUtil.toast("请登录")
                          context.startActivity(Intent(context, LoginActivity::class.java))
                          asLoading.delayDismiss(500)
                          return@setOnClickListener
                      } else {
                          Member.getVipDate {
                              when (it) {
                                  "0" -> {
                                      asLoading.dismiss()
                                      EggUtil.toast("您不是VIP哦，请充值。")
                                      context.startActivity(
                                          Intent(
                                              context,
                                              GetMemberActivity::class.java
                                          )
                                      )
                                  }
                                  else -> {
                                      asLoading.delayDismiss(500)
                                      CapData.type = CapData.TYPE_SHOW
                                      openScreen()
                                  }
                              }
                          }
                      }*/

/*if (result == Member.TYPE_NO_LOGIN.toString()) {
    EggUtil.toast("请登录")
    asLoading.delayDismiss(500)
    context.startActivity(Intent(context, LoginActivity::class.java))
    return@launch
}
if (result != null) {
    asLoading.delayDismiss(500)
    XPopup.Builder(context).asCustom(WebToFileZipPopup(context)).show()
} else {
    asLoading.delayDismiss(500)
    EggUtil.toast("请充值")
    (context as Activity).startActivity(Intent(context,GetMemberActivity::class.java))
}*/
//                        EggUtil.toast("点击小")
//                        EggUtil.toast(this@LocalFunList.context, "${ScreenCaptureInfo.isStart}")

/*





context.startService(
                                Intent(
                                    context,
                                    CapturedImagesServer::class.java
                                )
                            )
                            CapData.type = CapData.TYPE_NO_SHOW
//                        EggUtil.toast(this@LocalFunList.context, "${ScreenCaptureInfo.isStart}")

                            if (!ScreenCaptureInfo.isStart) {
                                if (!CapturedImageConfig.isServerPermission) {
                                    ScreenCaptureUtil.initAccessibilityPermissions(context)
                                    return@setOnClickListener
                                }

                                if (CapturedImageConfig.isServerStart) {
                                    ScreenCaptureUtil.readyCapture(
                                        context as Activity,
                                        ScreenCaptureInfo.CODE
                                    )
                                } else {
                                    EggUtil.toast("服务未启动")
                                    //激活启动
                                    ScreenCaptureUtil.initAccessibilityPermissions(context)
                                }
                            } else {
                                EggUtil.toast("已启动")
                            }




 */