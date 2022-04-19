package com.srcbox.file.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.srcbox.file.R
import com.srcbox.file.data.`object`.ScreenCaptureInfo
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.ScreenCaptureUtil


class FloatWin(private val context: Activity) {


    private var layPar: WindowManager.LayoutParams? = null
    private var windowManager: WindowManager? = null
    private var view: View? = null

    @SuppressLint("ClickableViewAccessibility", "RtlHardcoded")
    fun show() {

        if (context.isFinishing) {
            EggUtil.toast("出了点问题，请重新加入软件。")
            return
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(context)) {
                context.startActivityForResult(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    ), 0
                )
                Data.floatWin = this
                return
            }
        }

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        view = LayoutInflater.from(context).inflate(R.layout.float_win, null, false)
        val floatCancel = view?.findViewById<TextView>(R.id.floatCancel)
        val scanning = view?.findViewById<ImageView>(R.id.scann)
//        val drag = view?.findViewById<TextView>(R.id.drag)
        EggUtil.loadIcon(context, "#2196f3", floatCancel!!)
//        EggUtil.loadIcon(context, drag!!, "#F44336")
        scanning?.setImageBitmap(BitmapFactory.decodeStream(context.assets.open("img/scann.png")))
        layPar = WindowManager.LayoutParams()
        layPar?.gravity = Gravity.LEFT or Gravity.TOP
        layPar?.width = WindowManager.LayoutParams.WRAP_CONTENT
        layPar?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layPar?.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layPar?.format = PixelFormat.RGBA_8888
        layPar?.x = 100
        layPar?.y = 100

        if (Build.VERSION.SDK_INT >= 26) {
            layPar?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            println("A")
        } else {
            layPar?.type = WindowManager.LayoutParams.TYPE_PHONE
            println("B")
        }

        view?.setOnClickListener {
            view?.visibility = View.GONE
            if (ScreenCaptureInfo.screenCaptureUtilInterface != null) {
                ScreenCaptureInfo.screenCaptureUtilInterface?.startCapture(
                    context,
                    ScreenCaptureInfo.resultCode,
                    ScreenCaptureInfo.intentData!!
                ) {
                    ScreenCaptureUtil.getScreenImage(
                        context,
                        it,
                        view!!
                    )
                }
            } else {
                EggUtil.toast("您未开启截图权限哦")
            }
            println("已执行")
        }

        floatCancel.setOnClickListener {
            windowManager?.removeViewImmediate(view)
            ScreenCaptureInfo.isStart = false
        }

        view?.setOnTouchListener(FloatingOnTouchListener())
        windowManager?.addView(view, layPar)
        ScreenCaptureInfo.isStart = true
    }

    object Data {
        var floatWin: FloatWin? = null
    }

    private inner class FloatingOnTouchListener : View.OnTouchListener {
        private var startTime = 0L
        private var isClick = false
        private var x = 0
        private var y = 0

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isClick = false
                    x = event.rawX.toInt()
                    y = event.rawY.toInt()
                    startTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_MOVE -> {
                    isClick = true
                    val nowX = event.rawX.toInt()
                    val nowY = event.rawY.toInt()
                    val movedX = nowX - x
                    val movedY = nowY - y
                    x = nowX
                    y = nowY
                    layPar?.x = layPar?.x?.plus(movedX)
                    layPar?.y = layPar?.y?.plus(movedY)
                    windowManager!!.updateViewLayout(view, layPar)
                }
                MotionEvent.ACTION_UP -> {
                    isClick = (System.currentTimeMillis() - startTime) > 0.1 * 1000L
                }
                else -> {
                }
            }
            return isClick
        }
    }

/*
    private inner class FloatOnTouchLister : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            var mTouchStartX = 0.0
            var mTouchStartY = 0.0
            var x = 1F
            var y = 1F
            var initViewPlace = false
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    println("按下")
                    if (!initViewPlace) {
                        initViewPlace = true
                        //获取初始位置
                        mTouchStartX += (event.rawX - layPar?.x!!)
                        mTouchStartY += (event.rawY - layPar?.y!!)
                    } else {
                        //根据上次手指离开的位置与此次点击的位置进行初始位置微调
                        mTouchStartX += (event.rawX - x)
                        mTouchStartY += (event.rawY - y)
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    println("移动")
                    x = event.rawX
                    y = event.rawY
                    layPar?.x = (x - mTouchStartX).toInt()
                    layPar?.y = (y - mTouchStartY).toInt()
                    windowManager?.updateViewLayout(view, layPar)
                }

                MotionEvent.ACTION_UP -> {
                    println("停下")
                }
            }
            return true
        }
    }
*/
}


/* layPar.width = WindowManager.LayoutParams.WRAP_CONTENT
 layPar.height = WindowManager.LayoutParams.WRAP_CONTENT
 layPar.x = 200
 layPar.y = 200
 layPar.format = PixelFormat.RGBA_8888*/