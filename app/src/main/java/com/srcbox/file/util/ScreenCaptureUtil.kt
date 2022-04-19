package com.srcbox.file.util


import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.`object`.CapData
import com.srcbox.file.data.`object`.CapturedImageConfig
import com.srcbox.file.data.`object`.ScreenCaptureInfo
import com.srcbox.file.service.ShadeService
import com.srcbox.file.ui.MainActivity
import java.io.File
import java.lang.Exception


class ScreenCaptureUtil : Service() {

    private val winPoint = Point()
    private var mediaProjection: MediaProjection? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel()
        }
        ScreenCaptureInfo.screenCaptureUtilInterface = this
        return super.onStartCommand(intent, flags, startId)
    }

    fun startCapture(
        activity: Activity,
        resultCode: Int,
        intentData: Intent,
        callbacks: (bitmap: Bitmap) -> Unit
    ) {
        try {
//            CameraCharacteristics
            val c = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val windowManager =
                (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            windowManager.defaultDisplay.getRealSize(winPoint)

            val mediaProjectionManager =
                getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val imageReader =
                ImageReader.newInstance(
                    getScreenWidth(),
                    getScreenHeight(),
                    PixelFormat.RGBA_8888,
                    1
                )
            if (mediaProjection == null) {
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, intentData)
            }

            mediaProjection?.createVirtualDisplay(
                "screen-mirror",
                getScreenWidth(),
                getScreenHeight(),
                Resources.getSystem().displayMetrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.surface,
                null,
                null
            )
            var screenStop = false
            var image: Image? = null
            Thread {
                while (true) {
                    if (screenStop) {
                        break
                    }
                    image = imageReader.acquireLatestImage()
                    if (image != null) {
                        Thread.sleep(1000)
                        callbacks(covetBitmap(image!!)!!)
                        break
                    }
                }
            }.start()

            Handler().postDelayed({
                image?.let {
                    screenStop = true
                }
            }, 5000)

        } catch (e: SecurityException) {
            readyCapture(
                activity,
                ScreenCaptureInfo.CODE
            )
            EggUtil.toast("请授予截屏权限")
        }
    }


    private fun createNotificationChannel() {
        val builder: Notification.Builder =
            Notification.Builder(this.applicationContext) //获取一个Notification构造器
        val nfIntent = Intent(
            this,
            MainActivity::class.java
        ) //点击后跳转的界面，可以设置跳转数据
        builder.setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                nfIntent,
                0
            )
        ) // 设置PendingIntent
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.mipmap.ic_launcher
                )
            ) // 设置下拉列表中的图标(大图标)
