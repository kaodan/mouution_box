package com.srcbox.file.ui.popup

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import com.lxj.xpopup.core.CenterPopupView
import com.qmuiteam.qmui.layout.QMUILayoutHelper
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUILangHelper
import com.srcbox.file.R
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.SettingActivity
import com.srcbox.file.ui.popup.SignAProtocolPopup.Companion.TYPE_USER
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.SpTool
import kotlinx.android.synthetic.main.popup_sign_a_protocol.view.*
import kotlin.system.exitProcess

class SignAProtocolPopup(context: Context) : CenterPopupView(context) {
    companion object {
        const val TYPE_USER = 0
        const val TYPE_PRIVATE = 1
    }

    var XY_TYPE: Int = -1


    override fun onCreate() {
        super.onCreate()
        when (XY_TYPE) {
            TYPE_USER -> {
                yes.visibility = View.GONE
                end.visibility = View.GONE
                protocolContent.text =
                    Html.fromHtml(EggIO.readFile(context.assets.open("html/userXy.html")))
                return
            }

            TYPE_PRIVATE -> {
                yes.visibility = View.GONE
                end.visibility = View.GONE
                protocolContent.text =
                    Html.fromHtml(EggIO.readFile(context.assets.open("html/privacyXy.html")))
                return
            }
        }

        EggUtil.loadIcon(context, AppSetting.colorGray, back)
        signAProtocol.radius = QMUIDisplayHelper.dp2px(context, 15)
        yes.radius = QMUILayoutHelper.RADIUS_OF_HALF_VIEW_HEIGHT
        setProtocolContent()
        back.setOnClickListener {
            it.visibility = View.GONE
            setProtocolContent()
        }

        yes.setOnClickListener {
            SpTool.putSettingString("isProtocol", true.toString())
            dismiss()
        }

        end.setOnClickListener {
            exitProcess(0)
        }
    }

    private fun setProtocolContent() {
        val content = """
尊敬的用户，您好!感谢您对山盒的信赖!我们依据最新的监管要求更新了《用户服务协议》和《隐私政策》,请务必审慎阅读所有条款,尤其是:

1、我们对您的个人信息的收集/保存/使用/对外提供/保护等规则，以及您的用户权利等条款;

2、与您约定免除或限制责任的条款;3、与您约定法律适用和管辖的条款;4、其他以颜色或粗体进行标识的重要条款。

您点击“同意”即表示您已阅读完毕并同意以上协议的全部内容。
        """.trimIndent()
        val userServeStart = content.indexOf("《用户")
        val userServeEnd = content.indexOf("协议》") + 3
        val privacyStart = content.indexOf("《隐")
        val privacyEnd = content.indexOf("策》") + 2
        protocolContent.text = Html.fromHtml(content)
        val spannableString = SpannableString(content)

        val userClickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                back.visibility = View.VISIBLE
                protocolContent.text =
                    Html.fromHtml(EggIO.readFile(context.assets.open("html/userXy.html")))
//                widget.setBackgroundColor(Color.RED)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor(AppSetting.colorStress)
                ds.isUnderlineText = false
            }
        }

        val privacyClickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                back.visibility = View.VISIBLE
                protocolContent.text =
                    Html.fromHtml(EggIO.readFile(context.assets.open("html/privacyXy.html")))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor(AppSetting.colorStress)
                ds.isUnderlineText = false
            }
        }

        spannableString.setSpan(
            userClickSpan,
            userServeStart,
            userServeEnd,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )

        spannableString.setSpan(
            privacyClickSpan,
            privacyStart,
            privacyEnd,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
//        spannableString.setSpan(clickSpan, 0, 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        protocolContent.movementMethod = LinkMovementMethod.getInstance()
        protocolContent.text = spannableString
    }

    override fun getImplLayoutId(): Int {
        return R.layout.popup_sign_a_protocol
    }
}