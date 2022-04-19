package com.srcbox.file.data.`object`

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.view.accessibility.AccessibilityNodeInfo

object CapData {
    const val OK = -1
    const val CANCELED = 0
    const val RUNING = 1
    var currentState = Activity.RESULT_CANCELED
    var isStart = false
    var activity: Activity? = null
    var intentData: Intent? = null
    var resultCode: Int = 0
    var rootNode: AccessibilityNodeInfo? = null
    const val TYPE_SHOW = 6
    const val TYPE_NO_SHOW = 7
    var type = TYPE_SHOW
    val arrayRectList = ArrayList<AccessibilityNodeInfo>()
    var bitmap: Bitmap? = null
}



/*

data class CapData(var activity: Activity) : Parcelable {
    constructor(source: Parcel) : this(
        source.readActivity()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int): Unit = with(dest) {
        writeActivity(activity)
    }

    private fun writeActivity(activity: Activity) {
        this.activity = activity
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CapData> = object : Parcelable.Creator<CapData> {
            override fun createFromParcel(source: Parcel): CapData = CapData(source)
            override fun newArray(size: Int): Array<CapData?> = arrayOfNulls(size)
        }
    }
}

private fun Parcel.readActivity(): Activity? {
    return null
}
*/
