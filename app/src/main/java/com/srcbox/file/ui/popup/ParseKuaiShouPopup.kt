package com.srcbox.file.ui.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.qmuiteam.qmui.layout.QMUILinearLayout
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.rengwuxian.materialedittext.MaterialEditText
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.ParseDouyinWateMark
import com.srcbox.file.util.ParseKuaiSou
import kotlinx.android.synthetic.main.parse_dou_yin_popup.view.*
import java.io.File
import java.lang.Exception
import kotlin.concurrent.thread

class ParseKuaiShouPopup(context: Context) : CenterPopupView(context) {
    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        super.onCreate()
        var isF = false
        val parseKuaiShouBtn = findViewById<LinearLayout>(R.id.parseKuaiShouBtn)
        val cancelKuaiShouBtn = findViewById<LinearLayout>(R.id.cancelKuaiShouBtn)
        val parseKuaiShouBtnText = findViewById<TextView>(R.id.parseKuaiShouBtnText)
        val kuaiShouEdit = findViewById<EditText>(R.id.kuai_shou_edit)
        val kuaiShouEditContainer = findViewById<QMUILinearLayout>(R.id.kuai_shou_edit_container)
        val kuaiShouVideoLink = findViewById<TextView>(R.id.kuai_shou_video_link)
        kuaiShouEditContainer.radius = QMUIDisplayHelper.dp2px(context, 8)
        pasteText.setOnClickListener {
            kuaiShouEdit.setText(EggUtil.trimClipChineseValue(context))
        }

        cancelKuaiShouBtn.setOnClickListener {
            dismiss()
        }
        parseKuaiShouBtn.setOnClickListener {
            val kuaiShouLink = kuaiShouEdit.text.toString()
            if (kuaiShouLink.isEmpty()) {
                EggUtil.toast("不能为空")
                return@setOnClickListener
            }
            parseKuaiShouBtnText.text = "解析中"
            thread {

                try {
                    val outF = File(AppStorageData.getFileOutFile(), "网络解析/快手视频解析/")
                    ParseKuaiSou().getVideo(
                        EggUtil.filterSpecialStr(kuaiShouLink),
                        outF,
                        { fl: Float ->
                            (context as Activity).runOnUiThread {
                                parseKuaiShouBtnText.text = "${fl}%"
                                if (fl == 100f) {
                                    if (!isF) {
                                        XPopup.Builder(context)
                                            .asConfirm("提示", "文件保存在：${outF.absolutePath}", null)
                                            .show()
                                    }

                                    parseKuaiShouBtnText.text = "确定"
                                    isF = true
                                }
                            }
                        }, {
                            (context as Activity).runOnUiThread {
                                kuaiShouVideoLink.visibility = View.VISIBLE
                                kuaiShouVideoLink.text = "直链是：$it"
                            }
                        })
                } catch (e: Exception) {
                    (context as Activity).runOnUiThread {
                        e.printStackTrace()
                        EggUtil.toast("失败，请重试${e.message}")
                        parseKuaiShouBtnText.text = "确定"
                    }
                }

            }
        }
    }


    override fun getImplLayoutId(): Int {
        return R.layout.parse_kuai_shou_popup
    }
}