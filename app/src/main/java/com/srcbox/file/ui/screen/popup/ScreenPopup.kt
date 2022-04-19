package com.srcbox.file.ui.screen.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.srcbox.file.ui.screen.ScreenAccessibility
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.qmuiteam.qmui.layout.QMUILayoutHelper
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.ui.screen.Screenshot
import com.srcbox.file.ui.screen.data.ScreenNode
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.Member
import com.srcbox.file.view.StokeRect
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.popup_screen_show.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import kotlin.system.exitProcess

class ScreenPopup(context: Context) : BottomPopupView(context) {
    private var screenBitmap: Bitmap? = null
    private var screenNodes: ArrayList<ScreenNode> = ArrayList()
    private var currentScreenNodeIndex = 0
    private var overAllHex = ""
    private val xOffset = QMUIDisplayHelper.dp2px(context, 80) / 2
    private val yOffset = QMUIDisplayHelper.dp2px(context, 80) / 2
    private var currentX = 0
    private var currentY = 0
/*    private var currentScreenX = 0
    private var currentScreenY = 0*/

    fun setBitmap(bitmap: Bitmap) {
        screenBitmap = bitmap
    }

    override fun getImplLayoutId(): Int {
        return R.layout.popup_screen_show
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()


        when (Screenshot.type) {
            Screenshot.TYPE_SCREEN -> {
                screenSelectImgBar.visibility = View.VISIBLE
                floatColorSelectImgBar.visibility = View.GONE
                blowUp.visibility = View.GONE
            }

            Screenshot.TYPE_FLOAT_COLOR -> {
                screenSelectImgBar.visibility = View.GONE
                floatColorSelectImgBar.visibility = View.VISIBLE
                blowUp.visibility = View.VISIBLE
            }
        }

        ScreenAccessibility.mRootInActiveWindow?.let {
            foreachNode(it)
            seekBar.min = 0f
            seekBar.max = screenNodes.size.toFloat() - 1
            if (seekBar.max == 0f) {
                return@let
            }

            ScreenAccessibility.isWork = true

            seekBar.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {
                    seekParams?.let {
                        currentScreenNodeIndex = it.progress
                        stokeRoot.removeAllViews()
                        val screenNode = screenNodes[seekParams.progress]
                        val rect = Rect()
                        screenNode.node?.getBoundsInScreen(rect)
                        val stokeRect = StokeRect(
                            context,
                            rect.left.toFloat(),
                            rect.top.toFloat(),
                            rect.right.toFloat(),
                            rect.bottom.toFloat()
                        )
                        stokeRect.setStokeColor("#652196f3")
                        stokeRect.setStokeWidth(15f)
                        stokeRoot.addView(stokeRect)
                        screenNode.view = stokeRect
                    }
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {

                }

            }
        }

        bg.setImageBitmap(screenBitmap)
        blowUp.radius = QMUILayoutHelper.RADIUS_OF_HALF_VIEW_HEIGHT
        copyColor.radius = QMUILayoutHelper.RADIUS_OF_HALF_VIEW_HEIGHT
        cancelFloatContainer.radius = QMUILayoutHelper.RADIUS_OF_HALF_VIEW_HEIGHT
        cancelContainer.radius = QMUILayoutHelper.RADIUS_OF_HALF_VIEW_HEIGHT
        EggUtil.loadIcon(context, "#ffffff", blowUpIcon)

        screenBitmap?.let {
            xSelectSeekBar.max = it.width.toFloat()
            ySelectSeekBar.max = it.height.toFloat()
        }

        bg.setOnTouchListener { v, event ->
            currentX = event.x.toInt()
            currentY = event.y.toInt()
            onXyChange()
            if (Screenshot.type == Screenshot.TYPE_SCREEN) {
                return@setOnTouchListener false
            }

            true
        }


        xSelectSeekBar.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
                seekParams?.let {
                    blowUp.x = it.progress.toFloat()
                    currentX = blowUp.x.toInt()
                    onXyChange()
                }
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {}
            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {}
        }