//.setContentTitle("SMI InstantView") // 设置下拉列表里的标题
            .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
            .setContentText("is running......") // 设置上下文内容
            .setWhen(System.currentTimeMillis()) // 设置该通知发生的时间
        /*以下是对Android 8.0的适配*/ //普通notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id")
        }
        //前台服务notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                "notification_id",
                "notification_name",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification: Notification = builder.build() // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND //设置为默认的声音
        startForeground(110, notification)
    }

    private fun covetBitmap(image: Image): Bitmap? {
        val width = image.width
        val height = image.height
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * width
        var bitmap =
            Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            )
        bitmap.copyPixelsFromBuffer(buffer)
        bitmap =
            Bitmap.createBitmap(bitmap, 0, 0, width, height)
        image.close()
        return bitmap
    }

    private fun getScreenWidth(): Int {
        return winPoint.x
    }

    private fun getScreenHeight(): Int {
        return winPoint.y
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private var filePath: File = File("")
        private var mapInfoList = ArrayList<HashMap<String, String>>()
        fun readyCapture(activity: Activity, CODE: Int) {
            val mediaProjectionManager =
                activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            activity.startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                CODE
            )
        }

        private fun initAccessibilityConfig() {
            CapturedImageConfig.classNames.add("android.widget.Image")
            CapturedImageConfig.classNames.add("android.widget.ImageView")
            CapturedImageConfig.classNames.add("android.widget.ImageButton")
        }

        fun initAccessibilityPermissions(context: Context): Boolean {
            initAccessibilityConfig()
            /*return if (!CapturedImageConfig.isServerStart) {*/
            EggUtil.toast("请授予无障碍权限")
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            /*   false
           } else {
               true
           }*/
            return true
        }

        fun getScreenImage(
            activity: Activity,
            bitmap: Bitmap,
            view: View
        ) {
            CapData.arrayRectList.clear()

            mapInfoList = ArrayList<HashMap<String, String>>()
            try {
                val packageManager = activity.packageManager
                val applicationInfo =
                    packageManager.getApplicationInfo(CapData.rootNode?.packageName.toString(), 0)
                val appName = packageManager.getApplicationLabel(applicationInfo)
                this.filePath = File(
                    AppStorageData.getFileOutFile(),
                    "$appName/page"
                )
            } catch (e: Exception) {
                activity.runOnUiThread {
                    EggUtil.toast("截图失败")
                }
            }


            if (!filePath.exists()) filePath.mkdirs()
            Thread {
                foreachNode(CapData.rootNode)

                when (CapData.type) {
                    CapData.TYPE_SHOW -> {
                        activity.runOnUiThread {
                            Handler().postDelayed({
                                view.visibility = View.VISIBLE
                            }, 500)
                            CapData.bitmap = bitmap
                            activity.startService(Intent(activity, ShadeService::class.java))
                        }
                    }

                    CapData.TYPE_NO_SHOW -> {
                        mapInfoList.forEach {

                            saveIconFile(it, bitmap)
                        }
                        if (mapInfoList.size == 0) {
                            activity.runOnUiThread {
                                EggUtil.toast("当前页面无资源，请尝试可视化抓捕。")
                                view.visibility = View.VISIBLE
                            }
                            return@Thread
                        }
                        bitmap.recycle()
                        activity.runOnUiThread {
                            EggUtil.toast("抓捕成功，路径：${this.filePath.absolutePath}")
                            Handler().postDelayed({
                                view.visibility = View.VISIBLE
                            }, 500)
                        }
                    }
                }
            }.start()
        }

        private fun foreachNode(node: AccessibilityNodeInfo?) {

            node?.let {
                var isI = false
                when (CapData.type) {
                    CapData.TYPE_SHOW -> {
                        isI = true
                    }

                    CapData.TYPE_NO_SHOW -> {
                        isI = CapturedImageConfig.classNames.contains(it.className)
                    }
                }

                if (isI) {
                    println(it.className)
                    val rect = Rect()
                    CapData.arrayRectList.add(it)
                    it.getBoundsInScreen(rect)
                    val map = HashMap<String, String>()
                    map["state"] = "cut"
                    map["package"] = node.packageName.toString()
                    map["x"] = rect.left.toString()
                    map["y"] = rect.top.toString()
                    map["w"] = rect.width().toString()
                    map["h"] = rect.height().toString()
                    mapInfoList.add(map)
                }
                for (i in 0 until it.childCount) {
                    foreachNode(it.getChild(i))
                }
            }
        }

        private fun saveIconFile(map: HashMap<String, String>, bitmap: Bitmap): String {
            val bit = EggUtil.cutBitmap(
                bitmap,
                map["x"]!!.toInt(),
                map["y"]!!.toInt(),
                map["w"]!!.toInt(),
                map["h"]!!.toInt()
            ) ?: return ""
            val randNum = (11111..99449).random() + ((22222..93333).random())
            val f = File(filePath, "${System.currentTimeMillis()}-$randNum.png")
            EggUtil.saveBitmapFile(
                bit, f
            )
            return f.path
        }
    }
}

/*val intent = Intent(activity, com.srcbox.file.ui.extractManager.activity.MainActivity::class.java)
                         intent.addCategory(Intent.CATEGORY_LAUNCHER)
                         intent.action = Intent.ACTION_MAIN
                         intent.flags =
                             Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                         activity.startActivity(intent)*/