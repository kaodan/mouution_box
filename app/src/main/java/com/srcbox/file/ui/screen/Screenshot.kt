package com.srcbox.file.ui.screen

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder


class Screenshot(private val activity: Activity) {
    private var serviceConnection: ServiceConnection? = null
    var screenServicesBinder: ScreenServices.ScreenBind? = null

    companion object {
        const val TYPE_FLOAT_COLOR = 1
        const val TYPE_SCREEN = 2
        var type = 0
    }


    init {
        serviceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                service as ScreenServices.ScreenBind
                screenServicesBinder = service
            }
        }
        val intent = Intent(activity, ScreenServices::class.java)
        activity.bindService(
            intent,
            serviceConnection as ServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun bindWith(): ScreenServices.ScreenBind? {
        return screenServicesBinder?.with(activity)
    }

    fun screen(callback: (bit: Bitmap) -> Unit) {
        bindWith()?.screenshot(callback)
    }

    fun show() {
        bindWith()?.show()
    }

    fun release() {
        serviceConnection?.let { activity.unbindService(it) }
    }
}