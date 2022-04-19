package com.srcbox.file.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.egg.extractmanager.ExtractListener
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.race604.drawable.wave.WaveDrawable
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.UserAppData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.extractManager.code.ExtractManager
import com.srcbox.file.ui.extractManager.code.ExtractResourceFormApp
import com.srcbox.file.ui.extractManager.code.ExtractTaskMessage
import com.srcbox.file.ui.popup.AppMessagePopup
import com.srcbox.file.ui.popup.ExtractSrcPopup
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.app_fun.*
import kotlinx.android.synthetic.main.app_fun.title_bar
import kotlinx.android.synthetic.main.app_list.*
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.lang.StringBuilder
import java.security.MessageDigest
import java.util.zip.ZipFile
import kotlin.concurrent.thread


class AppFunActivity : AppCompatActivity() {
    private var gXp: BasePopupView? = null
    private val requestBody = 6
    private lateinit var packageNameV: String
    private val unInstallReceiver = UnInstallPackage(this)

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_fun)
        ImmersionBar.with(this).statusBarDarkFont(true, 0.2f).navigationBarColor("#ffffff")
            .navigationBarDarkIcon(true).transparentStatusBar().init()
        EggUtil.loadIcon(this, AppSetting.colorStress, title_bar.leftView)
        packageNameV = intent.getStringExtra("package")!!
        val packInfo = packageManager.getPackageInfo(packageNameV, 0)
        try {
            packageManager.getPackageInfo(packageNameV, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            EggUtil.toast("此应用不存在")
            return
        }
        val intentFilter = IntentFilter(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")

        val s = StringBuilder("s")
        s.append("a")
        s.toString()

        registerReceiver(unInstallReceiver, intentFilter)
        GlobUtil.changeTheme(
            theme_t1,
            open_app_text,
            share_app_text,
            un_app_text,
            extract_app_text,
            extract_app_icon_text,
            theme_t2
        )

        ExtractSrcPopup.userAppData = UserAppData(
            File(packInfo.applicationInfo.sourceDir),
            packInfo.applicationInfo.loadIcon(packageManager),
            packInfo.applicationInfo.loadLabel(packageManager).toString(),
            (File(packInfo.applicationInfo.sourceDir).length() / 1024).toInt(),
            packInfo.packageName,
            packInfo.firstInstallTime,
            EggUtil.isOsApp(packInfo.applicationInfo)

        )

        app_r_icon.setImageDrawable(ExtractSrcPopup.userAppData!!.appIcon)
        app_r_name.text = ExtractSrcPopup.userAppData!!.name


        res_pack.setOnClickListener {
            res_pack_text.text = "打包中，请耐心等待"
            thread {
                val rootCacheDir = "资源打包/${ExtractSrcPopup.userAppData!!.name}/cache"
                val rootDir = "资源打包/${ExtractSrcPopup.userAppData!!.name}"
                EggUtil.saveFile(
                    this,
                    ExtractSrcPopup.userAppData!!.appSource.inputStream(),
                    File(
                        AppStorageData.getFileOutFile(),
                        "$rootCacheDir/安装包/${ExtractSrcPopup.userAppData!!.name}.apk"
                    )
                )

                EggUtil.saveBitmapFile(
                    EggUtil.getBitmapFromDrawable(ExtractSrcPopup.userAppData!!.appIcon),
                    File(
                        AppStorageData.getFileOutFile(),
                        "$rootCacheDir/图标/${ExtractSrcPopup.userAppData!!.appPackageName}.png"
                    )
                )

                val newExtFile = File(AppStorageData.getFileOutFile(), "ext1.4.json")
                ExtractManager.outDir =
                    File(AppStorageData.getFileOutFile(), "$rootCacheDir/安装包资源")
                ExtractManager.ruleJson =
                    JSON.parseObject(EggIO.readFile(newExtFile))
                ExtractResourceFormApp(
                    ExtractTaskMessage(
                        ExtractSrcPopup.userAppData!!.appSource,
                        ExtractSrcPopup.userAppData!!.name
                    ),
                    object : ExtractListener {
                        override fun onProgress(float: Float) {
                            float.toString()
                        }

                        override fun onStart() {
                            "提取中"
                        }

                        override fun onPause() {
                            "完成"
                        }

                        override fun onCancel() {
                            "关闭"
                        }

                        @SuppressLint("SetTextI18n")
                        override fun onSuccess() {
                            ZipUtil.pack(
                                File(AppStorageData.getFileOutFile(), rootCacheDir),
                                File(AppStorageData.getFileOutFile(), "$rootDir/资源.zip")
                            )
                            EggUtil.deletes(File(AppStorageData.getFileOutFile(),rootCacheDir))
                            runOnUiThread {
                                res_pack_text.text = "资源打包"
                                XPopup.Builder(this@AppFunActivity).asConfirm(
                                    "提示",
                                    "已保存在${File(AppStorageData.getFileOutFile(), rootDir).absolutePath}",
                                    null
                                ).show()
                            }
                        }
                    }).start()
//                File(AppStorageData.getFileOutFile(), "$rootCacheDir/资源.zip").createNewFile()

            }
        }

        extract_app.setOnClickListener {
            extract_app_text.text = "提取中"
            Thread {
                val outFile = File(
                    AppStorageData.getFileOutFile(),
                    "单个提取/安装包/${ExtractSrcPopup.userAppData!!.name}/${ExtractSrcPopup.userAppData!!.name}.apk"
                )
                EggUtil.saveFile(
                    this,
                    ExtractSrcPopup.userAppData!!.appSource.inputStream(),
                    outFile
                )
                runOnUiThread {
                    XPopup.Builder(this).asConfirm("提示", "已保存在${outFile.absolutePath}", null).show()
                    extract_app_text.text = "提取安装包"
                }
            }.start()
        }

        extract_app_icon.setOnClickListener {
            val outFile = File(
                AppStorageData.getFileOutFile(),
                "单个提取/${ExtractSrcPopup.userAppData!!.name}/图标/${ExtractSrcPopup.userAppData!!.appPackageName}.png"
            )
            EggUtil.saveBitmapFile(
                EggUtil.getBitmapFromDrawable(ExtractSrcPopup.userAppData!!.appIcon),
                outFile
            )
            XPopup.Builder(this).asConfirm("提示", "已保存在${outFile.absolutePath}", null).show()

            /* EggUtil.saveDrawable(
                 ExtractSrcPopup.userAppData!!.appIcon,
                 File(
                     AppStorageData.getFileOutFile(),
                     "${ExtractSrcPopup.userAppData!!.name}/图标/${ExtractSrcPopup.userAppData!!.appPackageName}.png"
                 ), this, true
             )*/
//            EggUtil.toast("已保存至根目录：${AppSetting.appFileOut}")
        }

        app_fun_msg.setOnClickListener {
            XPopup.Builder(this).asCustom(AppMessagePopup(this)).show()
        }

        app_fun_get_app_src.setOnClickListener {
            gXp = XPopup.Builder(this).asCustom(ExtractSrcPopup(this)).show()
        }

        title_bar.setOnTitleBarListener(
            object : OnTitleBarListener {
                override fun onLeftClick(v: View?) {
                    finish()
                }

                override fun onRightClick(v: View?) {}

                override fun onTitleClick(v: View?) {}
            }
        )

        share_app.setOnClickListener {
            share_app_text.text = "分享中"
            Thread {
                EggUtil.shareFile(
                    this,
                    ExtractSrcPopup.userAppData!!.appSource,
                    ExtractSrcPopup.userAppData!!.name
                )

                runOnUiThread {
                    share_app_text.text = "分享APP"
                }
            }.start()

        }

        open_app.setOnClickListener {
            ExtractSrcPopup.userAppData!!.appPackageName?.let { it1 -> EggUtil.startApp(this, it1) }
        }

        un_app.setOnClickListener {

            ExtractSrcPopup.userAppData!!.appPackageName?.let { it1 ->
                EggUtil.uninstallNormal(this, it1)
            }
        }

        val wave = WaveDrawable(this, R.drawable.wave)
        prog_ress.setImageDrawable(wave)
        wave.level = 5000
        wave.isIndeterminate = false


//        app_fun_type_recycler.layoutManager = LinearLayoutManager(this)
//        app_fun_type_recycler.adapter = AppFunSelect(arrayList)
        val newExtFile = File(AppStorageData.getFileOutFile(), "ext1.4.json")
        if (!newExtFile.exists()) {
            newExtFile.parentFile?.mkdirs()
            newExtFile.createNewFile()
            EggIO.copyFileTo(
                assets.open("json/extTable/ext1.4.json"),
                newExtFile.outputStream()
            )
        }

        val extTab = JSON.parseObject(EggIO.readFile(newExtFile))
        extTab.forEach { it ->
            val viewTypeFile =
                LayoutInflater.from(this)
                    .inflate(R.layout.view_item_file_type, typeContainer, false)
            val typeFileSwitch = viewTypeFile.findViewById<SwitchCompat>(R.id.type_file_switch)
            viewTypeFile.findViewById<TextView>(R.id.type_file_text).text = it.key
            DrawableCompat.setTintList(
                typeFileSwitch.thumbDrawable, ColorStateList(
                    arrayOf(
                        intArrayOf(), intArrayOf()
                    ),
                    intArrayOf(
                        Color.parseColor(AppSetting.colorStress),
                        Color.parseColor(AppSetting.colorTransTress)
                    )
                )
            )

            DrawableCompat.setTintList(
                typeFileSwitch.trackDrawable, ColorStateList(
                    arrayOf(
                        intArrayOf(), intArrayOf()
                    ),
                    intArrayOf(
                        Color.parseColor(AppSetting.colorTransTress),
                        Color.parseColor(AppSetting.colorTransTress)
                    )
                )
            )

            typeFileSwitch.isChecked = (it.value as JSONObject).getBoolean("on")
            typeContainer.addView(viewTypeFile)
            typeFileSwitch.setOnClickListener { switch ->
                switch as SwitchCompat
                extTab.getJSONObject(it.key)["on"] = switch.isChecked
                EggIO.writeFile(newExtFile, extTab.toJSONString())
//                switch.isChecked = !switch.isChecked
            }
        }
    }


    override fun onStop() {
        super.onStop()
        gXp?.let {
            gXp?.dismiss()
        }
    }

    override fun onDestroy() {
        unregisterReceiver(unInstallReceiver)
        super.onDestroy()
    }


    inner class UnInstallPackage(val activity: Activity) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            println(intent)
            if (intent!!.action == Intent.ACTION_PACKAGE_REMOVED) {
                activity.finish()
            }
        }
    }
}


//        wave.isIndeterminate = true
//        GlobUtil.changeTitle(this, false)
//        EggUtil.transStatusBar(this)
/*
        val appFunTitleBarLayoutParams =
            app_fun_title_bar.layoutParams as CollapsingToolbarLayout.LayoutParams
        appFunTitleBarLayoutParams.topMargin = EggUtil.getStatusBarHeight(this)
*/