package com.srcbox.file.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PackageReceiver: BroadcastReceiver(){

    companion object {
        var selPackageName = ""
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.dataString
        if (intent!!.action == Intent.ACTION_PACKAGE_REMOVED){
            if (packageName == selPackageName){

            }
        }
    }

}