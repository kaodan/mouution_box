package com.srcbox.file.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import cn.leancloud.codec.MD5
import com.srcbox.file.R
import com.srcbox.file.application.EggApplication
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.`object`.CapData
import com.srcbox.file.data.ShowIconData
import com.srcbox.file.util.EggUtil
import com.srcbox.file.view.StokeRect
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import java.io.File

class ShadeService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var view: View
    private var showIcons = ArrayList<ShowIconData>()
    private var thisShowIcon: Int = 0
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showFloatWindow()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showFloatWindow() {
        thisShowIcon = 0
        showIcons.clear()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        view = LayoutInflater.from(EggApplication.context)
            .inflate(R.layout.shade_activity, null, false)
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(EggApplication.context)) {
                return
            }
        }

        val layPar = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layPar.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layPar.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layPar.format = PixelFormat.RGBA_8888
        layPar.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layPar.width = WindowManager.LayoutParams.MATCH_PARENT
        layPar.height = WindowManager.LayoutParams.MATCH_PARENT

        val height =
            EggUtil.getWindowHeight(EggApplication.context) - EggUtil.getStatusBarHeight(this)
        println("高度是：$height")
        val cBit = EggUtil.cutBitmap(
            CapData.bitmap,
            0,
            EggUtil.getStatusBarHeight(this),
            EggUtil.getWindowWidth(EggApplication.context),
            height
        )

        view.findViewById<ImageView>(R.id.bg).setImageBitmap(cBit)
        if (CapData.arrayRectList.size == 0) {
            EggUtil.toast("没获取到数据")
            return
        }
        var i = 0
        //过滤
        CapData.arrayRectList.forEach {
            kotlin.run {
                val rect = Rect()
                it.getBoundsInScreen(rect)
                if (rect.height() == 1 || rect.width() == 1) {
                    return@run
                }
                val bit = EggUtil.cutBitmap(
                    CapData.bitmap,
                    rect.left,
                    rect.top,
                    rect.width(),
                    rect.height()
                )
                    ?: return@run
                val stokeRect = StokeRect(
                    this,
                    rect.left.toFloat(),
                    rect.top.toFloat() - EggUtil.getStatusBarHeight(this),
                    rect.right.toFloat(),
                    rect.bottom.toFloat() - EggUtil.getStatusBarHeight(this)
                )
                println(it.windowId)
                var resourceName = it.viewIdResourceName
                if (resourceName == null) {
                    resourceName = "无ID"
                }
                val showIcon = ShowIconData(
                    bit,
                    stokeRect,
                    resourceName
                )
                showIcons.add(showIcon)
            }
        }

        showIcons.forEach {
            it.stokeRect.setOnClickListener {
                if (view.findViewById<RelativeLayout>(R.id.shade_node_panel_root).visibility == View.GONE) {
                    view.findViewById<RelativeLayout>(R.id.shade_node_panel_root).visibility =
                        View.VISIBLE
                } else {
                    view.findViewById<RelativeLayout>(R.id.shade_node_panel_root).visibility =
                        View.GONE
                }
                val showIconData = getThisRectBit()
                view.findViewById<ImageView>(R.id.shade_img).setImageBitmap(showIconData.bitmap)
                view.findViewById<TextView>(R.id.resource_id_name).text =
                    showIconData.resourceIdName
            }
            view.findViewById<ConstraintLayout>(R.id.root).addView(it.stokeRect)
        }

        val cView = LayoutInflater.from(EggApplication.context)
            .inflate(R.layout.shade_node_panel, null, false)
        view.findViewById<ConstraintLayout>(R.id.root).addView(cView)
        val cVLayPar = cView.layoutParams as ConstraintLayout.LayoutParams
        cVLayPar.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        cVLayPar.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        /*view.setOnClickListener {
            println("干嘛")

        }*/

        val selectNodeSeekBar = view.findViewById<IndicatorSeekBar>(R.id.select_node_seekBar)
        selectNodeSeekBar.min = 0f
        selectNodeSeekBar.max = (showIcons.size - 1).toFloat()
        selectNodeSeekBar.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
                val showIconData = getThisRectBit(seekParams!!.progress)
                thisShowIcon = seekParams!!.progress
                view.findViewById<ImageView>(R.id.shade_img)
                    .setImageBitmap(showIconData.bitmap)
                view.findViewById<TextView>(R.id.resource_id_name).text =
                    showIconData.resourceIdName
                view.findViewById<TextView>(R.id.shade_save_node).text = "保存"
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
            }
        }

        view.findViewById<TextView>(R.id.shade_exit).setOnClickListener {
            windowManager.removeView(view.rootView)
        }

        //下一个
        view.findViewById<LinearLayout>(R.id.shade_node_next).setOnClickListener {
            view.findViewById<TextView>(R.id.shade_save_node).text = "保存"
            thisShowIcon++
            if (thisShowIcon == CapData.arrayRectList.size - 1) {
                EggUtil.toast("到底了哦")
                thisShowIcon--
                return@setOnClickListener
            }
            val showIconData = getThisRectBit()
            view.findViewById<ImageView>(R.id.shade_img).setImageBitmap(showIconData.bitmap)
            view.findViewById<TextView>(R.id.resource_id_name).text = showIconData.resourceIdName
        }

        //上一个
        view.findViewById<LinearLayout>(R.id.shade_node_up).setOnClickListener {
            view.findViewById<TextView>(R.id.shade_save_node).text = "保存"
            thisShowIcon--
            if (thisShowIcon < 0) {
                EggUtil.toast("到顶了哦")
                thisShowIcon++
                return@setOnClickListener
            }
            val showIconData = getThisRectBit()
            view.findViewById<ImageView>(R.id.shade_img).setImageBitmap(showIconData.bitmap)
            view.findViewById<TextView>(R.id.resource_id_name).text = showIconData.resourceIdName
        }

        view.findViewById<TextView>(R.id.shade_save_node).setOnClickListener {
            it as TextView
            val f = File(
                AppStorageData.getFileOutFile(),
                "可视化抓捕/${MD5.computeMD5(((555..1000).random()).toString())}.png"
            )
            EggUtil.saveBitmapFile(
                getThisRectBit().bitmap, f
            )
            it.text = f.path
            EggUtil.toast("已保存至：${f.path}")
        }
        windowManager.addView(view, layPar)
    }

    fun getThisRectBit(thisS: Int = thisShowIcon): ShowIconData {
        for ((i, showIcon) in showIcons.withIndex()) {
            if (i == thisS) {
                showIcon.stokeRect.setStokeColor("#f44336")
                showIcon.stokeRect.setStokeWidth(7f)
            } else {
                showIcon.stokeRect.setStokeColor("#2196f3")
                showIcon.stokeRect.setStokeWidth(4f)
            }
        }
        if (thisS >= showIcons.size) {
            return showIcons[0]
        }
        return showIcons[thisS]
    }

    fun dismiss() {
        windowManager.removeView(view)
    }
}


/*CapData.arrayRectList[thisR].getBoundsInScreen(rect)
       val bit = EggUtil.cutBitmap(
           CapData.bitmap,
           rect.left,
           rect.top,
           rect.width(),
           rect.height()
       )*/
/*views.forEach {
    if (vi == thisR) {
        it.setStokeColor("#f44336")
        it.setStokeWidth(7f)
    } else {
        it.setStokeColor("#2196F3")
        it.setStokeWidth(4f)
    }
    vi++
}*/

/*
stokeRect.setOnClickListener {
    if (view.findViewById<RelativeLayout>(R.id.shade_node_panel_root).visibility == View.GONE) {
        view.findViewById<RelativeLayout>(R.id.shade_node_panel_root).visibility =
            View.VISIBLE
    } else {
        view.findViewById<RelativeLayout>(R.id.shade_node_panel_root).visibility =
            View.GONE
    }
    view.findViewById<ImageView>(R.id.shade_img).setImageBitmap(getThisRectBit())
}
stokeRect.setOnLongClickListener {
    true
}*/
//            view.findViewById<ConstraintLayout>(R.id.root).addView(stokeRect)
