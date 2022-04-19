package com.srcbox.file.ui.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Environment
import android.widget.LinearLayout
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.egg.extractmanager.ExtractListener
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.UserAppData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.extractManager.code.ExtractManager
import com.srcbox.file.ui.extractManager.code.ExtractResourceFormApp
import com.srcbox.file.ui.extractManager.code.ExtractTaskMessage
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GetResourceFormFile
import org.angmarch.views.NiceSpinner
import java.io.File

class ExtractSrcPopup(context: Context) : BottomPopupView(context) {

    companion object {
        var userAppData: UserAppData? = null
    }

    override fun getImplLayoutId(): Int {
        return R.layout.custom_popup
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        super.onCreate()
        //提取按钮
        val extractAppSrc = findViewById<LinearLayout>(R.id.extract_app_src)
        //提取按钮文本
        val extractAppSrcText = findViewById<TextView>(R.id.extract_app_src_text)
        //类型单选框
        val extractAppSrcFileType = findViewById<NiceSpinner>(R.id.nice_spinner_src_file_type)
        extractAppSrcFileType.attachDataSource(arrayListOf("安装包的资源", "本地的资源"))
        val baseFile = File(AppStorageData.getFileOutFile(), "单个提取")
        ExtractManager.outDir =
            File(baseFile, "安装包资源")

        findViewById<TextView>(R.id.extract_out_dir).text = "存储路径${baseFile.path}"


        extractAppSrc.setOnClickListener {
            var file = File("")
            when (extractAppSrcFileType.text.toString()) {
                "安装包的资源" -> {
                    val newExtFile = File(AppStorageData.getFileOutFile(), "ext1.4.json")
                    extractAppSrcText.text = "提取中"
                    ExtractManager.ruleJson =
                        JSON.parseObject(EggIO.readFile(newExtFile))
                    ExtractResourceFormApp(
                        ExtractTaskMessage(userAppData!!.appSource, userAppData!!.name),
                        object : ExtractListener {
                            override fun onProgress(float: Float) {
                                (context as Activity).runOnUiThread {
                                    extractAppSrcText.text = float.toString()
                                }
                            }

                            override fun onStart() {
                                (context as Activity).runOnUiThread {
                                    extractAppSrcText.text = "提取中"
                                }
                            }

                            override fun onPause() {
                                (context as Activity).runOnUiThread {
                                    extractAppSrcText.text = "提取"
                                }
                            }

                            override fun onCancel() {
                                (context as Activity).runOnUiThread {
                                    extractAppSrcText.text = "提取"
                                }
                            }

                            @SuppressLint("SetTextI18n")
                            override fun onSuccess() {
                                (context as Activity).runOnUiThread {
                                    extractAppSrcText.text = "已完成"
                                    XPopup.Builder(context)
                                        .asConfirm("提示", "已保存在：${ExtractManager.outDir}", null)
                                        .show()
                                }
                            }
                        }).start()
                    return@setOnClickListener
                }

                "本地的资源" -> {
                    if (Build.VERSION.SDK_INT >= 30) {
                        EggUtil.toast("抱歉，此功能暂无法使用")
                        return@setOnClickListener
                    }

                    extractAppSrcText.text = "准备中"
                    file = File(
                        Environment.getExternalStorageDirectory().path,
                        "Android/data/${userAppData!!.appPackageName}"
                    )
                    val newExtFile = File(AppStorageData.getFileOutFile(), "ext1.4.json")
                    GetResourceFormFile(
                        file,
                        File(
                            baseFile,
                            "本地资源提取/${userAppData!!.name}"
                        ),
                        JSON.parseObject(EggIO.readFile(newExtFile))
                    ) {
                        (context as Activity).runOnUiThread {
                            when (it) {
                                -1F -> {
                                    EggUtil.toast("无文件")
                                    extractAppSrcText.text = "提取资源"
                                }

                                100.0F -> {
                                    extractAppSrcText.text = "提取资源"
                                }

                                else -> {
                                    extractAppSrcText.text = it.toString()
                                }
                            }
                        }
                    }
                    return@setOnClickListener
                }
            }
        }
    }
}


