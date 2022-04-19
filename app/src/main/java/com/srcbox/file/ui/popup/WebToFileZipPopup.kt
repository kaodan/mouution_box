package com.srcbox.file.ui.popup

import android.app.Activity
import android.content.Context
import android.widget.Button
import android.widget.TextView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.rengwuxian.materialedittext.MaterialEditText
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.WebToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import www.linwg.org.lib.LCardView
import java.io.File

class WebToFileZipPopup(context: Context) : BottomPopupView(context) {
    override fun onCreate() {
        super.onCreate()
        findViewById<Button>(R.id.to_yes_on).setOnClickListener {
            val con = findViewById<MaterialEditText>(R.id.link_edit).text.toString()
            if (con.isEmpty()) {
                return@setOnClickListener
            }

            if (findViewById<Button>(R.id.to_yes_on).text.toString() == "打包中")
                return@setOnClickListener

            GlobalScope.launch(Dispatchers.Main) {
                findViewById<Button>(R.id.to_yes_on).text = "打包中"
                EggUtil.hideKeyboard(context as Activity)
                val outPath = File(AppStorageData.getFileOutFile(), "网站源码打包")
                val webToFile = WebToFile(outPath, con)
                val isS = webToFile.getSrc()
                if (!isS) {
                    XPopup.Builder(context).asConfirm("提示", "失败", null)
                        .show()
                    findViewById<Button>(R.id.to_yes_on).text = "打包"
                    return@launch
                }
                XPopup.Builder(context).asConfirm("提示", "文件保存在：${outPath.absolutePath}", null)
                    .show()
                findViewById<Button>(R.id.to_yes_on).text = "打包"
            }
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.web_to_file_zip_popup
    }
}