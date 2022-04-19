package com.srcbox.file.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.srcbox.file.util.ToastUtil

class ScreenEmptyFragment : Fragment() {

    companion object {
        const val EMPTY_FRAGMENT_TAG = "ScreenEmptyfragment"
        const val REQUEST_SCREENSHOT_CODE = 1
        const val REQUEST_FLOAT_CODE = 2
        const val REQUEST_ACCESSIBILITY_CODE = 3
    }

    private var performCode: (() -> Unit)? = null
    private var callback: ((Bitmap) -> Unit)? = null
    private var mediaProjection: MediaProjection? = null
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaIntentData: Intent? = null
    private var imageReader: ImageReader? = null
    private var windowsManager: WindowManager? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_SCREENSHOT_CODE -> {
                onScreenBitmap(resultCode, data)
            }

            REQUEST_FLOAT_CODE -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(context)) {
                        requestAccessibilityPermission()
                    } else {
                        Toast.makeText(activity, "授权失败，请重试。", Toast.LENGTH_LONG).show()
                    }
                }
            }

            REQUEST_ACCESSIBILITY_CODE -> {
                requestAccessibilityPermission()
            }
        }
    }


    private fun onScreenBitmap(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            var isGetImageReaderOnce = false
            val winPoint = Point()
            windowsManager =
                context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowsManager?.defaultDisplay!!.getRealSize(winPoint)

            val width = winPoint.x
            val height = winPoint.y
            imageReader =
                ImageReader.newInstance(
                    width,
                    height,
                    PixelFormat.RGBA_8888,
                    1
                )

            data?.let {
                mediaIntentData = data
                mediaProjection =
                    mediaProjectionManager?.getMediaProjection(resultCode, it)
            }


            //创建一个虚拟屏幕用来装截图
            mediaProjection?.createVirtualDisplay(
                "screen-mirror",
                width,
                height,
                Resources.getSystem().displayMetrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader?.surface,
                null,
                null
            )


            imageReader?.setOnImageAvailableListener({
                try {

                    if (!isGetImageReaderOnce) {
                        isGetImageReaderOnce = true
                        val image = it.acquireLatestImage()
                        val b = covetBitmap(image)
                        mediaProjection?.stop()

                        if (callback == null) {
                            return@setOnImageAvailableListener
                        }
                        callback?.let {
                            if (b != null) {
                                it(b)
                            }
                        }
                    }
                } catch (e: IllegalStateException) {
                    Toast.makeText(activity, "截图失败，请重试。", Toast.LENGTH_LONG).show()
                    println(e.message)
                }
            }, null)
        }
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
            Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
        image.close()
        return bitmap
    }

    private fun requestScreenPermissions() {
        if (mediaProjectionManager == null) {
            mediaProjectionManager =
                context?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        }
        startActivityForResult(
            mediaProjectionManager?.createScreenCaptureIntent(),
            REQUEST_SCREENSHOT_CODE
        )
    }

    fun screenshot(callback: (b: Bitmap) -> Unit) {
        this.callback = callback
        onActivityResult(REQUEST_SCREENSHOT_CODE, Activity.RESULT_OK, mediaIntentData)
    }

    private fun requestAccessibilityPermission() {
        when (Screenshot.type) {
            Screenshot.TYPE_FLOAT_COLOR -> {
                requestScreenPermissions()
                performCode?.let { it() }
            }

            Screenshot.TYPE_SCREEN -> {
                activity?.let {
                    if (!ScreenAccessibility.isAccessibility) {
                        ToastUtil(it).longShow("请授予无障碍权限")
                        it.startActivityForResult(
                            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
                            REQUEST_ACCESSIBILITY_CODE
                        )
                    } else {
                        requestScreenPermissions()
                        performCode?.let { it() }
                    }
                }
            }
        }
    }

    fun float(performCode: () -> Unit) {
        this.performCode = performCode
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(context)) {
                requestAccessibilityPermission()
            } else {
                startActivityForResult(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context?.packageName}")
                    ), REQUEST_FLOAT_CODE
                )
            }
        }
    }
}


/*






 */