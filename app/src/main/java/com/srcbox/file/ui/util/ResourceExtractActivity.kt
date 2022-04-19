package com.srcbox.file.ui.util

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.lxj.xpopup.XPopup
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.srcbox.file.R
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.ResourceData
import com.srcbox.file.ui.GetMemberActivity
import com.srcbox.file.ui.login.LoginActivity
import com.srcbox.file.ui.popup.WebResourceBoxPopup
import com.srcbox.file.util.*
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.activity_resource_extract.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import kotlin.concurrent.thread

class ResourceExtractActivity : AppCompatActivity() {
    private var currentTitle: String = ""
    private lateinit var webResourceBoxPopupCustom: WebResourceBoxPopup
    var currentUrl = ""
    private var needClearHistory = false
    private val cookies: HashMap<String, String> = HashMap()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource_extract)
        searchContainer.radius = QMUIDisplayHelper.dp2px(this, 10)
//        moreFun.radius = QMUILayoutHelper.RADIUS_OF_HALF_VIEW_HEIGHT
        webResourceBoxPopupCustom = WebResourceBoxPopup(this)
        val webResourceBoxPopup =
            XPopup.Builder(this).enableDrag(false).asCustom(webResourceBoxPopupCustom)
        webUrlEdit.setOnEditorActionListener { v, actionId, event ->
            val editStr = v.text.toString()
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                if (!editStr.contains("http")) {
                    loadWeb("https://www.baidu.com/s?wd=$editStr")

                } else {
                    loadWeb(editStr)
                }
            }
            v.text = ""
            true
        }

        bilibili.setOnClickListener {
            loadWeb("https://www.bilibili.com")
        }

        acfun.setOnClickListener {
            loadWeb("https://www.acfun.cn")
        }

        baidu.setOnClickListener {
            loadWeb("https://www.baidu.com")
        }

        tencentVideo.setOnClickListener {
            loadWeb("https://v.qq.com")
        }

        files.setOnClickListener {
            webResourceBoxPopup.show()
        }

        saveHtml.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val asLoading = XPopup.Builder(this@ResourceExtractActivity).asLoading()
                asLoading.show()
                val memberDate = Member.MemberData("0")
                when (Member.isVip(memberDate)) {
                    Member.UserType.TIME_LIMIT -> {
                        println("Current time ${memberDate.dateTime}")
                        saveHtml()
                    }

                    Member.UserType.ORDINARY -> {
                        EggUtil.toast("请充值")
                        startActivity(
                            Intent(
                                this@ResourceExtractActivity,
                                GetMemberActivity::class.java
                            )
                        )
                    }

                    Member.UserType.ALWAYS -> {
                        saveHtml()
                    }

                    Member.UserType.NO_LOG -> {
                        EggUtil.toast("请登录")
                        asLoading.delayDismiss(500)
                        startActivity(
                            Intent(
                                this@ResourceExtractActivity,
                                LoginActivity::class.java
                            )
                        )
                        return@launch
                    }

                    Member.UserType.ERROR -> {
                        EggUtil.toast("出现错误，请重试")
                    }

                    Member.UserType.NOTHING_CONNECT -> {
                        EggUtil.toast("网络无连接")
                    }
                }
                asLoading.delayDismiss(500)
            }
        }

//        moreFun.radius = QMUILayoutHelper.RADIUS_OF_HALF_VIEW_WIDTH
    }


    private fun saveHtml() {
        val asLoading = XPopup.Builder(this).asLoading()
        asLoading.show()
        val outFile = File(AppStorageData.getFileOutFile(), "资源嗅探/网页保存")
        println("当前链接是：$currentUrl")
        thread {
            WebAllPageSourceX(
                currentUrl,
                outFile,
                cookies
            ).getSource()
            runOnUiThread {
                asLoading.dismiss()
                XPopup.Builder(this).asConfirm("提示", "文件已保存在：$outFile", null).show()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentUrl == "") {
                needClearHistory = true
                finish()
            } else {
                if (!tenWeb.canGoBack()) {
                    needClearHistory = true
                    tenWebContainer.visibility = View.GONE
                    tenWeb.clearHistory()
                    moreFun.visibility = View.GONE
                    currentUrl = ""
//                    tenWeb.loadUrl("")
                } else {
                    tenWeb.goBack()
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadWeb(url: String) {
        moreFun.visibility = View.VISIBLE
        currentUrl = url.trim()
        tenWebContainer.visibility = View.VISIBLE
        val webSetting = tenWeb.settings
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.javaScriptEnabled = true
        webSetting.useWideViewPort = true
        webSetting.loadWithOverviewMode = true
        webSetting.loadsImagesAutomatically = true
        webSetting.defaultTextEncodingName = "utf-8"
        webSetting.domStorageEnabled = true
        tenWeb.loadUrl(currentUrl)

        tenWeb.webViewClient = object : WebViewClient() {

            override fun shouldInterceptRequest(
                p0: WebView?,
                p1: WebResourceRequest?
            ): WebResourceResponse? {
                p1?.let {
                    checkResource(it)
                }
                return null
            }


            override fun doUpdateVisitedHistory(p0: WebView?, p1: String?, p2: Boolean) {
                super.doUpdateVisitedHistory(p0, p1, p2)
                if (needClearHistory) {
                    needClearHistory = false
                    p0?.clearHistory()
                }
            }

            override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                super.onPageStarted(p0, p1, p2)
                if (p1 == "") {
                    tenWebContainer.visibility = View.GONE
                }

                tenWebProgress.visibility = View.VISIBLE
            }

            override fun onPageFinished(p0: WebView?, p1: String?) {
                super.onPageFinished(p0, p1)
                tenWebProgress.visibility = View.GONE

                p1?.let {
                    val cookieUrl = CookieManager.getInstance().getCookie(p1)
                    cookies[p1] = cookieUrl
                    println("Cookie：$cookieUrl")
                }
            }
        }

        tenWeb.webChromeClient = object : WebChromeClient() {

            override fun onReceivedTitle(p0: WebView?, p1: String?) {
                super.onReceivedTitle(p0, p1)
                p0?.let {
                    currentUrl = it.url
                }
                if (p1 != null) {
                    currentTitle = p1
                }
            }

            override fun onProgressChanged(p0: WebView?, p1: Int) {
                super.onProgressChanged(p0, p1)
                tenWebProgress.progress = p1
            }


        }

    }

    fun checkResource(webResourceRequest: WebResourceRequest) {
        val requestUrl = webResourceRequest.url.toString()
        var url = requestUrl
        val parseUrlUtil = ParseUrlUtil(requestUrl)
        val isBiliBili = parseUrlUtil.isBiliBili()
        if (isBiliBili) {
            val biliBiliVideo = parseUrlUtil.getBiliBiliVideo()
            biliBiliVideo?.let {
                url = it
            }
        }

        thread {
            try {
                val requestHeader = ParseUrlUtil(url).requestHeader()

                requestHeader?.let {
                    WebResourceBoxPopup.resourcesArrList.forEach {
                        if (it.url == url) {
                            return@thread
                        }
                    }

                    WebResourceBoxPopup.resourcesArrList.add(
                        ResourceData(
                            currentTitle,
                            url,
                            requestHeader
                        )
                    )
                }

            } catch (e: Exception) {
            }

        }
    }
}