/*

//        niceSpinnerNameType.attachDataSource(arrayListOf("包名", "名称"))

//        val niceSpinnerSrcType = findViewById<NiceSpinner>(R.id.nice_spinner_src_type)
//        niceSpinnerSrcType.attachDataSource(arrayListOf("全部", "图片", "视频", "动图"))
//        val niceSpinnerNameType = findViewById<NiceSpinner>(R.id.nice_spinner_name_type)
//        niceSpinnerSrcSize.attachDataSource(arrayListOf("全部", "大于1M", "大于200kb", "大于3M"))

        val resourceExtractTask =
            ResourceExtractTask(userAppData!!.appSource, userAppData!!)
        extractAppSrc.setOnClickListener {
//            EggUtil.toast(niceSpinnerSrcSize.text.toString())

            if (extractAppSrcText.text == "提取资源") {

                when (extractAppSrcFileType.text.toString()) {
                    "素材" -> {
                        ResourceData.extractType = ResourceData.EXTRACT_TYPE_SRC
                    }

                    "图标" -> {
                        ResourceData.extractType = ResourceData.EXTRACT_TYPE_ICON
                    }

                    "安装包" -> {
                        ResourceData.extractType = ResourceData.EXTRACT_TYPE_PACK
                    }

                    "本地资源" -> {
                        ResourceData.extractType = ResourceData.EXTRACT_TYPE_LOCATION
                    }
                }

                ResourceData.outDir = AppStorageData.getFileOutFile()
                extractAppSrcText.text = "提取中"
                Thread {

                    resourceExtractTask.startTask { itF ->
                        println(itF)


                        when (ResourceData.extractType) {
                            ResourceData.EXTRACT_TYPE_SRC -> {
                                (context as Activity).runOnUiThread {
                                    extractAppSrcText.text = itF.toString()
                                    if (itF == 100F) {
                                        extractAppSrcText.text = "浏览图片"
                                    }
                                }
                            }

                            ResourceData.EXTRACT_TYPE_ICON -> {
                                /*
                                (context as Activity).runOnUiThread {
                                    extractAppSrcText.text = resourceExtractTask.outDir.toString()
                                }*/
                                (context as Activity).runOnUiThread {
                                    EggUtil.saveDrawable(
                                        userAppData!!.appIcon,
                                        File(
                                            AppStorageData.getFileOutFile(),
                                            "${userAppData!!.name}/图标/${userAppData!!.appPackageName}.png"
                                        ), com.srcbox.file.ui.extractManager.code.EggApplication.context, true
                                    )
                                    extractAppSrcText.text = "提取资源"
                                    EggUtil.toast("已保存到根目录：${AppSetting.appFileOut}")
                                }

                            }

                            ResourceData.EXTRACT_TYPE_PACK -> {
                                (context as Activity).runOnUiThread {
                                    extractAppSrcText.text = resourceExtractTask.outDir.toString()
                                }
                            }

                            ResourceData.EXTRACT_TYPE_LOCATION -> {

                                /* Thread {
                                     val p =
                                         "${Environment.getExternalStorageDirectory()}/Android/data/${userAppData!!.appPackageName}"
                                     val po = File(p)
 //            val pSums = po.listFiles()?.size
                                     var isGet = false
                                     val getSrcFormDir = GetSrcFormDir(
                                         context as Activity,
                                         getZipFile!!
                                     ) { i: Int, b: Boolean ->
                                         isGet = b
                                         (context as Activity).runOnUiThread {
 //                                            show_get_local_src_text.text = "已提取：${i}个"
                                         }
                                     }
                                     getSrcFormDir.setOutDir("$apkOutPath/本地资源")
                                     getSrcFormDir.getSrc(po)
                                     (context as Activity).runOnUiThread {
                                         if (isGet && getSrcFormDir.fNum != 0) {
 //                                            show_get_local_src_text.text = "提取本地资源"
                                         } else {
                                             EggUtil.toast("此应用无资源")
 //                                            show_get_local_src_text.text = "提取本地资源"
                                         }
                                     }
                                 }.start()*/


                                EggUtil.toast("暂时不开放")
                            }
                        }
                    }
                }.start()
            } else if (extractAppSrcText.text == "浏览图片") {
//                EggUtil.toast(ResourceData.outDir.toString())
                val intent = Intent(context, ImageSelectActivity::class.java)
//                EggUtil.toast(resourceExtractTask.outDir.toString())
                intent.putExtra("filePath", resourceExtractTask.outDir.absolutePath)
                context.startActivity(intent)
            }
        }

*/
