package com.srcbox.file.ui.popup

import android.content.Context
import android.widget.TextView
import cn.leancloud.AVException
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.lxj.xpopup.core.CenterPopupView
import com.rengwuxian.materialedittext.MaterialEditText
import com.srcbox.file.R
import com.srcbox.file.ui.GetMemberActivity
import com.srcbox.file.util.EggUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import www.linwg.org.lib.LCardView

class TopUpRechargePopup(context: Context) : CenterPopupView(context) {
    override fun onCreate() {
        super.onCreate()
        findViewById<LCardView>(R.id.yes_on).setOnClickListener {
//            commit_member_text.text = "充值中"
            var isUseKey = false
            val keyQ = AVQuery<AVObject>("KeyPass")
            val keyPass = findViewById<MaterialEditText>(R.id.key_pass_edit).text.toString()
            val yesOnText = findViewById<TextView>(R.id.yes_on_text)
            yesOnText.text = "充值中"
            if (keyPass.isEmpty()) {
                EggUtil.toast("卡密不能为空")
                yesOnText.text = "充值"
                return@setOnClickListener
            }

            keyQ.whereEqualTo("key", keyPass)
            keyQ.firstInBackground.subscribe(object : Observer<AVObject> {
                override fun onComplete() {
                    println("完成")
                    if (!isUseKey) {
                        EggUtil.toast("卡密不存在")
                        yesOnText.text = "充值"
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    println("关联${d.isDisposed}")
                }

                override fun onNext(t: AVObject) {
                    isUseKey = true
                    val u = t.getJSONObject("user")
                    if (u != null) {
                        EggUtil.toast("卡密已被使用")
                        yesOnText.text = "充值"
                        return
                    }
                    t.put("user", AVUser.currentUser())
                    t.saveInBackground().subscribe(object : Observer<AVObject> {
                        override fun onComplete() {

                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(t: AVObject) {
                            EggUtil.toast("使用成功")
                            (context as GetMemberActivity).setUserInfo()
                            yesOnText.text = "充值"
                        }

                        override fun onError(e: Throwable) {
                            val ave = AVException(e)
                            EggUtil.toast("错误码：${ave.code.toString()}")
                            yesOnText.text = "充值"
                        }
                    })
                }

                override fun onError(e: Throwable) {
                    val ave = AVException(e)
                    EggUtil.toast("错误码：${ave.code.toString()}")
                    yesOnText.text = "充值"
                }
            })
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.top_up_recharge_popup
    }
}