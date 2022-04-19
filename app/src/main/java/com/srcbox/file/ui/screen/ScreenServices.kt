package com.srcbox.file.ui.screen

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.*
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.srcbox.file.ui.screen.popup.ScreenPopup
import com.srcbox.file.ui.screen.ScreenEmptyFragment.Companion.EMPTY_FRAGMENT_TAG
import com.lxj.xpopup.XPopup
import com.srcbox.file.R
import com.srcbox.file.ui.MainActivity


class ScreenServices : Service() {
    companion object {
        var screenServices: ScreenServices? = null
    }

    private val screenNotificationId = "SCREEN_NOTIFICATION"

    //    private var screenNotificationNumberId = 0
    private val mBinder = ScreenBind()
    private var notificationManager: NotificationManager? = null

    inner class ScreenBind : Binder() {
        private var winLayPar: WindowManager.LayoutParams? = null
        var winManager: WindowManager? = null
        var view: View? = null
        private var activity: Activity? = null
        private var emptyFragment: ScreenEmptyFragment? = null
        private var layoutInflater: LayoutInflater? = null
        var supportFragmentManager: FragmentManager? = null
        fun with(activity: Activity): ScreenBind {
            this.activity = activity
            createEmptyFragment()
            return this
        }

        private fun createEmptyFragment() {
            supportFragmentManager = (activity as FragmentActivity).supportFragmentManager
            val extensionFragment = supportFragmentManager?.findFragmentByTag(EMPTY_FRAGMENT_TAG)
            val fragment = if (extensionFragment != null) {
                extensionFragment as ScreenEmptyFragment
            } else {
                val emptyFragment =
                    ScreenEmptyFragment()
                supportFragmentManager?.beginTransaction()?.add(
                    emptyFragment,
                    EMPTY_FRAGMENT_TAG
                )?.commitNow()
                emptyFragment
            }

            this.emptyFragment = fragment
        }


        fun cancelEmptyFragment() {
            println("已删除S")
            emptyFragment?.let {
                supportFragmentManager?.beginTransaction()?.remove(it)
            }


        }

        fun screenshot(callback: (b: Bitmap) -> Unit) {
            emptyFragment?.screenshot {
                callback(it)
            }
        }

        fun show() {
            emptyFragment?.float {
                activity?.let {

                    showFloat()
                }
            }
        }


        private fun showFloat() {
            if (winManager != null) {
                return
            }
            winManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            winLayPar = WindowManager.LayoutParams()
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            view = layoutInflater?.inflate(R.layout.screen_float, null)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                winLayPar?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                winLayPar?.type = WindowManager.LayoutParams.TYPE_PHONE
            }

            winLayPar?.flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            winLayPar?.width = WindowManager.LayoutParams.WRAP_CONTENT
            winLayPar?.height = WindowManager.LayoutParams.WRAP_CONTENT
            winLayPar?.format = PixelFormat.RGBA_8888
            winLayPar?.gravity = Gravity.START or Gravity.TOP
            winManager?.addView(view, winLayPar)
            view?.setOnClickListener { v ->
                v.visibility = View.GONE
                emptyFragment?.screenshot { bitmap ->
                    v.visibility = View.VISIBLE
                    activity?.let {
                        val screenPopup = ScreenPopup(it)
                        screenPopup.setBitmap(bitmap)
                        XPopup.Builder(it)
                            .enableShowWhenAppBackground(true)
                            .enableDrag(false)
                            .asCustom(screenPopup)
                            .show()
                    }
                }
            }

            view?.setOnTouchListener(FloatingOnTouchListener())
        }

        private inner class FloatingOnTouchListener : View.OnTouchListener {
            private var x = 0
            private var y = 0
            private var startTime = 0L
            private var isClick = false

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(view: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isClick = false
                        startTime = System.currentTimeMillis()
                        x = event.rawX.toInt()
                        y = event.rawY.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        isClick = true
                        val nowX = event.rawX.toInt()
                        val nowY = event.rawY.toInt()
                        val movedX = nowX - x
                        val movedY = nowY - y
                        x = nowX
                        y = nowY
                        winLayPar?.x = winLayPar?.x?.plus(movedX)
                        winLayPar?.y = winLayPar?.y?.plus(movedY)
                        winManager?.updateViewLayout(view, winLayPar)
                    }

                    MotionEvent.ACTION_UP -> {
                        isClick = (System.currentTimeMillis() - startTime) > 0.1 * 1000L
                    }
                    else -> {
                    }
                }
                return isClick
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        screenServices = this
        createNotificationChannel()
    }

/*    override fun onDestroy() {
        mBinder.view?.visibility = View.GONE
        mBinder.winManager = null
        mBinder.cancelEmptyFragment()
        super.onDestroy()
//        stopForeground(true)
    }*/

    private fun createNotificationChannel() {
        val builder: Notification.Builder =
            Notification.Builder(this.applicationContext) //获取一个Notification构造器
        val nfIntent = Intent(this, MainActivity::class.java) //点击后跳转的界面，可以设置跳转数据
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.mipmap.ic_launcher
                )
            )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText("山盒正在运行中...")
            .setWhen(System.currentTimeMillis())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(screenNotificationId)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                screenNotificationId,
                "notification_name",
                NotificationManager.IMPORTANCE_LOW
            )

//            screenNotificationNumberId
            notificationManager?.createNotificationChannel(channel)
        }

        val notification: Notification = builder.build() // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND //设置为默认的声音
        startForeground(110, notification)
    }
}