package com.srcbox.file.ui.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.lxj.xpopup.XPopup
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.activity_lock_screen.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class LockScreenActivity : AppCompatActivity() {
    @SuppressLint("WrongThread", "WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)
        GlobUtil.changeTitle(this)

        val wallManagerInstance = WallpaperManager.getInstance(this)
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                var wallPagerFile = wallManagerInstance.getWallpaperFile(2)
                if (wallPagerFile == null) {
                    wallPagerFile = wallManagerInstance.getWallpaperFile(1)
                }
                val bitmap = BitmapFactory.decodeFileDescriptor(wallPagerFile.fileDescriptor)
                wallPagerFile.close()
                lockScreenImage.setImageBitmap(bitmap)

                lockScreen.setOnClickListener {
                    val outFile = File("${AppStorageData.getFileOutFile()}", "锁屏.jpeg")
                    outFile.parentFile?.mkdirs()
                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        100,
                        BufferedOutputStream(FileOutputStream(outFile))
                    )
                    XPopup.Builder(this).asConfirm("提示", "文件保存在：$outFile", null).show()
                }
            } catch (e: Exception) {
                EggUtil.toast("对不起，您的手机不支持")
            }

        } else {
            EggUtil.toast("对不起，您的手机不支持")
        }
    }
}