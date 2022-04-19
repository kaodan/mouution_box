package com.srcbox.file.ui.popup

import android.app.Activity
import android.content.Context
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.CenterPopupView
import com.qmuiteam.qmui.layout.QMUILayoutHelper
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.ResourceData
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.popup_select_down_or_show.view.*
import java.io.File
import kotlin.concurrent.thread

class SelectDownOrShowPopup(context: Context) : BottomPopupView(context) {
    var resourceData: ResourceData? = null
    override fun onCreate() {
        super.onCreate()

        downloadQmui.radius = QMUILayoutHelper.RADIUS_OF_HALF_VIEW_HEIGHT
        showQmui.radius = QMUILayoutHelper.RADIUS_OF_HALF_VIEW_HEIGHT

        downloadQmui.setOnClickListener {
            val outFile = File(
                AppStorageData.getFileOutFile(),
                "资源嗅探/${resourceData?.mediaType?.type}/${resourceData?.title}-${System.currentTimeMillis()}.${resourceData?.mediaType?.subtype}"
            )

            resourceData?.let {
                thread {
                    var isShow = false
                    EggUtil.downloadFile(it.url, false, outFile) { progress ->
                        (context as Activity).runOnUiThread {
                            if (progress == 100f) {
                                if (!isShow) {
                                    XPopup.Builder(context).asConfirm("提示", "文件已保存到：$outFile", null)
                                        .show()
                                    downloadQmuiText.text = "下载"
                                }
                                isShow = true
                            }
                            downloadQmuiText.text = progress.toString()
                        }
                    }
                }

            }
        }

        showQmui.setOnClickListener {
            resourceData?.let {
                EggUtil.goBrowser(context, it.url)
            }
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.popup_select_down_or_show
    }
}