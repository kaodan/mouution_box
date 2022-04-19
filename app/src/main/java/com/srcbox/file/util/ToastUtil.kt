package com.srcbox.file.util

import android.app.Activity
import android.content.Context
import android.widget.Toast

class ToastUtil(private val activity: Context) {
    fun longShow(content: String) {
        Toast.makeText(activity, content, Toast.LENGTH_LONG).show()
    }

    fun shortShow(content: String) {
        Toast.makeText(activity, content, Toast.LENGTH_SHORT).show()
    }
}