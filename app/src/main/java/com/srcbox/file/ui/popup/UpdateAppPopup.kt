package com.srcbox.file.ui.popup

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.TextView
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.lxj.xpopup.core.BottomPopupView
import com.srcbox.file.R
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.update_app_popup.view.*
import java.text.DecimalFormat

class UpdateAppPopup(context: Context) : BottomPopupView(context) {
    private var versionName = ""
    private var appLink = ""
    override fun onCreate() {
        super.onCreate()
        val updateBtnText = findViewById<TextView>(R.id.update_btn_text)
        hint_ver_update_btn.setOnClickListener {
            updateBtnText.text = "准备中"
            FileDownloader.setup(context)
            val fileName = "updateApp${versionName}.apk"
            val downPath = "${context.filesDir}/apk/$fileName"
            FileDownloader.getImpl().create(appLink).setPath(downPath)
                .setListener(object : FileDownloadListener() {
                    override fun warn(task: BaseDownloadTask?) {}

                    override fun completed(task: BaseDownloadTask?) {
                        //已完成
                        updateBtnText.text = "点击安装"
                        if (Build.VERSION.SDK_INT >= 26) {
                            if (!context.packageManager.canRequestPackageInstalls()) {
                                EggUtil.toast("请授权")
                                EggUtil.startInstallPermissionSettingActivity(context)
                                return
                            }
                        }
                        EggUtil.openAndroidFile(context, downPath)
                    }

                    override fun pending(
                        task: BaseDownloadTask?,
                        soFarBytes: Int,
                        totalBytes: Int
                    ) {
                        //等待中
                        updateBtnText.text = "等待中"
                    }

                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        EggUtil.toast("出现错误：${e!!.message}")
                    }

                    @SuppressLint("SetTextI18n")
                    override fun progress(
                        task: BaseDownloadTask?,
                        soFarBytes: Int,
                        totalBytes: Int
                    ) {
                        val pro = soFarBytes.toFloat() / totalBytes.toFloat()
                        updateBtnText.text = DecimalFormat(".00").format((pro * 100.0)) + "%"
                    }

                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun connected(
                        task: BaseDownloadTask?,
                        etag: String?,
                        isContinue: Boolean,
                        soFarBytes: Int,
                        totalBytes: Int
                    ) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes)
                        //启动中
                        updateBtnText.text = "启动中"
                    }
                }).start()
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.update_app_popup
    }

    @SuppressLint("SetTextI18n")
    fun setContent(version: String, content: String, newLink: String) {
        versionName = version
        appLink = newLink
        findViewById<TextView>(R.id.ver_c).text =
            "${EggUtil.getAppVersionName(context)} -> $version"
        findViewById<TextView>(R.id.hint_ver_update_content).text =
            content
    }
}