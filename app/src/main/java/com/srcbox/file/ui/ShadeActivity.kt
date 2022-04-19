package com.srcbox.file.ui

import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.XPopupCallback
import com.srcbox.file.R
import com.srcbox.file.data.`object`.CapData
import com.srcbox.file.ui.popup.ShowIconPopup
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import com.srcbox.file.view.StokeRect
import kotlinx.android.synthetic.main.shade_activity.*

class ShadeActivity : AppCompatActivity() {
    private var showIconPopup: ShowIconPopup? = null
    private var thisRect = -2
    private val views = ArrayList<StokeRect>()
    private var showXPopup: XPopup.Builder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shade_activity)
//        EggUtil.alterStateBarColor(this, "#222222")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
        GlobUtil.changeTitle(this, false)
        showIconPopup = ShowIconPopup(this)
        bg.setImageBitmap(CapData.bitmap)
        if (CapData.arrayRectList.size == 0) {
            EggUtil.toast("没获取到数据")
            finish()
        }
        var i = 0
        CapData.arrayRectList.forEach {
            val rect = Rect()
            it.getBoundsInScreen(rect)
            val stokeRect = StokeRect(
                this,
                rect.left.toFloat(),
                rect.top.toFloat(),
                rect.right.toFloat(),
                rect.bottom.toFloat()
            )
            views.add(stokeRect)
            root.addView(stokeRect)
//            stokeRect.setStokeColor("#000000")
            stokeRect.tag = i
            println(i)
            stokeRect.setOnClickListener { itv ->
                println(itv)
                getIcon(itv.tag.toString().toInt())

            }
            i++
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (thisRect == -2) {
                    thisRect = 0
                } else {
                    thisRect--
                }
                if (thisRect < 0) {
                    EggUtil.toast("已经到低了哦")
                    thisRect++
                    return true
                }
                println("当前位置：$thisRect")
                showIcon(thisRect)
                return true
            }

            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (thisRect == -2) {
                    thisRect = 1
                } else {
                    thisRect++
                }
                if (thisRect > CapData.arrayRectList.size - 1) {
                    EggUtil.toast("已经到顶了哦")
                    thisRect--
                    return true
                }
                println("当前位置：$thisRect")
                showIcon(thisRect)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showIcon(thisI: Int) {

        val rect = Rect()
        CapData.arrayRectList[thisI].getBoundsInScreen(rect)
        val bit = EggUtil.cutBitmap(
            CapData.bitmap,
            rect.left,
            rect.top,
            rect.width(),
            rect.height()
        )
        if (showXPopup == null) {
            showXPopup = XPopup.Builder(this)
            showXPopup?.setPopupCallback(object : XPopupCallback {
                override fun onBackPressed(): Boolean {
                    return false
                }

                override fun onDismiss() {}

                override fun beforeShow() {}

                override fun onCreated() {
                    showIconPopup!!.rootView.findViewById<ImageView>(R.id.show_icon)!!
                        .setImageBitmap(bit)
                }

                override fun onShow() {}

            })?.asCustom(showIconPopup)?.show()
        } else {
            if (!showIconPopup!!.isShow) {
                showIconPopup?.show()
            }
            println(bit)
            showIconPopup!!.rootView.findViewById<ImageView>(R.id.show_icon)!!.setImageBitmap(bit)
//            showIconPopup?.setImg(bit!!)
        }
    }

    private fun getIcon(i: Int) {
        println("数字：$i")
    }
}


/* val hashMap = HashMap<String, String>()
                            hashMap["x"] = rect.left.toString()
                            hashMap["y"] = rect.top.toString()
                            hashMap["w"] = rect.width().toString()
                            hashMap["h"] = rect.height().toString()*/
/*val hashMap = HashMap<String, String>()
           hashMap["x"] = rect.left.toString()
           hashMap["y"] = rect.top.toString()
           hashMap["w"] = rect.width().toString()
           hashMap["h"] = rect.height().toString()
           val fStr = ScreenCaptureUtil.saveIconFile(hashMap, CapData.bitmap!!)
           EggUtil.toast("已保存至：$fStr")*/