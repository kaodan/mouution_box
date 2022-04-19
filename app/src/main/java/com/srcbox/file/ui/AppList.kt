package com.srcbox.file.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.fastjson.JSONArray
import com.bumptech.glide.Glide
import com.hjq.bar.OnTitleBarListener

import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnSelectListener
import com.srcbox.file.R
import com.srcbox.file.adapter.AppListAdapter
import com.srcbox.file.contract.AppListContract
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.UserAppData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.presenter.AppListPresenter
import com.srcbox.file.ui.extractManager.activity.ExtractManagerActivity
import com.srcbox.file.ui.popup.ExtractSrcPopup
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import com.srcbox.file.util.LoadAppList
import com.srcbox.file.util.resource.extract.ResourceData
import com.srcbox.file.util.resource.extract.ResourceExtractTask
import kotlinx.android.synthetic.main.app_list.*
import java.io.File

class AppList : AppCompatActivity(), AppListContract.View {
    private val appListPresenter = AppListPresenter(this)
    private var appListAdapter: AppListAdapter? = null
    private val appArrayList: ArrayList<UserAppData> = ArrayList()

    //    private var inf:ArrayList<UserAppData> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (onceActivity == null) {
            onceActivity = this
        } else {
            val v = getRootView(onceActivity!!)
            (v.parent as ViewGroup).removeView(v)
            setContentView(v)
            GlobUtil.changeTitle(this)
            if (LoadAppList.state == LoadAppList.END_STATE) {
//                appListPresenter.installAppInfo(sortTimeType)
                initView()
            }
            onceActivity = this
            return
        }

        setContentView(R.layout.app_list)
        initView()
    }


    private fun initView() {
        GlobUtil.changeTheme(title_bar.rightView)

        AppListAdapter.selectApp.clear()
        searchStr = ""
        val s = intent.getStringExtra("search_app_name")
        s?.let {
            searchStr = s
        }
        GlobUtil.changeTitle(this)
        EggUtil.loadIcon(this, AppSetting.colorStress, search_icon)

        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_pressed)
        states[1] = intArrayOf(android.R.attr.state_enabled)
        val colors = intArrayOf(
            Color.parseColor(AppSetting.colorTransTress),
            Color.parseColor(AppSetting.colorStress)
        )
        val colorStateList = ColorStateList(states, colors)
        floating_right_button.backgroundTintList = colorStateList


        appListPresenter.installAppInfo(sortTimeType)
        floating_right_button.setOnClickListener {
            XPopup.Builder(this).atView(floating_right_button)
                .asAttachList(arrayOf("按时间排序", "按大小排序", "提取全部图标"), null,
                    OnSelectListener { position, _ ->
                        when (position) {
                            0 -> {
                                sortType = sortTimeType
                                appListPresenter.installAppInfo(sortTimeType)
                            }
                            1 -> {
                                sortType = sortSizeType
                                appListPresenter.installAppInfo(sortSizeType)
                            }

                            2 -> {
                                val asLoading = XPopup.Builder(this).asLoading()
                                asLoading.show()
                                val outDir = File(AppStorageData.getFileOutFile(), "批量提取/全部图标")
                                appArrayList.forEach {
                                    println(it.appPackageName)
                                    EggUtil.saveBitmapFile(
                                        EggUtil.getBitmapFromDrawable(it.appIcon),
                                        File(outDir, "${it.appPackageName}.png")
                                    )
                                }
                                asLoading.dismiss()
                                XPopup.Builder(this)
                                    .asConfirm("提示", "文件保存在了：${outDir.absolutePath}", null).show()
                            }
                            /*2 -> {
                                ResourceData.outDir =
                                    File(AppStorageData.getFileOutFile(), "批量提取")
                                startActivity(Intent(this@AppList, ExtractManager::class.java))
                            }*/
                        }
                    }).show()
        }




        title_bar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {
                finish()
            }

            override fun onRightClick(v: View?) {
                v as TextView
                if (v.text != "提取") {
                    AppListAdapter.isSelect = true
                    EggUtil.toast("请选择APP")
                    v.text = "提取"
                } else {
                    XPopup.Builder(this@AppList).atView(v)
                        .asAttachList(arrayOf("图标", "素材", "安装包"), null) { i: Int, _: String ->
                            ResourceData.outDir =
                                File(AppStorageData.getFileOutFile(), "批量提取")
                            when (i) {
                                0 -> {
                                    val asLoad = XPopup.Builder(this@AppList).asLoading()
                                    asLoad.show()
                                    Thread {
                                        ResourceData.extractType = ResourceData.EXTRACT_TYPE_ICON
                                        AppListAdapter.selectApp.forEach {
                                            EggUtil.saveBitmapFile(
                                                EggUtil.getBitmapFromDrawable(it!!.appIcon),
                                                File(
                                                    AppStorageData.getFileOutFile(),
                                                    "批量提取/${it.appPackageName}.png"
                                                )
                                            )
                                        }

                                        appListPresenter.installAppInfo(sortTimeType)
                                        runOnUiThread {
                                            asLoad.setTitle("${AppSetting.appFileOut}/批量提取")
                                            asLoad.delayDismiss(5000)
                                            AppListAdapter.isSelect = false
                                            AppListAdapter.selectApp.clear()
                                            title_bar.rightView.text = "批量提取"

                                        }
                                    }.start()
                                }

                                1 -> {
                                    ResourceData.extractType = ResourceData.EXTRACT_TYPE_SRC
                                    startActivity(
                                        Intent(
                                            this@AppList,
                                            ExtractManagerActivity::class.java
                                        )
                                    )
                                }

                                2 -> {
                                    val asLoad = XPopup.Builder(this@AppList).asLoading()
                                    asLoad.show()
                                    Thread {
                                        ResourceData.extractType = ResourceData.EXTRACT_TYPE_PACK
                                        AppListAdapter.selectApp.forEach {
                                            val resourceExtractTask =
                                                ResourceExtractTask(this@AppList, it.appSource, it)
                                            resourceExtractTask.startTask { }
                                        }
                                        appListPresenter.installAppInfo(sortTimeType)
                                        runOnUiThread {
                                            asLoad.setTitle("${AppSetting.appFileOut}/批量提取")
                                            asLoad.delayDismiss(5000)
                                            AppListAdapter.isSelect = false
                                            AppListAdapter.selectApp.clear()
                                            title_bar.rightView.text = "批量提取"
                                        }
                                    }.start()
                                }
                            }
                        }.show()
                }
            }

            override fun onTitleClick(v: View?) {}
        })

        search_icon.setOnClickListener {
            searchStr = search_edit.text.toString()
            appListPresenter.installAppInfo(sortType)
        }

        search_edit.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                searchStr = search_edit.text.toString()
                appListPresenter.installAppInfo(sortType)
                EggUtil.hideKeyboard(this)
            }
            false
        }
    }

    private fun getRootView(context: Context): View {
        val activity = context as Activity
        return activity.findViewById(R.id.root)
    }

    override fun onRestart() {
        super.onRestart()
        AppListAdapter.isSelect = false
        AppListAdapter.selectApp.clear()
        app_list.adapter?.notifyDataSetChanged()
        title_bar.rightView.text = "批量提取"
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event!!.action == KeyEvent.ACTION_DOWN) {
            title_bar.rightView.text = "批量提取"

            if (!AppListAdapter.isSelect) {
                AppListAdapter.selectApp.clear()
                finish()
            } else {
                AppListAdapter.isSelect = false
                AppListAdapter.selectApp.clear()
                app_list.adapter?.notifyDataSetChanged()
            }
        }
        return false
    }
