package com.srcbox.file.ui.popup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.util.EggUtil
import java.io.File
import kotlin.concurrent.thread


class CompressImagePopup(context: Context) : CenterPopupView(context) {
    companion object {
        var file: File? = null
        var compressTypeInt = 0
        const val COMPRESS_STORAGE_TYPE = 0
        const val COMPRESS_MEMORY_TYPE = 1
    }

    override fun onCreate() {
        super.onCreate()
        val selectImage = findViewById<TextView>(R.id.select_image)
        val compressType = findViewById<TextView>(R.id.compress_type)
//        val compressStorageText = findViewById<TextView>(R.id.compress_storage_text)
//        val compressMemoryText = findViewById<TextView>(R.id.compress_memory_text)
        val compressText = findViewById<TextView>(R.id.compress_text)
        val compressBtn = findViewById<CardView>(R.id.compress_btn)
        val compressBtnText = findViewById<TextView>(R.id.compress_btn_text)
        compressType.setOnClickListener {
            XPopup.Builder(context).atView(it)
                .asAttachList(arrayOf("压缩存储", "压缩内存"), null) { i: Int, s: String ->
                    it as TextView
                    when (i) {
                        0 -> {
                            it.text = "压缩存储"
                            compressTypeInt = COMPRESS_STORAGE_TYPE
                        }

                        1 -> {
                            it.text = "压缩内存"
                            compressTypeInt = COMPRESS_MEMORY_TYPE
                        }
                    }
                }.show()
        }

        selectImage.setOnClickListener {
            (context as Activity).startActivityForResult(
                Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ), 0x110
            )
        }

        compressBtn.setOnClickListener {
            if (file == null) {
                EggUtil.toast("请选择一张图片")
                return@setOnClickListener
            }
            compressBtnText.text = "压缩中"
            thread {
                when (compressTypeInt) {
                    COMPRESS_MEMORY_TYPE -> {
                        val beforeBitmap = BitmapFactory.decodeFile(file!!.absolutePath)
                        val afterBitmap = EggUtil.compressBySampleSize(
                            beforeBitmap,
                            4,
                            true
                        )
                        if (afterBitmap == null) {
                            (context as Activity).runOnUiThread {
                                EggUtil.toast("失败，请重试")
                            }
                            return@thread
                        }
                        val resultStr =
                            "压缩前占用内存大小：${EggUtil.getSizeExt(beforeBitmap.byteCount)}，压缩后占用内存大小：${EggUtil.getSizeExt(
                                afterBitmap.byteCount
                            )}"
                        val saveFile =
                            File(
                                AppStorageData.getFileOutFile(),
                                "图片压缩/p${EggUtil.getFileNameNoEx(file!!.name)}.jpeg"
                            )
                        EggUtil.saveBitmapFile(afterBitmap, saveFile)

                        (context as Activity).runOnUiThread {
                            compressText.visibility = View.VISIBLE
                            compressText.text = resultStr
                            XPopup.Builder(context)
                                .asConfirm("提示", "已保存到：${saveFile.absoluteFile}", null)
                                .show()
                        }

                    }

                    COMPRESS_STORAGE_TYPE -> {
                        val beforeBitmap = BitmapFactory.decodeFile(file!!.absolutePath)
                        val afterBitmap = EggUtil.compressByQuality(
                            beforeBitmap, 2,
                            true
                        )
                        if (afterBitmap == null) {
                            (context as Activity).runOnUiThread {
                                EggUtil.toast("失败，请重试")
                            }
                            return@thread
                        }
                        val saveFile =
                            File(
                                AppStorageData.getFileOutFile(),
                                "图片压缩/p${EggUtil.getFileNameNoEx(file!!.name)}.jpeg"
                            )
                        EggUtil.saveBitmapFile(afterBitmap, saveFile)

                        val resultStr =
                            "压缩前存储大小：${EggUtil.getSizeExt(file!!.length())}，压缩后存储大小：${EggUtil.getSizeExt(
                                saveFile.length()
                            )}"
                        (context as Activity).runOnUiThread {
                            compressText.visibility = View.VISIBLE
                            compressText.text = resultStr
                            XPopup.Builder(context)
                                .asConfirm("提示", "已保存到：${saveFile.absoluteFile}", null)
                                .show()
                        }
                    }

                    else -> {
                        EggUtil.toast("请选择类型")
                    }
                }
                (context as Activity).runOnUiThread {
                    compressBtnText.text = "压缩"
                }
            }

        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.compress_popup
    }
}


/*var beforeMemorySize: Int = 0
var afterMemorySize: Int = 0
when (afterBitmap!!.config) {
    Bitmap.Config.RGB_565 -> {
        beforeMemorySize = beforeBitmap.width * beforeBitmap.height * 2
        afterMemorySize = afterBitmap.width * afterBitmap.height * 2
    }

    Bitmap.Config.ARGB_8888 -> {
        beforeMemorySize = beforeBitmap.width * beforeBitmap.height * 4
        afterMemorySize = afterBitmap.width * afterBitmap.height * 4
    }

    else -> {
    }
}*/

/*println(resultStr2)
val resultStr =
    "压缩前内存大小：${EggUtil.getSizeExt(beforeMemorySize)}，压缩后内存大小：${EggUtil.getSizeExt(
        afterMemorySize
    )}"*/