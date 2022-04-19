package com.srcbox.file.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lxj.xpopup.XPopup
import com.srcbox.file.R
import com.srcbox.file.util.GlobUtil
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

class PlayVideoActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        GlobUtil.changeTitle(this)
        val asLoading = XPopup.Builder(this).asLoading()
        asLoading.show()
        val webView = findViewById<WebView>(R.id.video_web)
        webView.settings.javaScriptEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.blockNetworkImage = false
        webView.settings.blockNetworkLoads = false
        webView.settings.domStorageEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.setGeolocationEnabled(true)
        webView.settings.setSupportZoom(true)
        webView.loadUrl(intent.getStringExtra("url"))
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(p0: WebView?, p1: String?) {
                super.onPageFinished(p0, p1)
                asLoading.dismiss()
            }

            override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                super.onPageStarted(p0, p1, p2)
            }
        }
    }
}