/*    override fun onDestroy() {
        super.onDestroy()
        appListPresenter.stopInstallAppInfo()
    }*/

    override fun onStop() {
        super.onStop()
        AppListAdapter.isSelect = false
        AppListAdapter.selectApp.clear()
        app_list.adapter?.notifyDataSetChanged()
        title_bar.rightView.text = "批量提取"
    }

    override fun start() {}

    override fun listApp(arrayList: ArrayList<UserAppData>) {
        appArrayList.addAll(arrayList)
        runOnUiThread {
            EggUtil.hideKeyboard(this)
            if (arrayList.size == 0) {
                Glide.with(applicationContext).load(R.mipmap.none).into(center_img)
                center_none.visibility = View.VISIBLE
                center_text.visibility = View.VISIBLE
                return@runOnUiThread
            }
            AppListAdapter.isSelect = false
            AppListAdapter.selectApp.clear()
            center_none.visibility = View.GONE
            center_text.visibility = View.GONE
            search_bar_container.visibility = View.VISIBLE
            app_list.visibility = View.VISIBLE
            app_list.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            appListAdapter = AppListAdapter(applicationContext, arrayList)
            app_list.adapter = appListAdapter
        }
    }

    override fun loading() {
        runOnUiThread {
            search_bar_container.visibility = View.GONE
            app_list.visibility = View.GONE
            center_none.visibility = View.VISIBLE
            center_text.visibility = View.GONE
            Glide.with(applicationContext!!).load(R.mipmap.loading).into(center_img)
        }

    }

    companion object {
        private var onceActivity: AppList? = null
        var typeAppsMessage: JSONArray? = null
        const val sortTimeType = 0
        const val sortSizeType = 1
        var searchStr = ""
        var sortType = sortTimeType
    }
}

/*Thread {
                       AppListAdapter.selectApp.forEach {
                           *//* val resourceExtractManager = ResourceExtractManager()
                         resourceExtractManager.addTask()*//*
                            ResourceExtractTask(it.appSource,it).startTask { itF ->
                                println("${it.name} $itF")
                            }
                        }
                    }.start()*/

//        listApp()

//                                listApp()

//
//                                listApp()

/*
inf.sortWith(Comparator { o1, o2 ->
    var i = 0

    i
})*/


/*var b = false
            try {
                if (typeAppsMessage?.get(currAppI).toString().contains(it.appPackageName)) {
                    b = true
                }
                currAppI++
            }catch (e:IndexOutOfBoundsException){
                e.message
            }
            b*


            i = o2.appSize - o1.appSize
if (i == 0) {
    i = o2.appSize - o1.appSize
}


 */


/*
      EggUtil.loadIcon(
          this,
          AppSetting.colorStress,
          findViewById<TitleBar>(R.id.title_bar).leftView
      )
      title_bar.setOnTitleBarListener(object : OnTitleBarListener {
          override fun onLeftClick(v: View?) {
              finish()
          }

          override fun onRightClick(v: View?) {}
          override fun onTitleClick(v: View?) {}
      })*/
//        val appInfoList = ArrayList<UserAppData>()

/*val applications =
                packageManager.getInstalledPackages(0)
            applications.forEach {

                appInfoList.add(
                    UserAppData(
                        it.applicationInfo.loadIcon(packageManager),
                        it.applicationInfo.loadLabel(packageManager).toString(),
                        ((File(it.applicationInfo.sourceDir).length()) / 1024).toInt(),
                        it.packageName
                    )
                )
            }*/