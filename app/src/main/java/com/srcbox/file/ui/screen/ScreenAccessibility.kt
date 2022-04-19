package com.srcbox.file.ui.screen

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.srcbox.file.data.`object`.CapData
import com.srcbox.file.data.`object`.CapturedImageConfig

class ScreenAccessibility : AccessibilityService() {
    companion object {
        var mRootInActiveWindow: AccessibilityNodeInfo? = null
        var isAccessibility = false
        var isWork = false
    }

    override fun onCreate() {
        super.onCreate()
        isAccessibility = true
        CapturedImageConfig.isServerPermission = true
        CapturedImageConfig.isServerStart = true
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        CapData.rootNode = rootInActiveWindow
        if (!isWork) {
            mRootInActiveWindow = rootInActiveWindow
        }
    }
}