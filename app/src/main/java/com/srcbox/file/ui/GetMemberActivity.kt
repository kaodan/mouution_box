package com.srcbox.file.ui

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cn.leancloud.AVException
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import cn.leancloud.codec.MD5
import com.alipay.sdk.app.PayTask
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.srcbox.file.R
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.data.PayMessage
import com.srcbox.file.payutil.OrderInfoUtil2_0
import com.srcbox.file.payutil.PayConfig
import com.srcbox.file.payutil.PayResult
import com.srcbox.file.ui.popup.TopUpRechargePopup
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import com.srcbox.file.util.Member
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_get_member.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class GetMemberActivity : AppCompatActivity() {

    companion object {
        const val MONTH = 0
        const val YEAR = 1
        const val PERPETUAL = 2
        const val TWO_DAY = 3
        var typeMember: Int? = null
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_member)
        GlobUtil.changeTitle(this)
        EggUtil.loadIcon(this, "#2196f3", pay_icon_1, pay_icon_2, pay_icon_3, pay_icon_4)

        pay_on_1.setOnClickListener {
            pay(PayMessage("2", "山盒月费", "山盒月费-b", OrderInfoUtil2_0.getOutTradeNo()))
            typeMember = MONTH
        }

        pay_on_2.setOnClickListener {
            pay(PayMessage("15", "山盒年费", "山盒年费-b", OrderInfoUtil2_0.getOutTradeNo()))
            typeMember = YEAR
        }

        pay_on_3.setOnClickListener {
            pay(PayMessage("30", "山盒永久", "山盒永久-b", OrderInfoUtil2_0.getOutTradeNo()))
            typeMember = PERPETUAL
        }

        pay_on_4.setOnClickListener {
            pay(PayMessage("0.2", "山盒2毛体验", "山盒2毛体验-b", OrderInfoUtil2_0.getOutTradeNo()))
            typeMember = TWO_DAY
        }

        top_up_recharge_on.setOnClickListener {
            XPopup.Builder(this).asCustom(TopUpRechargePopup(this)).show()
        }



        member_taoabo.setOnClickListener {
            val str = """
                fu植这行话${"$"}Pbry1lwcwy1${"$"}转移至👉τáǒЬáǒ👈【买对的 | 山盒 [安卓] 超强资源提取工具箱】；或https://m.tb.cn/h.VSEMifM?sm=8d0cde 點击鏈→接，再选择瀏lan嘂..大开
            """.trimIndent()
            EggUtil.copyText(this, str)
            EggUtil.toast("已复制淘口令，请打开淘宝。")
        }

        contact_qun.setOnClickListener {
            EggUtil.joinQQGroup(this, AppSetting.QUN_KEY)
        }

        contact_people.setOnClickListener {
            EggUtil.goQQ(this, "1970284668")
        }
        setUserInfo()
    }


    fun setUserInfo() {
        if (AVUser.currentUser() != null) {

            user_name.text = AVUser.currentUser().username
            user_state.visibility = View.VISIBLE
            user_state.text = "检测中"
            Member.getVipDate {
                when (it) {
                    "0" -> {
                        user_state.text = "普通用户"
                        user_state.setTextColor(Color.parseColor("#cccccc"))
                    }

                    "-1" -> {
                        user_state.text = "永久VIP用户"
                        user_state.setTextColor(Color.parseColor(AppSetting.colorStress))
                    }

                    else -> {
                        user_state.text = it
                        user_state.setTextColor(Color.parseColor(AppSetting.colorStress))
                    }
                }
            }
            val qq: String = AVUser.currentUser().email ?: return
            Glide.with(this).load(EggUtil.getQQIconLink(qq)).placeholder(R.mipmap.placeholder)
                .error(R.drawable.qq)
                .into(user_icon)
        }
    }

    private fun pay(payMessage: PayMessage) {
        val params: Map<String, String> =
            OrderInfoUtil2_0.buildOrderParamMap(
                PayConfig.APP_ID,
                true,
                payMessage
            )
        val orderParam: String = OrderInfoUtil2_0.buildOrderParam(params)
        val sign: String = OrderInfoUtil2_0.getSign(params, PayConfig.RSA2_PRIVATE, true)
        val orderInfo = "$orderParam&$sign"
        thread {
            val aliPay = PayTask(this)
            val result =
                aliPay.payV2(orderInfo, true)
            runOnUiThread {
                val payResult = PayResult(result)
                if (payResult.resultStatus == "9000") {
                    GlobalScope.launch(Dispatchers.Main) {
                        val asLoading = XPopup.Builder(this@GetMemberActivity).asLoading()
                        asLoading.show()
                        userAlterVip {
                            println("${it[0]} ${it[1]}")
                            asLoading.delayDismiss(500)
                            if (it[0] && it[1]) {
                                XPopup.Builder(this@GetMemberActivity)
                                    .asConfirm("提示", "付款成功", null).show()
                                setUserInfo()
                            } else {
                                if (it[2]) {
                                    XPopup.Builder(this@GetMemberActivity)
                                        .asConfirm("提示", "非法！！！", null).show()
                                    return@userAlterVip
                                }

                                if (!it[0] && !it[0]) {
                                    asLoading.delayDismiss(500)

                                    XPopup.Builder(this@GetMemberActivity)
                                        .asConfirm(
                                            "提示",
                                            "失败，您的单号是${payMessage.outTradeNo}，请加群联系管理员或者QQ1970284668解决\"",
                                            {
                                                EggUtil.joinQQGroup(
                                                    this@GetMemberActivity,
                                                    AppSetting.QUN_KEY
                                                )
                                            },
                                            {
                                                EggUtil.goQQ(this@GetMemberActivity, "1970284668")
                                            }).setCancelText("联系开发者").setConfirmText("加群求助").show()
                                } else if (!it[0]) {
                                    asLoading.delayDismiss(500)

                                    XPopup.Builder(this@GetMemberActivity)
                                        .asConfirm(
                                            "提示",
                                            "失败 make，您的单号是${payMessage.outTradeNo}，请加群联系管理员或者QQ1970284668解决\"",
                                            {
                                                EggUtil.joinQQGroup(
                                                    this@GetMemberActivity,
                                                    AppSetting.QUN_KEY
                                                )
                                            },
                                            {
                                                EggUtil.goQQ(this@GetMemberActivity, "1970284668")
                                            }).setCancelText("联系开发者").setConfirmText("加群求助").show()
                                } else if (!it[1]) {
                                    asLoading.delayDismiss(500)

                                    XPopup.Builder(this@GetMemberActivity)
                                        .asConfirm(
                                            "提示",
                                            "失败 use，您的单号是${payMessage.outTradeNo}，请加群联系管理员或者QQ1970284668解决\"",
                                            {
                                                EggUtil.joinQQGroup(
                                                    this@GetMemberActivity,
                                                    AppSetting.QUN_KEY
                                                )
                                            },
                                            {
                                                EggUtil.goQQ(this@GetMemberActivity, "1970284668")
                                            }).setCancelText("联系开发者").setConfirmText("加群求助").show()
                                }
                            }
                        }
                    }
                } else {
                    XPopup.Builder(this@GetMemberActivity)
                        .asConfirm("提示", "支付失败\n${payResult.resultStatus}", null).show()
                }
            }
        }
    }

    private suspend fun userAlterVip(onSuccess: (Array<Boolean>) -> Unit) {
        var subjectTime = 0
        val isSuccess = arrayOf(false, false, false)
        when (typeMember) {
            MONTH -> {
                subjectTime = 60 * 60 * 24 * 30
            }

            YEAR -> {
                subjectTime = 60 * 60 * 24 * 30 * 12
            }

            PERPETUAL -> {
                subjectTime = -1
            }

            TWO_DAY -> {
                subjectTime = 60 * 60 * 24
            }
            null -> {
                subjectTime = -2
            }
        }

        if (subjectTime == -2) {
            onSuccess(isSuccess)
            return
        }

        /* onSuccess(isSuccess)
         return*/

        return withContext(Dispatchers.IO) {
            val kA = AVObject("KeyPass")
            kA.put("key", MD5.computeMD5((0..10000).random().toString()) + "INAPPV01")
            kA.put("period", subjectTime.toString())
            kA.saveInBackground().subscribe(object : Observer<AVObject> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: AVObject) {
                    t.put("user", AVUser.currentUser())
                    t.saveInBackground().subscribe(object : Observer<AVObject> {
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: AVObject) {
                            isSuccess[0] = true
                            isSuccess[1] = true
                            isSuccess[2] = false
                            onSuccess(isSuccess)
                            println("${isSuccess[0]} ${isSuccess[1]}")
                        }

                        override fun onError(e: Throwable) {
                            val ave = AVException(e)
                            when (ave.code) {
                                401 -> {
                                    isSuccess[0] = true
                                    isSuccess[1] = false
                                    isSuccess[2] = true
                                }
                                else -> {
                                    isSuccess[0] = true
                                    isSuccess[1] = false
                                    isSuccess[2] = false
                                }
                            }

                            onSuccess(isSuccess)
                        }
                    })
                }

                override fun onError(e: Throwable) {
                    val ave = AVException(e)
                    when (ave.code) {
                        401 -> {
                            isSuccess[0] = false
                            isSuccess[1] = false
                            isSuccess[2] = true
                            onSuccess(isSuccess)
                        }

                        else -> {
                            isSuccess[0] = false
                            isSuccess[1] = false
                            isSuccess[2] = false
                        }
                    }
                }
            })
        }
    }
}