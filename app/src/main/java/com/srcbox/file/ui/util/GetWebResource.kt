package com.srcbox.file.ui.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.arialyy.aria.core.Aria
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
/*import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient*/
import kotlinx.android.synthetic.main.getwebresource_activity.*
import java.io.File
import java.net.URL

class GetWebResource : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.getwebresource_activity)
        GlobUtil.changeTitle(this)
//        EggUtil.toast(intent.getStringExtra("link")!!)
        val link = intent.getStringExtra("link")
        val webSetting = web_view.settings
//        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.javaScriptEnabled = true
        webSetting.useWideViewPort = true
        webSetting.loadWithOverviewMode = true
        webSetting.loadsImagesAutomatically = true
        webSetting.defaultTextEncodingName = "utf-8"
        webSetting.domStorageEnabled = true
        web_view.loadUrl(link?:"")
        val contentTypes = JSON.parseObject(EggIO.readFile(assets.open("json/content_type.json")))
        println("已运行")
        EggUtil.loadIcon(
            this,
            AppSetting.colorStress, download_icon, tit_back
        )
        EggUtil.loadIcon(this, "#ffffff", web_back, web_forward)
        tit_back.setOnClickListener {
            finish()
        }

        web_back.setOnClickListener {
            web_view.goBack()
        }

        web_forward.setOnClickListener {
            web_view.goForward()
        }
        var i = 0
        Settings.System.getString(contentResolver,Settings.System.ANDROID_ID)

        web_view.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {

            }

        })




        web_view.webViewClient = object : WebViewClient() {
            @SuppressLint("SetTextI18n")
            override fun shouldInterceptRequest(
                p0: WebView?,
                p1: String?
            ): WebResourceResponse? {
                val contentType = URL(p1).openConnection().contentType

                contentTypes.forEach {
                    if (it.value.toString() == contentType) {
                        val name =
                            "${EggUtil.getFileNameNoEx(getPathName(p1!!))}.${it.key}"
                        i++

                        runOnUiThread {
                            Aria.download(this@GetWebResource).load(p1)
                                .setFilePath(
                                    File(
                                        AppStorageData.getFileOutFile(),
                                        "网页资源/${URL(link).host}/$name"
                                    ).path
                                    , true
                                ).create()
                            download_num.text = "${i}个"
                        }
                    }
                }
                return super.shouldInterceptRequest(p0, p1)
            }

            override fun onPageStarted(
                p0: WebView?,
                url: String?,
                favicon: Bitmap?
            ) {
                super.onPageStarted(p0, url, favicon)
                web_progress.visibility = View.VISIBLE
            }

            override fun onPageFinished(p0: WebView?, url: String?) {
                super.onPageFinished(p0, url)
                web_progress.visibility = View.GONE
                web_title.text = p0?.title
            }
        }

        web_view.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(p0: WebView?, newProgress: Int) {
                super.onProgressChanged(p0, newProgress)
                web_progress.progress = newProgress
            }
        }
    }

    private fun getPathName(path: String): String {
        return path.split("/").let {
            it[it.size - 1]
        }
    }
}


/*val view = LayoutInflater.from(this).inflate(R.layout.get_web_float_win, null)
        val fv = FloatWindow.get("web_get")
        if (fv == null){
            FloatWindow.with(applicationContext).setView(view)
                .setDesktopShow(false).setFilter(true, GetWebResource::class.java)
                .setViewStateListener(object : ViewStateListener {
                    override fun onBackToDesktop() {}

                    override fun onMoveAnimStart() {}

                    override fun onMoveAnimEnd() {}

                    override fun onPositionUpdate(p0: Int, p1: Int) {}

                    override fun onDismiss() {}

                    override fun onShow() {
                        EggUtil.loadIcon(
                            this@GetWebResource,
                            AppSetting.colorStress, view.findViewById(R.id.download_icon)
                        )
                    }

                    override fun onHide() {}
                }).setTag("web_get")
                .setMoveType(MoveType.slide).setMoveStyle(500, AccelerateInterpolator()).build()
        }*/

