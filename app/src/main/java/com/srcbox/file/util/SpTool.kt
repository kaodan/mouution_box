package com.srcbox.file.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

object SpTool {
    private var context: Context? = null
    private fun getSp(fileName: String): SharedPreferences {
        return context!!.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    fun setContext(context: Context) {
        this.context = context
    }

    @SuppressLint("CommitPrefEdits")
    fun putString(fileName: String, key: String, value: String) {
        val edit = getSp(fileName).edit()
        edit.putString(key, value)
        edit.apply()
    }


    fun getString(fileName: String, key: String, defValue: String): String? {
        return getSp(fileName).getString(key, defValue)
    }

    fun putSettingString(key: String, value: String) {
        val edit = getSp("setting").edit()
        edit.putString(key, value)
        edit.apply()
    }

    fun getSettingString(key: String, dafValue: String): String {
        return getSp("setting").getString(key, dafValue)!!
    }

}