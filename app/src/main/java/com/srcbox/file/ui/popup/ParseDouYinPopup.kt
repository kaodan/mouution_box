package com.srcbox.file.ui.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.qmuiteam.qmui.layout.QMUILayoutHelper
import com.qmuiteam.qmui.layout.QMUILinearLayout
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.rengwuxian.materialedittext.MaterialEditText
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.ParseDouyinWateMark
import kotlinx.android.synthetic.main.parse_dou_yin_popup.view.*
import java.io.File
import java.lang.Exception
import java.util.regex.Pattern
import kotlin.concurrent.thread

class ParseDouYinPopup(context: Context) : CenterPopupView(context) {
    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        super.onCreate()
        val parseDouYinBtn = findViewById<LinearLayout>(R.id.parseDouYinBtn)
        val cancelDouYinBtn = findViewById<LinearLayout>(R.id.cancelDouYinBtn)
        val parseDouYinBtnText = findViewById<TextView>(R.id.parseDouYinBtnText)
        val douYinEdit = findViewById<EditText>(R.id.dou_yin_edit)
        val douYinEditContainer = findViewById<QMUILinearLayout>(R.id.dou_yin_edit_container)
        val douYinVideoLink = findViewById<TextView>(R.id.dou_yin_video_link)
        douYinEditContainer.radius = QMUIDisplayHelper.dp2px(context, 8)

        cancelDouYinBtn.setOnClickListener {
            dismiss()
        }

        pasteText.setOnClickListener {
            douYinEdit.setText(EggUtil.trimClipChineseValue(context))
        }


        parseDouYinBtn.setOnClickListener {
            val douYinLink = douYinEdit.text.toString()
            if (douYinLink.isEmpty()) {
                EggUtil.toast("不能为空")
                return@setOnClickListener
            }
            parseDouYinBtnText.text = "解析中"
            thread {
                try {
                    val outF = File(AppStorageData.getFileOutFile(), "网络解析/抖音视频解析/")
                    ParseDouyinWateMark().getNoneWatermark(
                        EggUtil.filterSpecialStr(douYinLink),
                        outF,
                        { fl: Float ->
                            (context as Activity).runOnUiThread {
                                parseDouYinBtnText.text = "${fl}%"
                                if (fl == 100f) {
                                    XPopup.Builder(context)
                                        .asConfirm("提示", "文件保存在：${outF.absolutePath}", null)
                                        .show()
                                    parseDouYinBtnText.text = "确定"
                                }
                            }
                        }, {
                            (context as Activity).runOnUiThread {
                                douYinVideoLink.visibility = View.VISIBLE
                                douYinVideoLink.text = "直链是：$it"
                            }
                        })
                } catch (e: Exception) {
                    (context as Activity).runOnUiThread {
                        e.printStackTrace()
                        EggUtil.toast("失败，请重试${e.message}")
                        parseDouYinBtnText.text = "确定"
                    }
                }

            }
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.parse_dou_yin_popup
    }
}