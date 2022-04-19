package com.srcbox.file.application

import android.app.Application
import android.content.Context
import cat.ereza.customactivityoncrash.config.CaocConfig
import cn.leancloud.AVOSCloud
import com.hjq.bar.TitleBar
import com.hjq.bar.style.TitleBarLightStyle
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.logic.`object`.ServiceCreator
import com.srcbox.file.logic.network.UserService
import com.srcbox.file.ui.ErrorCrash
import com.srcbox.file.util.SpTool
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk


class EggApplication : Application() {
    companion object {
        lateinit var context: Context
        var isDebug = false
        var isError = true
        lateinit var userService:UserService
    }

    override fun onCreate() {
        super.onCreate()
        if (isDebug) {
            //慎重使用！！！ 这个使用后要注释
            /* AVOSCloud.initialize(
                 this,
                 "ofdfGpmix6GzKWGHAAcqVI1m-gzGzoHsz",
                 "yXLaw0DI59FDpFaWSFtlrg9m",
                 "https://api.aaaaa.ac.cn"
             )*/
        } else {
            AVOSCloud.initializeSecurely(
                this,
                "ofdfGpmix6GzKWGHAAcqVI1m-gzGzoHsz",
                "https://api.aaaaa.ac.cn"
            )
        }

        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        QbSdk.initTbsSettings(map)
        QbSdk.initX5Environment(this, null)

        TitleBar.initStyle(TitleBarLightStyle(this));
        context = applicationContext
        userService = ServiceCreator.create<UserService>()
        CaocConfig.Builder.create().errorActivity(ErrorCrash::class.java).apply()
        SpTool.setContext(applicationContext)
        val p = SpTool.getSettingString(
            "fileOutPath",
            "山盒2.0"
        )
        AppSetting.appFileOut = p
        AppSetting.colorStress = SpTool.getSettingString("themeColor", AppSetting.colorStress)

        AppSetting.colorTransTress =
            "#${SpTool.getSettingString("themeTransColor", "572626675").toInt()
                .toString(16)}"
    }
}