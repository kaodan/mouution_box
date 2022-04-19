package com.srcbox.file.ui.userinfo

import android.app.Activity
import android.content.Context
import android.widget.Button
import android.widget.TextView
import cn.leancloud.AVException
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.lxj.xpopup.core.CenterPopupView
import com.srcbox.file.R
import com.srcbox.file.util.EggIO
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.ToastUtil
import kotlinx.android.synthetic.main.activity_user_info.view.*
import kotlinx.android.synthetic.main.user_info_email_popup.view.*
import kotlinx.android.synthetic.main.user_info_email_popup.view.email
import kotlin.concurrent.thread

class UserInfoEmailPopup(context: Context, val emailTV: TextView) : CenterPopupView(context) {
    override fun onCreate() {
        super.onCreate()
        replace.setOnClickListener {
            val email = email.text.toString()
            val emailCode = emailCode.text.toString()
            it as Button
            it.text = "提交中..."
            if (email.isEmpty() || emailCode.isEmpty()) {
                it.text = "提交"
                ToastUtil(context).longShow("不能为空")
                return@setOnClickListener
            }
            thread {
                try {
                    val avEmailCode = AVQuery<AVObject>("emailCode")
                    avEmailCode.whereEqualTo("code", emailCode)
                    val first: AVObject? = avEmailCode.first
                    if (first == null) {
                        (context as Activity).runOnUiThread {
                            ToastUtil(context).longShow("更换码无效")
                            it.text = "提交"
                        }
                        return@thread
                    }
                    avEmailCode.first.delete()
                    val avUser = AVUser.currentUser()
                    avUser.email = email
                    avUser.save()
                    (context as Activity).runOnUiThread {
                        ToastUtil(context).longShow("已向您的新邮箱发送了一封邮件，请立即激活验证")
                        it.text = "提交"
                        emailTV.text = email
                        AVUser.requestEmailVerifyInBackground(email).subscribe { }
                        dismiss()
                    }
                } catch (e: AVException) {
                    (context as Activity).runOnUiThread {
                        ToastUtil(context).longShow("失败，请重试${e.message}")
                        it.text = "提交"
                    }
                }
            }
        }


        getReplaceCode.setOnClickListener {
            EggUtil.goQQ(context as Activity,"3441152376")
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.user_info_email_popup
    }
}