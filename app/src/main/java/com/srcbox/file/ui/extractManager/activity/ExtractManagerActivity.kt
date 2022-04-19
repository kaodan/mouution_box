package com.srcbox.file.ui.extractManager.activity

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.srcbox.file.R
import com.srcbox.file.adapter.AppListAdapter
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.ui.extractManager.code.ExtractCacheManager
import com.srcbox.file.ui.extractManager.code.ExtractManager
import com.srcbox.file.ui.extractManager.code.ExtractResourceFormApp
import com.srcbox.file.ui.extractManager.code.ExtractTaskMessage
import com.srcbox.file.ui.extractManager.code.adapter.ExtractItem
import com.srcbox.file.util.GlobUtil
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

class ExtractManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.extract_activity)

        GlobUtil.changeTitle(this)

        ExtractManager.outDir = File(AppStorageData.getFileOutFile(), "批量提取")
        ExtractManager.ruleJson =
            JSON.parseObject(readFile(assets.open("json/extTable/ext1.4.json")))
        ExtractCacheManager.getExtractMessages().forEach {
            println("数据是${it.key} ${it.value.state}")
        }
        ExtractManager.type = ExtractManager.TYPE_FILE

        ExtractManager.handler = MyHandler(this)

        AppListAdapter.selectApp.forEach {
            if (ExtractResourceFormApp.jobHashMap[it.appSource.path] == null) {
                val extApp = ExtractResourceFormApp(
                    ExtractTaskMessage(it.appSource, it.name)
                )
                extApp.isNewTask = true
                ExtractManager.addTask(extApp)
            }
        }
        ExtractManager.syncLocal()
        ExtractManager.startAllTask()
        findViewById<RecyclerView>(R.id.extract_x_list).layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.extract_x_list).adapter =
            ExtractItem(this, ExtractManager.extractTaskHashMap)
        findViewById<RecyclerView>(R.id.extract_x_list).setHasFixedSize(true)
    }

    class MyHandler(private val context: Activity) : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val bundle = msg.data
            val tag = bundle.getString("tag")
            when (msg.what) {
                ExtractManager.HANDLER_START -> {
                    println("主界面已接收到启动 $tag")
                }

                ExtractManager.HANDLER_PAUSE -> {
                }

                ExtractManager.HANDLER_CANCEL -> {

                }

                ExtractManager.HANDLER_PROGRESS -> {
                    val value = bundle.getString("value")
                    context.findViewById<RecyclerView>(R.id.extract_x_list).adapter?.notifyItemChanged(
                        ExtractManager.extractTaskHashMap.keys.indexOf(tag)
                    )
                }
            }
        }
    }

    fun readFile(fileIn: InputStream): String {
        val strB = StringBuilder()
        val isr = InputStreamReader(fileIn)
        val br = BufferedReader(isr)
        try {
            while (br.ready()) {
                strB.append(br.readLine() + "\n")
            }
        } catch (e: Exception) {
            println(e.message + "错误")
        }
        return strB.toString().trimIndent()
    }
}


/*packages.add("fasv.jc")
*/
//        packages.add("per.goweii.wanandroid")
//                    context.findViewById<RecyclerView>(R.id.extract_list).adapter?.notifyDataSetChanged()
//                    context.findViewById<TextView>(R.id.test_text).text = value
//                    context.findViewById<TextView>(R.id.test_progress).text = value
//        println(strB.toString().trimIndent())

/*  fun changeItem(newVal: ExtractTaskMessage?) {
      newVal?.let {
          val lv =
              extract_container.findViewWithTag<LinearLayout>(it.file.path)
          println("当前有:${lv.childCount}")
          val textView = lv.findViewById<TextView>(R.id.progress)
          textView.text = EggUtil.computeProgress(it.thisFilePos, it.total).toString()
      }

  }*/

/*

{
/data/app/fasv.jc-eFoMKRGSlbl46eqrc-0Zsw==/base.apk=ExtractTaskMessage(file=/data/app/fasv.jc-eFoMKRGSlbl46eqrc-0Zsw==/base.apk, appName=fasv.jc, state=0, thisFilePos=804), /data/app/per.goweii.wanandroid-llg3wkMSzINWEaa0NkkxaQ==/base.apk=ExtractTaskMessage(file=/data/app/per.goweii.wanandroid-llg3wkMSzINWEaa0NkkxaQ==/base.apk, appName=per.goweii.wanandroid, state=0, thisFilePos=1194)
}*/


/*extManager.extractTaskHashMap.forEach {
println("找到了：${it.key}")
val view = LayoutInflater.from(this).inflate(R.layout.item, null, false)
view.tag = it.key
extract_container.addView(view)
}*/
/*
        extract_list.layoutManager = LinearLayoutManager(this)
        listAdapterData = extManager.list()
        extract_list.adapter = com.srcbox.file.ui.extractManager.code.adapter.Item(this, listAdapterData)*/


/*if (newVal == null) {
    return
}
var i = 0
var thP = -1
listAdapterData.forEach {
    if (it.file.path == newVal.file.path) {
        listAdapterData[i] = newVal
        thP = i
        return@forEach
    }
    i++
}
println("已通知：$thP    ${EggUtil.computeProgress(newVal.thisFilePos, newVal.total)}")
extract_list.adapter?.notifyItemChanged(thP)*/