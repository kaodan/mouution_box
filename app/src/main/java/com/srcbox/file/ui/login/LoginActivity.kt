package com.srcbox.file.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import cn.leancloud.AVException
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import cn.leancloud.codec.MD5
import cn.leancloud.types.AVNull
import com.alibaba.fastjson.JSON
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.lxj.xpopup.XPopup
import com.srcbox.file.R
import com.srcbox.file.application.EggApplication.Companion.userService
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.util.*
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    companion object {
        var userState: Int = 1
        const val USER_STATE_LOGIN = 1
        const val USER_STATE_REGISTER = 2
    }

    private var mTencent: Tencent? = null

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        GlobUtil.changeTitle(this)
//        Toast.makeText(this,"",Toast.LENGTH_SHORT)
        mTencent =
            Tencent.createInstance(AppSetting.QQ_APP_ID, this, "com.srcbox.file.fileProviders")

        qqLogin.setOnClickListener {
            mTencent?.login(this, "all", QQLoginUiListener())
        }

        forget_pass.setOnClickListener {
            XPopup.Builder(this).asInputConfirm(
                "忘记密码", null, "输入邮箱"
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

        user_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length > 6) {
                    Glide.with(this@LoginActivity).load("http://q1.qlogo.cn/g?b=qq&nk=${s}&s=640")
                        .placeholder(R.mipmap.placeholder)
                        .error(R.mipmap.error).into(user_qq_icon)
                }
            }
        })

        val emailSufixs = arrayOf(
            "@qq.com",
            "@163.com",
            "@126.com",
            "@gmail.com",
            "@sina.com",
            "@hotmail.com",
            "@yahoo.cn",
            "@sohu.com",
            "@foxmail.com",
            "@139.com",
            "@yeah.net",
            "@vip.qq.com",
            "@vip.sina.com"
        )
        /*user_email.setAdapter(
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                emailSufixs
            )
        )
        user_email.setTokenizer(EmailAutoTokenizer())*/

        referValid()

        topImg.setOnClickListener {
            getUserInfo()
        }

        /*imageValid.setOnClickListener {
            referValid()
        }*/


        user_submit.setOnClickListener {

            userSubT.text = "进入中..."
            if ((user_email.text?.isEmpty() == true) or user_pass.text!!.isEmpty()) {
                EggUtil.toast("不能为空")
                userSubT.text = "进入"
                return@setOnClickListener
            }

            if (AVUser.currentUser() == null) {
                userSubT.text = "登录中"
                AVUser.loginByEmail(user_email.text.toString(), user_pass.text.toString())
                    .subscribe(object : Observer<AVObject> {
                        override fun onComplete() {}

                        override fun onSubscribe(d: Disposable) {}

                        override fun onNext(t: AVObject) {
                            val androidId = getAndroidId()
                            if (t.getString("androidId") != androidId && (Date().time - t.getDate("loginDate").time) < (60 * 60 * 24 * 1000)) {
                                EggUtil.toast("您的设备已在另一个设备登录，请一天后尝试")
                                AVUser.logOut()
                                userSubT.text = "进入"
                                return
                            }

                            t.put("loginDate", Date())
                            t.put(
                                "androidId", androidId
                            )
                            t.saveInBackground().subscribe(object : Observer<AVObject> {
                                override fun onComplete() {
                                }

                                override fun onSubscribe(d: Disposable) {

                                }

                                override fun onNext(t: AVObject) {
                                    finish()
                                }

                                override fun onError(e: Throwable) {
                                    EggUtil.toast("登录失败，请重试")
                                }

                            })
                            userSubT.text = "进入"
                        }

                        override fun onError(e: Throwable) {
                            val aException = AVException(e)
                            println("错误：$e")
                            when (aException.code) {
                                210 -> {
                                    EggUtil.toast("用户名或密码错误")
                                    userSubT.text = "进入"
                                }

                                401 -> {
                                    EggUtil.toast("破解逆向是不好的哟")
                                    userSubT.text = "进入"
                                }

                                125 -> {
                                    EggUtil.toast("邮箱格式错误")
                                }

                                211 -> {
                                    userSubT.text = "注册中"
                                    val avUser = AVUser()
                                    avUser.email = user_email.text.toString()
                                    avUser.username = user_email.text.toString()
                                    avUser.password = user_pass.text.toString()
                                    avUser.put("loginDate", Date())
                                    avUser.put(
                                        "androidId", getAndroidId()
                                    )
                                    avUser.signUpInBackground()
                                        .subscribe(object : Observer<AVObject> {
                                            override fun onComplete() {}

                                            override fun onSubscribe(d: Disposable) {}

                                            override fun onNext(t: AVObject) {
                                                EggUtil.toast("注册成功，请去邮箱验证账号")
                                                userSubT.text = "登录中"
                                                it.performClick()
                                            }

                                            override fun onError(e: Throwable) {
                                                if (AVException(e).code == 125) {
                                                    EggUtil.toast("邮箱格式错误")
                                                }
                                                userSubT.text = "进入"
                                            }
                                        })
                                }

                                else -> {
                                    AVException(e).message?.let { its ->
                                        EggUtil.toast(its)
                                    }
                                    userSubT.text = "进入"
                                }
                            }
                        }
                    })
            } else {
                EggUtil.toast("您已登录")
                userSubT.text = "进入"
            }


            /* try {
                 when (userState) {
                     USER_STATE_LOGIN -> {
                         ToastUtil(this).longShow("登录")
                         AVUser.loginByEmail(user_name.text.toString(), user_pass.text.toString())
                             .subscribe(object : Observer<AVUser> {
                                 override fun onSubscribe(d: Disposable) {
                                 }

                                 override fun onNext(t: AVUser) {
                                 }

                                 override fun onError(e: Throwable) {
                                 }

                                 override fun onComplete() {
                                     userSubT.text = ""
                                 }

                             })
                         *//*thread {
                            val response = userService.loginUser(
                                user_name.text.toString(),
                                user_pass.text.toString()
                            ).execute()
                            response.body()?.string()?.let {
                                runOnUiThread {
                                    val json = JSON.parseObject(it)
                                    val code = json.getIntValue("code")
                                    println("登录状态码是$code")
                                    println("登录的数据是$json")
                                    if (code == 1) {
                                        SpTool.putSettingString(
                                            "token",
                                            json.getJSONObject("data").getJSONObject("userinfo")
                                                .getString("token")
                                        )
                                    }

                                    val msg = json.getString("msg")
                                    ToastUtil(this).longShow(msg)
                                }
                            }
                            println("返回头:${response.headers()}")
                        }*//*
                    }

                    USER_STATE_REGISTER -> {
                        ToastUtil(this).longShow("注册")

                        *//*thread {
                            val response = userService.registerUser(
                                user_name.text.toString(),
                                user_pass.text.toString(),
                                user_email.text.toString(),
                                "13333333333",
                                user_valid_code.text.toString()
                            ).execute()
                            runOnUiThread {
                                response.body()?.string()?.let {
                                    val json = JSON.parseObject(it)
                                    val code = json.getIntValue("code")
                                    println("注册状态码是$code")
                                    val msg = json.getString("msg")
                                    ToastUtil(this).longShow(msg)
                                }
                            }


                        }*//*
                    }
                }
            } catch (e: Exception) {
                println("发生了错误$e")
            }*/
        }

        /*user_toggle.setOnClickListener {

            when (userState) {
                USER_STATE_REGISTER -> {
                    YoYo.with(Techniques.TakingOff).duration(700).onStart {
                    }
                        .onStart {
                            registerContainer.visibility = View.GONE
                            YoYo.with(Techniques.SlideInUp).duration(700).playOn(commitContainer)
                        }
                        .onEnd {
                        }.playOn(registerContainer)
                    userState = USER_STATE_LOGIN
                    user_toggle_text.text = "切换为注册"
                }

                USER_STATE_LOGIN -> {
                    registerContainer.visibility = View.VISIBLE
                    YoYo.with(Techniques.Landing).duration(700)
                        .playOn(registerContainer)
                    userState = USER_STATE_REGISTER
                    user_toggle_text.text = "切换为登录"
                }
            }
        }*/
    }

    private fun referValid() {

        thread {
//            val userService2 = ServiceCreator.create<UserService>()
            /*val cookieJar: ClearableCookieJar =
                PersistentCookieJar(
                    SetCookieCache(),
                    SharedPrefsCookiePersistor(EggApplication.context)
                )
            val okHttpClient = OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build()
            val retrofit: Retrofit =
                Retrofit.Builder().baseUrl("http://192.168.1.54:8045/").addConverterFactory(
                    GsonConverterFactory.create()
                )
                    .client(okHttpClient)
                    .build()
            val userService2 =retrofit.create(UserService::class.java)
*/


            try {
                val img = userService.getImageValidCode().execute()


                val bit = BitmapFactory.decodeStream(img.body()?.byteStream())
                runOnUiThread {
                    Glide.with(this).load(bit).placeholder(R.mipmap.placeholder)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .error(R.mipmap.error)
//                        .into(imageValid)
                }
            } catch (e: java.lang.Exception) {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            println("QQ登录已返回")
            Tencent.onActivityResultData(requestCode, resultCode, data, QQLoginUiListener())
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getAndroidId(): String {
        val androidId = Settings.System.getString(
            contentResolver,
            Settings.System.ANDROID_ID
        )
        if (androidId.isEmpty()) {
            val androidStr = "androidId"
            val spAndroidId = SpTool.getSettingString(androidStr, "")
            if (spAndroidId == "") {
                val randomStr = MD5.computeMD5((0..1000000).random().toString())
                SpTool.putSettingString("", randomStr)
                return randomStr
            }
            return spAndroidId
        } else {
            return androidId
        }
    }

    private fun switch(id: Int) {
        val v = findViewById<LinearLayout>(id)
        when (id) {
            R.id.log_on -> {
                val otherV = findViewById<LinearLayout>(R.id.reg_on)
            }

            R.id.reg_on -> {
                val otherV = findViewById<LinearLayout>(R.id.log_on)
            }
        }
    }

    private fun getUserInfo() {
        thread {
            val us = userService.getUserInfo(SpTool.getSettingString("token", ""))
            us.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    println("状态码是${response.code()}")
                    println("已获取到用户信息${response.body()?.string()}")
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    println("获取信息发生了错误$t")
                }

            })
        }
    }

    inner class QQLoginUiListener : IUiListener {
        override fun onComplete(p0: Any) {
            val asLoading = XPopup.Builder(this@LoginActivity).asLoading()
            asLoading.show()
            val json = JSON.parseObject(p0.toString())

//            thirdPartyData["user"] = AVUser.currentUser()

            LeanQQUtil.result(json, false, object : Observer<AVUser> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: AVUser) {

                    asLoading.dismiss()
                    finish()
                }

                override fun onError(e: Throwable) {
                    asLoading.dismiss()
                    ToastUtil(this@LoginActivity).longShow("登录错误：${e.message}")
                }

            })
        }

        override fun onCancel() {
            ToastUtil(this@LoginActivity).longShow("QQ登录已关闭1")
        }

        override fun onWarning(p0: Int) {
            ToastUtil(this@LoginActivity).longShow("QQ错误$p0")

        }

        override fun onError(p0: UiError?) {
            ToastUtil(this@LoginActivity).longShow("QQ登录发送错误${p0?.errorMessage}")
        }
    }
}


