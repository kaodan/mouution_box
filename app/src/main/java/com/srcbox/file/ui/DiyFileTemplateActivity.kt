package com.srcbox.file.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.lxj.xpopup.XPopup
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.login.LoginActivity
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import com.srcbox.file.util.Member
import kotlinx.android.synthetic.main.activity_diy_file_template.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.rosuh.filepicker.config.FilePickerManager
import java.io.File


class DiyFileTemplateActivity : AppCompatActivity() {
    private var magicNumber: String? = null
    private var extractStr: String? = null
    private val FILE_SELECT_CODE = 0
    private val newExtFile = File(AppStorageData.getFileOutFile(), "ext1.4.json")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diy_file_template)
        GlobUtil.changeTitle(this)
        newExtFile.parentFile?.let {
            if (!it.exists()){
                it.mkdirs()
            }
        }

        if (!newExtFile.exists()) {
            EggIO.copyFileTo(
                assets.open("json/extTable/ext1.4.json"),
                newExtFile.outputStream()
            )
        }
        selectFile.setOnClickListener {
            showFileChooser()
        }

        yesFile.setOnClickListener {
            if (magicNumber != null && extractStr != null) {
                GlobalScope.launch(Dispatchers.Main) {
                    val asLoading = XPopup.Builder(this@DiyFileTemplateActivity).asLoading()
                    val memberData = Member.MemberData("0")
                    when (Member.isVip(memberData)) {
                        Member.UserType.TIME_LIMIT -> {
                            writeExt()
                        }

                        Member.UserType.ORDINARY -> {
                            println("ordinary user")
                            EggUtil.toast("请充值")
                            startActivity(
                                Intent(
                                    this@DiyFileTemplateActivity,
                                    GetMemberActivity::class.java
                                )
                            )
                        }

                        Member.UserType.ALWAYS -> {
                            writeExt()
                        }

                        Member.UserType.NO_LOG -> {
                            println("user no log")
                            EggUtil.toast("请登录")
                            asLoading.delayDismiss(500)
                            startActivity(
                                Intent(
                                    this@DiyFileTemplateActivity,
                                    LoginActivity::class.java
                                )
                            )
                            return@launch
                        }

                        Member.UserType.ERROR -> {
                            EggUtil.toast("出现错误，请重试")
                        }

                        Member.UserType.NOTHING_CONNECT -> {
                            EggUtil.toast("网络无连接")
                        }
                    }
                }
            } else {
                EggUtil.toast("请选择文件")
            }
        }

        refreshDiyList()
    }

    private fun refreshDiyList() {
        diy_list_container.removeAllViews()
        val extTab = JSON.parseObject(EggIO.readFile(newExtFile))
        val extTabDiy = extTab.getJSONObject("自定义")
        extTabDiy?.forEach {
            kotlin.run {
                if (it.key == "on") return@run

                val view = LayoutInflater.from(this)
                    .inflate(R.layout.view_item_diy_file, diy_list_container, false)
                view.findViewById<TextView>(R.id.diy_magic).text = it.key
                view.findViewById<TextView>(R.id.diy_extract).text = it.value.toString()
                val closeDiyIcon = view.findViewById<TextView>(R.id.close_diy)
                EggUtil.loadIcon(
                    this,
                    AppSetting.colorStress,
                    closeDiyIcon
                )

                closeDiyIcon.setOnClickListener { _ ->
                    extTabDiy.remove(it.key)
                    EggIO.writeFile(newExtFile, extTab.toJSONString())
                    diy_list_container.removeView(view)
                }
                diy_list_container.addView(view)
            }

        }
    }

    private fun writeExt() {
        if (!newExtFile.exists()) {
            EggIO.copyFileTo(
                assets.open("json/extTable/ext1.4.json"),
                newExtFile.outputStream()
            )
        }

        val extTab = JSON.parseObject(EggIO.readFile(newExtFile))
        var chideJson = JSONObject()
        extTab.getJSONObject("自定义")?.let {
            chideJson = it
        }
        chideJson[magicNumber] = extractStr
        chideJson["on"] = true
        extTab["自定义"] = chideJson
        EggIO.writeFile(newExtFile, extTab.toJSONString())
        EggUtil.toast("成功")
        refreshDiyList()
    }

    @SuppressLint("Recycle", "SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FILE_SELECT_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val list = FilePickerManager.obtainData()
                    val oneStr = list[0]

                    val oneFile = File(oneStr)
                    val buff = ByteArray(3)
                    oneFile.inputStream().read(buff, 0, buff.size)
                    val magicNumber = EggUtil.byteToHexString(buff)
                    val fileExt = EggUtil.getPathExtend(oneStr)
                    fileShow.visibility = View.VISIBLE
                    magicNumberText.text = "该文件的魔数是：0X$magicNumber"
                    extractText.text = "该文件的后缀是：$fileExt"
                    this.magicNumber = magicNumber
                    this.extractStr = fileExt
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showFileChooser() {
        FilePickerManager.from(this)
            .maxSelectable(1)
            .forResult(FILE_SELECT_CODE)
    }
}

/* @SuppressLint("Recycle")
    fun getPath(context: Context, uri: Uri?): String? {
        if ("content".equals(uri?.scheme, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(uri!!, projection, null, null, null)
                val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(columnIndex)
                }
            } catch (e: Exception) {
                // Eat it  Or Log it.
            }
        } else if ("file".equals(uri?.scheme, ignoreCase = true)) {
            return uri?.path
        }
        return null
    }*/