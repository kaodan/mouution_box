package com.srcbox.file.ui.popup

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.LinearLayout
import cn.leancloud.codec.MD5
import com.lxj.xpopup.core.CenterPopupView
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.util.EggUtil
import java.io.File

class ShowIconPopup(context: Context) : CenterPopupView(context) {
    override fun onCreate() {
        super.onCreate()
        findViewById<LinearLayout>(R.id.save_show_icon).setOnClickListener {
            val b = EggUtil.getBitmapFromDrawable(findViewById<ImageView>(R.id.show_icon).drawable)
            val f = File(
                AppStorageData.getFileOutFile(),
                "可视化抓捕/${MD5.computeMD5(((555..1000).random()).toString())}.png"
            )
            EggUtil.saveBitmapFile(
                b,
                f
            )
            EggUtil.toast("已保存至：$f")
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.show_icon_popup
    }

    /*fun setImg(bitmap: Bitmap) {
        findViewById<ImageView>(R.id.show_icon).setImageBitmap(bitmap)
    }*/
}