package com.srcbox.file.ui.util

import android.app.WallpaperManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.wall_pager.*
import java.io.File

class WallPaper : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wall_pager)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT;
        }

        val d = WallpaperManager.getInstance(this).drawable
        wall.setImageDrawable(d)
        save_img.setOnClickListener {
            it as TextView
            it.text = "保存中"
            Thread {
                val f = File(AppStorageData.getFileOutFile(), "壁纸/wall.png")
                f.parentFile?.mkdirs()
                EggUtil.saveDrawable(d, f)
                runOnUiThread {
                    EggUtil.toast("已保存：${AppSetting.appFileOut}/壁纸")
                    it.text = "保存"
                }
            }.start()
        }
    }
}


//        GlobalMethods(this).initActivityUi()
/*  val rootRel = RelativeLayout(this)
  val btn = Button(this)
  btn.text = "保存"
  btn.setTextColor(Color.parseColor("#ffffff"))
  btn.setBackgroundColor(Color.parseColor("#66222222"))
  rootRel.layoutParams = RelativeLayout.LayoutParams(-1, -1)
  rootRel.layoutParams = RelativeLayout.LayoutParams(
      RelativeLayout.LayoutParams.MATCH_PARENT,
      RelativeLayout.LayoutParams.MATCH_PARENT
  )
  val layBtnRet = RelativeLayout.LayoutParams(
      ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          EggUtil.dp2px(this, 50f)
      )
  )

  layBtnRet.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
  val imageView = ImageView(this)
  imageView.layoutParams = RelativeLayout.LayoutParams(
      RelativeLayout.LayoutParams.MATCH_PARENT,
      RelativeLayout.LayoutParams.MATCH_PARENT
  )
  btn.layoutParams = layBtnRet
  rootRel.addView(btn)
  val wallpaperManager = WallpaperManager.getInstance(this)

  btn.setOnClickListener {
      btn.text = "保存中..."
      Thread {
          val path = "${AppSetting.fileStoragePath}壁纸.png"
          EggUtil.saveDrawable(
              wallpaperManager.drawable,
              File(path)
          )
          runOnUiThread {
              EggUtil.toast(this, "已保存在山盒目录下\n$path", true)
              btn.text = "保存"
          }
      }.start()
  }
  imageView.scaleType = ImageView.ScaleType.CENTER_CROP
  rootRel.addView(imageView)
  Thread {
      val bit = EggUtil.getBitmapFromDrawable(wallpaperManager.drawable)
      runOnUiThread {
          imageView.setImageURI(bit?.let {
              EggUtil.bitmap2uri(
                  this,
                  it
              )
          })
          bit?.recycle()
      }
  }.start()*/