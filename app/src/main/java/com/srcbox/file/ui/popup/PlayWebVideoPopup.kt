package com.srcbox.file.ui.popup

import android.annotation.SuppressLint
import android.content.Context
import com.lxj.xpopup.core.CenterPopupView
import com.srcbox.file.R
import com.tencent.smtt.sdk.WebView

class PlayWebVideoPopup(context: Context) : CenterPopupView(context) {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate() {
        super.onCreate()
        val webView = findViewById<WebView>(R.id.video_web)
        webView.settings.javaScriptEnabled = true;
        webView.settings.useWideViewPort = true;
        webView.settings.loadWithOverviewMode = true;
        webView.settings.blockNetworkImage = false;
        webView.settings.blockNetworkLoads = false;
        webView.settings.domStorageEnabled = true;
        webView.settings.builtInZoomControls = true;
        webView.settings.displayZoomControls = false;
        webView.settings.setGeolocationEnabled(true);
        webView.settings.setSupportZoom(true);
        webView.loadUrl("https://trace.moe/preview.php?anilist_id=1588&file=[HKG][Megadere][13][BIG5][DVDRIP][1024x576][H264_AAC].mp4&t=772.58&token=m_-BCQiwn50UIKx7iGrmOg")
    }

    override fun getImplLayoutId(): Int {
        return R.layout.play_web_video
    }
}