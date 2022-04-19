package com.srcbox.file.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.bumptech.glide.Glide
import com.egg.extractmanager.ExtractListener
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.ui.extractManager.code.ExtractManager
import com.srcbox.file.ui.extractManager.code.ExtractResourceFormApp
import com.srcbox.file.ui.extractManager.code.ExtractTaskMessage
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.activity_point_file_extract.*
import java.io.File
import java.net.URI

class PointFileExtract : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_file_extract)
        GlobUtil.changeTitle(this)
        intent.data?.path?.let {
            val f = File(it)
            when (EggUtil.getPathExtend(f.name)) {
                "apk" -> {
                    Glide.with(this).load(R.mipmap.apk).into(file_type)
                }

                "zip" -> {
                    Glide.with(this).load(R.mipmap.zip).into(file_type)
                }

                else -> {
                    EggUtil.toast("抱歉，不支持此格式。")
                    finish()
                    return
                }
            }


            ExtractManager.outDir = File(AppStorageData.getFileOutFile(), "指定提取")
            file_name.text = f.name
            extract_out_dir.text = "存储路径：${ExtractManager.outDir.path}"
            res_extract.setOnClickListener {

                ExtractManager.ruleJson =
                    JSON.parseObject(EggIO.readFile(assets.open("json/extTable/ext1.4.json")))
                ExtractResourceFormApp(ExtractTaskMessage(f, f.name), object : ExtractListener {
                    override fun onProgress(float: Float) {
                        println(float)
                        runOnUiThread {
                            res_extract_text.text = float.toString()
                        }
                    }

                    override fun onStart() {
                        runOnUiThread {
                            res_extract_text.text = "提取中"
                        }
                    }

                    override fun onPause() {
                        runOnUiThread {
                            res_extract_text.text = "提取"
                        }
                    }

                    override fun onCancel() {
                        runOnUiThread {
                            res_extract_text.text = "提取"
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess() {
                        runOnUiThread {
                            res_extract_text.text = "已完成"
                        }
                    }
                }).start()
            }
            return
        }
        finish()
    }

    class MyHandler(context: Context) : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                ExtractManager.HANDLER_PROGRESS -> {
                    val v = msg.data.getString("value")
                    println(v)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ExtractManager.extractTaskHashMap.clear()
        ExtractResourceFormApp.jobHashMap.clear()
    }
}