package com.srcbox.file.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArraySet
import com.alibaba.fastjson.JSON
import com.daimajia.numberprogressbar.NumberProgressBar
import com.hjq.bar.OnTitleBarListener
import com.srcbox.file.R
import com.srcbox.file.adapter.AppListAdapter
import com.srcbox.file.data.UserAppData
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import com.srcbox.file.util.resource.extract.ExtTable
import com.srcbox.file.util.resource.extract.ResourceData
import com.srcbox.file.util.resource.extract.ResourceExtractTask
import kotlinx.android.synthetic.main.extract_manager.*
import java.io.File

class ExtractManager : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.extract_manager)
        GlobUtil.changeTitle(this, false)
//        val oldArr = ArraySet<UserAppData>()


        warn_text.text = "不要退出此界面\n保存路径:${ResourceData.outDir}"

        title_bar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {}

            override fun onRightClick(v: View?) {}

            override fun onTitleClick(v: View?) {}
        })


        views.forEach { itV ->
            AppListAdapter.selectApp.forEach {
                if (it != null) {
                    if (itV.tag == it.appPackageName) {
                        AppListAdapter.selectApp.remove(it)
                        val itVParent = itV.parent
                        itVParent as ViewGroup
                        itVParent.removeView(itV)
                        progress_container.addView(itV)
                    }
                }
            }
        }

        AppListAdapter.selectApp.forEach {
            Thread {
                val view = LayoutInflater.from(this).inflate(R.layout.extract_item, null, false)
                views.add(view)
                Thread.currentThread().name = it.appPackageName
                view.tag = it.appPackageName
                val viewNumProgress = view.findViewById<NumberProgressBar>(R.id.ext_progress)

                view.findViewById<TextView>(R.id.ext_text).text = it.name
                val resourceExtractTask = ResourceExtractTask(this, it.appSource, it)
                runOnUiThread {
                    view.findViewById<ImageView>(R.id.ext_img).setImageDrawable(it.appIcon)
                    viewNumProgress.max = 100
                    progress_container.addView(view)

                }

                resourceExtractTask.startTask { itF ->
                    runOnUiThread {
                        println("${it.name} ${itF.toInt()}")
                        viewNumProgress.progress = itF.toInt()
                    }
                }
            }.start()
        }
    }


    companion object {
        val views = ArrayList<View>()
    }
}

/*ExtTable.getExtExtractsFile().listFiles()!!.forEach {
    kotlin.run {
        val jo = JSON.parseObject(EggIO.readFile(it))
        *//*if (jo.getInteger("state") == ResourceData.SUCCESS_STATE){
                    return@run
                }*//*
                val packA = packageManager.getPackageInfo(jo.getString("filePackage"), 0)
                val userAppData = UserAppData(
                    File(packA.applicationInfo.sourceDir),
                    packA.applicationInfo.loadIcon(packageManager),
                    packA.applicationInfo.loadLabel(packageManager).toString(),
                    (File(packA.applicationInfo.sourceDir).length() / 1024).toInt(),
                    packA.packageName,
                    packA.firstInstallTime,
                    EggUtil.isOsApp(packA.applicationInfo)
                )
                oldArr.add(userAppData)
            }
        }*/

/*val progress_stop_or_start =
            view.findViewById<TextView>(R.id.progress_stop_or_start)
        val progress_del_or_start =
            view.findViewById<TextView>(R.id.progress_del_or_start)*/
/* progress_stop_or_start.setOnClickListener { itV ->
                       itV as TextView
                       when (itV.text) {
                           "开始" -> {
                               itV.text = "暂停"
                               Thread {
                                   resourceExtractTask.startTask { itF ->
                                       runOnUiThread {
                                           println("${it.name}哈哈 ${itF.toInt()}")
                                           viewNumProgress.progress = itF.toInt()
                                       }
                                   }
                               }.start()
                           }

                           "暂停" -> {
                               itV.text = "开始"
                               resourceExtractTask.pause()
                           }
                       }
                   }

                   progress_del_or_start.setOnClickListener {
                       progress_container.removeView(view)
                       resourceExtractTask.removeTask()
                   }*/

/*println(jo.getInteger("state"))
         if (jo.getInteger("state") == ResourceData.SUCCESS_STATE){
             return@run
         }*/


//        val s = AppListAdapter.selectApp.stream().distinct().collect(Collectors.toList())

/*var isU = false
views.forEach { itV ->
    if (itV.tag == it.appPackageName){
        val itVParent = itV.parent
        itVParent as ViewGroup
        itVParent.removeView(itV)
        progress_container.addView(itV)
        isU = true
        return
    }
    println("控件TAG${itV.tag}")
}


if (isU){
    return@forEach
}*/

/*val threadGroup = Thread.currentThread().threadGroup
val ac = threadGroup!!.activeCount()
val listThread = arrayOfNulls<Thread>(ac)
threadGroup.enumerate(listThread)
for (index in 0 until ac) {
    val tName = listThread[index]?.name
    if (tName == it.appPackageName) {

        }
        return@forEach
    }
    println(tName)
}
*/
//            val resourceExtractManager = ResourceExtractManager()


/*var isU = false
               val cC = progress_container.childCount

               for (index in 0 until cC) {
                   val cCView = progress_container.getChildAt(cC)
                   if (cCView != null) {
                       if (cCView.tag == it.appPackageName) {
                           isU = true
                       }
                   }
               }

               if (!isU) {
                   views.add(view)
               }
              */

/*
        val view = LayoutInflater.from(this).inflate(R.layout.extract_item, null, false)
        val viewNumProgress = view.findViewById<NumberProgressBar>(R.id.ext_progress)
        view.findViewById<TextView>(R.id.ext_text).text = it.name
        runOnUiThread {
            view.findViewById<ImageView>(R.id.ext_img).setImageDrawable(userAppData.appIcon)
            viewNumProgress.max = 100
            progress_container.addView(view)
        }

        ResourceExtractTask(userAppData.appSource, userAppData).startTask { itF ->
            runOnUiThread {
                println("${it.name} ${itF.toInt()}")
                viewNumProgress.progress = itF.toInt()
            }
        }*/
