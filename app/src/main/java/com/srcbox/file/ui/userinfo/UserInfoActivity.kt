package com.srcbox.file.ui.userinfo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import cn.leancloud.AVException
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import cn.leancloud.types.AVNull
import com.alibaba.fastjson.JSON
import com.lxj.xpopup.XPopup
import com.srcbox.file.R
import com.srcbox.file.application.EggApplication
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.fragment.main_pager.FragmentMe
import com.srcbox.file.util.*
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlin.concurrent.thread

class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        GlobUtil.changeTitle(this)
        val mTencent = Tencent.createInstance(AppSetting.QQ_APP_ID, this)

        AVUser.currentUser()?.getJSONObject("authData")?.let {
            if (it.isNotEmpty()) {
                qqT.text =
                    "解除QQ绑定"
            } else {
                qqT.text =
                    "绑定QQ"
            }
        }
        nameT.text = AVUser.currentUser().username
        if (AVUser.currentUser().email != null) {
            emailT.text = AVUser.currentUser().email
        } else {
            emailT.text = "点我绑定邮箱！"
        }

        name.setOnClickListener {
            XPopup.Builder(this).asInputConfirm("输入新昵称", null) {
                val avUser = AVUser.currentUser()
                avUser.username = it
                avUser.saveInBackground().subscribe(object : Observer<AVObject> {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(t: AVObject) {
//                        ToastUtil(this@UserInfoActivity).longShow("成功")
                        nameT.text = it
                    }

                    override fun onError(e: Throwable) {
                        println(e.message)
                        ToastUtil(this@UserInfoActivity).longShow("${e.message}")
                    }
                })
            }.show()
        }

        email.setOnClickListener {
            if (AVUser.currentUser().email != null) {
                XPopup.Builder(this).asCustom(UserInfoEmailPopup(this, emailT)).show()
            } else {
                bindEmail()
            }
        }


        pass.setOnClickListener {
            if (AVUser.currentUser().email == null) {
                XPopup.Builder(this).asConfirm("温馨提示", "您必须先绑定邮箱！") {
                    bindEmail()
                }
                return@setOnClickListener
            }
            XPopup.Builder(this).asInputConfirm(
                "修改密码", null, "输入绑定邮箱"
            ) { text ->
                try {
                    AVUser.requestPasswordResetInBackground(text)
                        .subscribe(object : Observer<AVNull> {
                            override fun onComplete() {
                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onNext(t: AVNull) {
                                EggUtil.toast("已发送")
                            }

                            override fun onError(e: Throwable) {
                                EggUtil.toast(e.message!!)
                            }

                        })
                } catch (e: AVException) {
                }

            }.show()
        }

        qq.setOnClickListener {
            AVUser.currentUser()?.getJSONObject("authData")?.let {

                XPopup.Builder(this).asConfirm("温馨提示", "您确定要解绑QQ吗") {
                    if (it.isNotEmpty()) {
                        if (AVUser.currentUser().email == null) {
                            EggUtil.toast("未绑定邮箱，不能解绑QQ")
                            return@asConfirm
                        }
                        mTencent.logout(this)
                        AVUser.currentUser()?.dissociateWithAuthData("qq")
                            ?.subscribe(object : Observer<AVUser> {
                                override fun onComplete() {
                                }

                                override fun onSubscribe(d: Disposable) {
                                }

                                @SuppressLint("CheckResult")
                                override fun onNext(t: AVUser) {
                                    println("解除成功")
                                    qqT?.text = "绑定QQ"
                                    t.fetchIfNeededInBackground("authData").subscribe {
                                        println("刷新成功$it")
                                    }
                                }


                                override fun onError(e: Throwable) {
                                    println("解除失败${e.message}")
                                    e.message?.let { it1 ->
                                        ToastUtil(this@UserInfoActivity).longShow(
                                            it1
                                        )
                                    }
                                }

                            })
                        return@asConfirm
                    } else {
                        mTencent.login(this, "all", QQLoginUiListener())
                    }
                }.show()


            }

        }


        Member.getVipDate {
            when (it) {
                "0" -> {
                    vipT.text = "普通用户"
                }

                "-1" -> {
                    vipT.text = "永久VIP用户"
                }

                else -> {
                    vipT.text = it
                }
            }
        }
    }


    private fun bindEmail() {
        XPopup.Builder(this).asInputConfirm("绑定新邮箱", null) {
            val avUser = AVUser.currentUser()
            avUser.email = it
            avUser.saveInBackground().subscribe(object : Observer<AVObject> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: AVObject) {
                    emailT.text = it
                    thread {
                        AVUser.requestEmailVerifyInBackground(it).blockingSubscribe()
                    }
                }

                override fun onError(e: Throwable) {
                    println(e.message)
                    ToastUtil(this@UserInfoActivity).longShow("${e.message}")
                }
            })
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_LOGIN -> {
                println("QQ登录已返回")
                Tencent.onActivityResultData(
                    requestCode, resultCode, data, QQLoginUiListener()
                )
            }
        }
    }

    inner class QQLoginUiListener : IUiListener {
        override fun onComplete(p0: Any) {
            val json = JSON.parseObject(p0.toString())

            LeanQQUtil.result(json, true, object : Observer<AVUser> {
                override fun onComplete() {
                    println("QQ登录完成")
                }

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: AVUser) {
                    println("QQ登录成功了")
                    qqT.text =
                        "解除QQ绑定"
//                    t.getAVObject<S>()
                }

                override fun onError(e: Throwable) {
                    if (AVException(e).code == 137) {
                        ToastUtil(EggApplication.context).longShow("此QQ已绑定")
                    } else {
                        ToastUtil(EggApplication.context).longShow("QQ登录发送错误${e.message}")
                    }
                }
            })
        }

        override fun onCancel() {
            ToastUtil(EggApplication.context).longShow("QQ登录已关闭")

        }

        override fun onWarning(p0: Int) {

        }

        override fun onError(p0: UiError?) {
            ToastUtil(EggApplication.context).longShow("QQ登录发送错误${p0?.errorMessage}")
        }
    }
}