/* val okHttpClient = OkHttpClient()
                    val request = Request.Builder().url(
                        """
                https://graph.qq.com/user/get_user_info?access_token=${json.getString("access_token")}&oauth_consumer_key=${AppSetting.QQ_APP_ID}&openid=${json.getString(
                            "openid"
                        )}
            """.trimIndent()
                    ).build()
                    okHttpClient.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                        }

                        @SuppressLint("CheckResult")
                        override fun onResponse(call: Call, response: Response) {
                            val j = JSON.parseObject(response.body?.string() ?: "")
                            val qqName = j.getString("nickname")?:"默认名字"
                            val avCurrentUser = AVUser.currentUser()
                            avCurrentUser.username = qqName
                            avCurrentUser.saveInBackground().subscribe(object :Observer<AVObject>{
                                override fun onComplete() {
                                }

                                override fun onSubscribe(d: Disposable) {
                                }

                                override fun onNext(t: AVObject) {
                                }

                                override fun onError(e: Throwable) {
                                }

                            })
                        }
                    })*/


/*


user_login_text.text = "进入中..."
            if (user_email.text.isEmpty() or user_pass.text!!.isEmpty()) {
                EggUtil.toast("不能为空")
                user_login_text.text = "进入"
                return@setOnClickListener
            }

            if (AVUser.currentUser() == null) {
                user_login_text.text = "登录中"
                AVUser.loginByEmail(user_email.text.toString(), user_pass.text.toString())
                    .subscribe(object : Observer<AVObject> {
                        override fun onComplete() {}

                        override fun onSubscribe(d: Disposable) {}

                        override fun onNext(t: AVObject) {
                            val androidId = getAndroidId()
                            if (t.getString("androidId") != androidId && (Date().time - t.getDate("loginDate").time) < (60 * 60 * 24 * 1000)) {
                                EggUtil.toast("您的设备已在另一个设备登录，请一天后尝试")
                                AVUser.logOut()
                                user_login_text.text = "进入"
                                return
                            }

                            t.put("loginDate", Date())
                            t.put(
                                "androidId", androidId
                            )
                            t.saveInBackground().subscribe(object : Observer<AVObject> {
                                override fun onComplete() {
                                }

                                override fun onSubscribe(d: Disposable) {

                                }

                                override fun onNext(t: AVObject) {
                                    finish()
                                }

                                override fun onError(e: Throwable) {
                                    EggUtil.toast("登录失败，请重试")
                                }

                            })
                            user_login_text.text = "进入"
                        }

                        override fun onError(e: Throwable) {
                            val aException = AVException(e)
                            println("错误：$e")
                            when (aException.code) {
                                210 -> {
                                    EggUtil.toast("用户名或密码错误")
                                    user_login_text.text = "进入"
                                }

                                401 -> {
                                    EggUtil.toast("破解逆向是不好的哟")
                                    user_login_text.text = "进入"
                                }

                                125 -> {
                                    EggUtil.toast("邮箱格式错误")
                                }

                                211 -> {
                                    user_login_text.text = "注册中"
                                    val avUser = AVUser()
                                    avUser.email = user_email.text.toString()
                                    avUser.username = user_email.text.toString()
                                    avUser.password = user_pass.text.toString()
                                    avUser.put("loginDate", Date())
                                    avUser.put(
                                        "androidId", getAndroidId()
                                    )
                                    avUser.signUpInBackground()
                                        .subscribe(object : Observer<AVObject> {
                                            override fun onComplete() {}

                                            override fun onSubscribe(d: Disposable) {}

                                            override fun onNext(t: AVObject) {
                                                EggUtil.toast("注册成功，请去邮箱验证账号")
                                                user_login_text.text = "登录中"
                                                it.performClick()
                                            }

                                            override fun onError(e: Throwable) {
                                                if (AVException(e).code == 125) {
                                                    EggUtil.toast("邮箱格式错误")
                                                }
                                                user_login_text.text = "进入"
                                            }
                                        })
                                }

                                else -> {
                                    AVException(e).message?.let { its ->
                                        EggUtil.toast(its)
                                    }
                                    user_login_text.text = "进入"
                                }
                            }
                        }
                    })
            } else {
                EggUtil.toast("您已登录")
                user_login_text.text = "进入"
            }


 */