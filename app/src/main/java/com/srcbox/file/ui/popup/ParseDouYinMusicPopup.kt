package com.srcbox.file.ui.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.rengwuxian.materialedittext.MaterialEditText
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.ParseDouyinMusic
import com.srcbox.file.util.ParseDouyinWateMark
import kotlinx.android.synthetic.main.parse_dou_yin_popup.view.*
import java.io.File
import java.lang.Exception
import kotlin.concurrent.thread

class ParseDouYinMusicPopup(context: Context) : CenterPopupView(context) {
    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        super.onCreate()
        val parseDouYinMusicBtn = findViewById<LinearLayout>(R.id.parseDouYinMusicBtn)
        val cancelDouYinMusicBtn = findViewById<LinearLayout>(R.id.cancelDouYinMusicBtn)
        val parseDouYinMusicBtnText = findViewById<TextView>(R.id.parseDouYinMusicBtnText)
        val douYinMusicEdit = findViewById<MaterialEditText>(R.id.dou_yin_music_edit)
        val douYinMusicLink = findViewById<TextView>(R.id.dou_yin_music_link)

        cancelDouYinMusicBtn.setOnClickListener {
            dismiss()
        }

        parseDouYinMusicBtn.setOnClickListener {
            val douYinLink = douYinMusicEdit.text.toString()
            if (douYinLink.isEmpty()) {
                EggUtil.toast("不能为空")
                return@setOnClickListener
            }

            parseDouYinMusicBtnText.text = "解析中"

            thread {
                try {
                    val outF = File(AppStorageData.getFileOutFile(), "网络解析/抖音音乐解析/")
                    ParseDouyinMusic().getNoneWatermark(
                        douYinLink,
                        outF,
                        { fl: Float ->
                            (context as Activity).runOnUiThread {
                                parseDouYinMusicBtnText.text = "${fl}%"
                                if (fl == 100f) {
                                    XPopup.Builder(context)
                                        .asConfirm("提示", "文件保存在：${outF.absolutePath}", null)
                                        .show()
                                    parseDouYinMusicBtnText.text = "确定"
                                }
                            }
                        }, {
                            (context as Activity).runOnUiThread {
                                douYinMusicLink.visibility = View.VISIBLE
                                douYinMusicLink.text = "直链是：$it"
                            }
                        })
                } catch (e: Exception) {
                    (context as Activity).runOnUiThread {
                        e.printStackTrace()
                        EggUtil.toast("失败，请重试${e.message}")
                        parseDouYinMusicBtnText.text = "确定"
                    }
                }

            }
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.parse_dou_yin_music_popup
    }
}