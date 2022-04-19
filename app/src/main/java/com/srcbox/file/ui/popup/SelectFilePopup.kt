package com.srcbox.file.ui.popup

import android.content.Context
import android.os.Environment
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.srcbox.file.R
import com.srcbox.file.adapter.PopupSelectFileAdapter
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.SpTool
import java.io.File


fun filter(files: List<File>?): List<File>? {
    return files?.filter {
        it.isDirectory && !it.name.startsWith(".")
    }?.sortedBy {
        it.name
    }
}

class SelectFilePopup(context: Context) : BottomPopupView(context) {
    private var fList: List<File>? = null
    private var adapter: PopupSelectFileAdapter? = null
    override fun onCreate() {
        super.onCreate()
        val listV = findViewById<RecyclerView>(R.id.select_file_list)
        val popupFileBack = findViewById<TextView>(R.id.popup_select_back)
        val popupSelectButton = findViewById<FloatingActionButton>(R.id.popup_select_button)
        val popupAddButton = findViewById<FloatingActionButton>(R.id.popup_add_button)
        val popupSelFilTi = findViewById<TextView>(R.id.popup_select_file_tit)
        EggUtil.loadIcon(context, AppSetting.colorStress, popupFileBack)
        AppStorageData.getFileOutFile().let {
            if (it.exists()) {
                PopupSelectFileAdapter.currentFile = AppStorageData.getFileOutFile()
            } else {
                PopupSelectFileAdapter.currentFile = Environment.getExternalStorageDirectory()
            }
        }
        popupSelFilTi.text = PopupSelectFileAdapter.currentFile?.absolutePath



        popupAddButton.setOnClickListener {
            XPopup.Builder(context).asInputConfirm("输入文件名", "") {
                if (adapter?.arrayList == null) {
                    EggUtil.toast("失败")
                    return@asInputConfirm
                }
                if (it.isEmpty()) {
                    EggUtil.toast("不能为空")
                    return@asInputConfirm
                }
                val f = File(PopupSelectFileAdapter.currentFile, it)
                if (f.exists()) {
                    EggUtil.toast("文件已存在")
                    return@asInputConfirm
                }
                f.mkdirs()
                adapter?.arrayList?.add(f)

                EggUtil.toast("已创建")
                adapter?.notifyItemChanged(adapter?.arrayList!!.size - 1)
                listV.scrollToPosition(adapter?.arrayList!!.size - 1)
            }.show()
        }

        popupSelectButton.setOnClickListener {

            AppSetting.appFileOut = PopupSelectFileAdapter.currentFile!!.absolutePath.replace(
                Environment.getExternalStorageDirectory().path,
                ""
            )
            SpTool.putSettingString(
                "fileOutPath",
                AppSetting.appFileOut
            )
            EggUtil.toast("已保存")
            dismiss()
//            EggUtil.toast(AppStorageData.getFileOutFile().absolutePath)
        }

        fList = PopupSelectFileAdapter.currentFile?.listFiles()?.toList()
        fList = filter(fList)
        val arrl = ArrayList<File>()
        fList?.toList()?.let { arrl.addAll(it) }

        fList?.let {
            if (fList!!.isNotEmpty()) {
                adapter = PopupSelectFileAdapter(
                    arrl
                )
            }
        }

        listV.layoutManager = LinearLayoutManager(context)
        listV.adapter = adapter



        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onChanged() {
                super.onChanged()
                popupSelFilTi.text = PopupSelectFileAdapter.currentFile?.absolutePath
            }
        })


        popupFileBack.setOnClickListener {
            if (PopupSelectFileAdapter.currentFile == Environment.getExternalStorageDirectory()) {
                dismiss()
            } else {
                val f = PopupSelectFileAdapter.currentFile!!.parentFile
                PopupSelectFileAdapter.currentFile = f
                fList = f?.listFiles()?.toList()
                fList = filter(fList)
                adapter?.arrayList?.clear()
                fList?.let { it1 ->
                    adapter?.arrayList?.addAll(it1)
                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.popup_select_file
    }

}