        ySelectSeekBar.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
                seekParams?.let {
                    blowUp.y = it.progress.toFloat()
                    currentY = blowUp.y.toInt()
                    onXyChange()
                }
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {}
            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {}
        }

        copyColor.setOnClickListener {
            EggUtil.copyText(context as Activity, "0x$overAllHex")
            XPopup.Builder(context)
                .enableShowWhenAppBackground(true)
                .asConfirm("提示", "已复制 0x$overAllHex 十六进制颜色", null)
                .show()
        }

        saveCutImg.setOnClickListener{
            GlobalScope.launch(Dispatchers.Main) {
                val asLoading = XPopup.Builder(context).asLoading()
                asLoading.show()
                val memberDate = Member.MemberData("0")
                when (Member.isVip(memberDate)) {
                    Member.UserType.TIME_LIMIT -> {
                        println("Current time ${memberDate.dateTime}")
    //                        saveHtml()
                        saveCutImg()
                    }

                    Member.UserType.ORDINARY -> {
                        XPopup.Builder(context)
                            .enableShowWhenAppBackground(true)
                            .asConfirm("提示", "充值VIP后才可以保存图片哦", null)
                            .show()
                       /* (context as Activity).startActivity(
                            Intent(
                                context,
                                GetMemberActivity::class.java
                            )
                        )*/
                    }

                    Member.UserType.ALWAYS -> {
    //                        saveHtml()
                        saveCutImg()
                    }

                    Member.UserType.NO_LOG -> {
                        XPopup.Builder(context)
                            .enableShowWhenAppBackground(true)
                            .asConfirm("提示", "登录账号后并充值VIP才可以保存图片哦", null)
                            .show()
                        asLoading.delayDismiss(500)
                        /*(context as Activity).startActivity(
                            Intent(
                                context,
                                LoginActivity::class.java
                            )
                        )*/
                        return@launch
                    }

                    Member.UserType.ERROR -> {
                        EggUtil.toast("出现错误，请重试")
                    }

                    Member.UserType.NOTHING_CONNECT -> {
                        EggUtil.toast("网络无连接")
                    }
                }
                asLoading.delayDismiss(500)
            }

        }


        cancelContainer.setOnClickListener {
            dismiss()
        }

        cancelFloatContainer.setOnClickListener {
//            val isStop = context.stopService(Intent(context, ScreenServices::class.java))
//            ScreenServices.screenServices?.onDestroy()
            exitProcess(0)
        }
        /*blowUp.setOnClickListener {
            EggUtil.copyText(context as Activity, overAllHex)
            XPopup.Builder(context)
                .enableShowWhenAppBackground(true)
                .asConfirm("提示", "已复制 $overAllHex 十六进制颜色", null)
                .show()
        }*/
    }


    fun saveCutImg(){
        try {
            val bit = cutBitmap(screenBitmap!!, getCurrentScreenNodeRect())
            val outFile =
                File(AppStorageData.getFileOutFile(), "可视化采集/${System.currentTimeMillis()}.png")
            EggUtil.saveBitmapFile(bit, outFile)
            context?.let {
                XPopup.Builder(it)
                    .enableShowWhenAppBackground(true)
                    .asConfirm("提示", "文件已保存在$outFile", null)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "原图失效，请重试", Toast.LENGTH_SHORT).show()
        }
    }

    /*private inner class ScreenOnTouchListener : View.OnTouchListener {
        private var x = 0
        private var y = 0

        override fun onTouch(view: View?, event: MotionEvent): Boolean {
            blowUp.x = event.rawX
            blowUp.y = event.rawY
            *//*when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.rawX.toInt()
                    y = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {

                    val nowX = event.rawX.toInt()
                    val nowY = event.rawY.toInt()
                    val movedX = nowX - x
                    val movedY = nowY - y
                    x = nowX
                    y = nowY
                    blowUp.x = 100f
                    blowUp.y = 100f
                }

                MotionEvent.ACTION_UP -> {
                }
                else -> {
                }
            }*//*
            return true
        }
    }*/

    fun onXyChange() {
        /*val eveX = (currentX - xOffset / 2).toInt()
        val eveY = (currentY - yOffset).toInt()*/

        val eveX = currentX + xOffset
        val eveY = (currentY + yOffset).toInt()
        if (eveY < 0 || eveX < 0) {
            return
        }

        val clr = try {
            screenBitmap!!.getPixel(
                eveX,
                eveY
            )
        } catch (e: Exception) {
            0
        }

        blowUpHelp.x = eveX.toFloat()
        blowUpHelp.y = eveY.toFloat()

        val red = clr and 0x00ff0000 shr 16 // 取高两位
        val green = clr and 0x0000ff00 shr 8 // 取中两位
        val blue = clr and 0x000000ff // 取低两位


        val hex =
            String.format("%02X%02X%02X", red, green, blue)
        overAllHex = hex
        copyColor.setBackgroundColor(Color.parseColor("#50$hex"))
        /*blowUp.x = ((currentX - (xOffset.toFloat() * 1.5)).toFloat())
        blowUp.y = ((currentY - (yOffset.toFloat() * 1.2) + 8).toFloat())*/
        blowUp.x = currentX.toFloat()
        blowUp.y = currentY.toFloat()
    }

    private fun cutBitmap(bit: Bitmap, rect: Rect): Bitmap? {
        return try {
            Bitmap.createBitmap(bit, rect.left, rect.top, rect.width(), rect.height())
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    private fun getCurrentScreenNodeRect(): Rect {
        val rect = Rect()
        screenNodes[currentScreenNodeIndex].node?.getBoundsInScreen(rect)
        return rect
    }

    private fun foreachNode(node: AccessibilityNodeInfo) {
        val rect = Rect()
        node.getBoundsInScreen(rect)
        if (!(rect.width() == 0 || rect.height() == 0 || rect.height() < 10 || rect.width() < 10)) {
            screenNodes.add(ScreenNode(node, null))
        }

        for (i in 0 until node.childCount) {
            try {
                foreachNode(node.getChild(i))
            } catch (e: Exception) {
            }
        }
    }

    override fun onDismiss() {
        super.onDismiss()
        ScreenAccessibility.isWork = false
    }
}
