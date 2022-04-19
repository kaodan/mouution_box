package com.srcbox.file.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.srcbox.file.data.`object`.CapData
import com.srcbox.file.data.`object`.CapturedImageConfig

class CapturedImagesServer : AccessibilityService() {
    override fun onCreate() {
        super.onCreate()
        CapturedImageConfig.isServerPermission = true
//        AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        CapturedImageConfig.isServerStart = true
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        CapData.rootNode = rootInActiveWindow
    }

    override fun onInterrupt() {}


    override fun onUnbind(intent: Intent?): Boolean {
        println("服务已结束")
        return super.onUnbind(intent)
    }
}

//        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return


/*
    private fun sendActivityMsg(map: HashMap<String, String>) {
        val intent = Intent(message.ACTION_PACK)
        map.forEach {
            intent.putExtra(it.key, it.value)
        }
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }*/

//        if (CapData.currentState != CapData.OK || CapData.currentState == CapData.RUNING || !CapData.isStart) return
//        println(event?.packageName)


//        val strArray = arrayOf("")
//        CapturedImageConfig.packages.toArray(strArray)
//        serviceInfo = accessibilityInfo

//                cutBitmap(CapData.imgBit,rect.left,rect.top,rect.width(),rect.height())
/*   try {
       saveBitmapFile(
           imageScale(CapData.imgBit, 1080, 2280),
           "${CapturedImageConfig.capStoragePath}source.png"
       )
   }catch (e:Exception){
       println(e.message)
   }*/

/*


//        println("包名：${event?.packageName}    $event")
        /*if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
//            println(event.packageName)
            println("包名：${event.packageName}    $event")
            if (CapData.currentState != CapData.OK) return
            this.filePath = "${AppSetting.fileStoragePath}${CapData.rootNode?.packageName}/"
            CapData.packageName = event.packageName.toString()
            val file = File(this.filePath)
            if (!file.exists()) file.mkdirs()
            CapData.currentState = CapData.RUNING
            foreachNode(rootInActiveWindow)
            mapInfoList.forEach {
                saveIconFile(it)
            }
            println(mapInfoList.toString())
//            saveBitmapFile(CapData.imgBit, CapturedImageConfig.capStoragePath + "source.png")
//                println(rect.toString())
        }*/


     if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (AccessibilityConfig.packages.contains(event.packageName)) {
                    println("匹配到包名")
                    foreachNode(rootInActiveWindow, event)
                }
            }



 if (event?.eventType == AccessibilityEvent.TYPE_GESTURE_DETECTION_END) {
          foreachNode(rootInActiveWindow)
          println("正在保存")
      }*/


/*if (node.className.contains("android.widget.ImageView")) {
    println(node)
    if(node.contentDescription.contains("tfffaaaa")){

        val outBound = Rect()
        node.getBoundsInScreen(outBound)

        val map = HashMap<String, String>()
//                outBound
        map["state"] = "0"
        map["x"] = outBound.centerX().toString()
        map["y"] = outBound.centerY().toString()
        map["w"] = outBound.width().toString()
        map["h"] = outBound.height().toString()
//                println(map.toString())
        //sendActivityMsg(map)
    }

//            println("${outBound.width()}    ${outBound.height()}")
    *//*val bitmap = node.liveRegion.toDrawable().toBitmap(outBound.width(), outBound.height())
            saveBitmapFile(bitmap,(0..100).random().toString())
            callback(bitmap)*//*
        }*/