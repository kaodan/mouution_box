package com.srcbox.file.ui

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.srcbox.file.R
import com.srcbox.file.application.EggApplication
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.util.EggUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.error_activity.*
import java.io.File

class ErrorCrash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val log = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, intent)
        val crash = "Android Sdk ${Build.VERSION.SDK_INT}\n$log"

        if (EggApplication.isError) {
            setContentView(R.layout.error_activity)
            EggUtil.alterStateBarColor(this, "#333333")
            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = 25f
            gradientDrawable.setStroke(1, Color.parseColor("#333333"))
            gradientDrawable.setColor(Color.parseColor("#333333"))
            error_copy_btn.background = gradientDrawable

            error_copy_btn.setOnClickListener {
                val alertDialog = AlertDialog.Builder(this).setTitle("Error Log").setMessage(crash)
                    .setNegativeButton("复制") { _: DialogInterface, _: Int ->
                        EggUtil.copyText(this, crash)
                        EggUtil.toast("已复制")
                    }
                alertDialog.setNeutralButton("联系开发者") { _: DialogInterface, _: Int ->
                    EggUtil.copyText(this, log)
                    EggUtil.toast("已复制LOG")
                    EggUtil.goQQ(this, "1970284668")
                }
                alertDialog.show()
            }

            errorJiaQun.setOnClickListener {
                EggUtil.joinQQGroup(this, AppSetting.QUN_KEY)
            }

            error_repair_btn.setOnClickListener {
                EggUtil.deletes(File("$filesDir"))
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        val eggAVObject = AVObject("Crash")
        eggAVObject.put("sdk_version", "${Build.VERSION.SDK_INT}")
        eggAVObject.put("log", log)
        try {
            eggAVObject.put(
                "app_version",
                packageManager.getPackageInfo(packageName, 0).versionName
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.message
        }
        if (AVUser.currentUser() == null) {
            eggAVObject.put("email", "-2")
        } else {
            eggAVObject.put("email", AVUser.currentUser().email)
        }
        eggAVObject.saveInBackground().subscribe(object : Observer<AVObject> {
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(t: AVObject) {
                if (!EggApplication.isError){
                    startActivity(Intent(this@ErrorCrash, MainActivity::class.java))
                    finish()
                }
            }

            override fun onError(e: Throwable) {}
        })
    }
}