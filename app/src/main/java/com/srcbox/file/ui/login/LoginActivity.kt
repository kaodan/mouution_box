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
                "????????????", null, "????????????"
            ) { text ->

                try {
                    AVUser.requestPasswordResetInBackground(text)
                        .subscribe(object : Observer<AVNull> {
                            override fun onComplete() {
                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onNext(t: AVNull) {
                                EggUtil.toast("?????????")
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

            userSubT.text = "?????????..."
            if ((user_email.text?.isEmpty() == true) or user_pass.text!!.isEmpty()) {
                EggUtil.toast("????????????")
                userSubT.text = "??????"
                return@setOnClickListener
            }

            if (AVUser.currentUser() == null) {
                userSubT.text = "?????????"
                AVUser.loginByEmail(user_email.text.toString(), user_pass.text.toString())
                    .subscribe(object : Observer<AVObject> {
                        override fun onComplete() {}

                        override fun onSubscribe(d: Disposable) {}

                        override fun onNext(t: AVObject) {
                            val androidId = getAndroidId()
                            if (t.getString("androidId") != androidId && (Date().time - t.getDate("loginDate").time) < (60 * 60 * 24 * 1000)) {
                                EggUtil.toast("????????????????????????????????????????????????????????????")
                                AVUser.logOut()
                                userSubT.text = "??????"
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
                                    EggUtil.toast("????????????????????????")
                                }

                            })
                            userSubT.text = "??????"
                        }

                        override fun onError(e: Throwable) {
                            val aException = AVException(e)
                            println("?????????$e")
                            when (aException.code) {
                                210 -> {
                                    EggUtil.toast("????????????????????????")
                                    userSubT.text = "??????"
                                }

                                401 -> {
                                    EggUtil.toast("???????????????????????????")
                                    userSubT.text = "??????"
                                }

                                125 -> {
                                    EggUtil.toast("??????????????????")
                                }

                                211 -> {
                                    userSubT.text = "?????????"
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
                                                EggUtil.toast("???????????????????????????????????????")
                                                userSubT.text = "?????????"
                                                it.performClick()
                                            }

                                            override fun onError(e: Throwable) {
                                                if (AVException(e).code == 125) {
                                                    EggUtil.toast("??????????????????")
                                                }
                                                userSubT.text = "??????"
                                            }
                                        })
                                }

                                else -> {
                                    AVException(e).message?.let { its ->
                                        EggUtil.toast(its)
                                    }
                                    userSubT.text = "??????"
                                }
                            }
                        }
                    })
            } else {
                EggUtil.toast("????????????")
                userSubT.text = "??????"
            }


            /* try {
                 when (userState) {
                     USER_STATE_LOGIN -> {
                         ToastUtil(this).longShow("??????")
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
                                    println("??????????????????$code")
                                    println("??????????????????$json")
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
                            println("?????????:${response.headers()}")
                        }*//*
                    }

                    USER_STATE_REGISTER -> {
                        ToastUtil(this).longShow("??????")

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
                                    println("??????????????????$code")
                                    val msg = json.getString("msg")
                                    ToastUtil(this).longShow(msg)
                                }
                            }


                        }*//*
                    }
                }
            } catch (e: Exception) {
                println("???????????????$e")
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
                    user_toggle_text.text = "???????????????"
                }

                USER_STATE_LOGIN -> {
                    registerContainer.visibility = View.VISIBLE
                    YoYo.with(Techniques.Landing).duration(700)
                        .playOn(registerContainer)
                    userState = USER_STATE_REGISTER
                    user_toggle_text.text = "???????????????"
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
            println("QQ???????????????")
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
                    println("????????????${response.code()}")
                    println("????????????????????????${response.body()?.string()}")
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    println("???????????????????????????$t")
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
                    ToastUtil(this@LoginActivity).longShow("???????????????${e.message}")
                }

            })
        }

        override fun onCancel() {
            ToastUtil(this@LoginActivity).longShow("QQ???????????????1")
        }

        override fun onWarning(p0: Int) {
            ToastUtil(this@LoginActivity).longShow("QQ??????$p0")

        }

        override fun onError(p0: UiError?) {
            ToastUtil(this@LoginActivity).longShow("QQ??????????????????${p0?.errorMessage}")
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
                            val qqName = j.getString("nickname")?:"????????????"
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


user_login_text.text = "?????????..."
            if (user_email.text.isEmpty() or user_pass.text!!.isEmpty()) {
                EggUtil.toast("????????????")
                user_login_text.text = "??????"
                return@setOnClickListener
            }

            if (AVUser.currentUser() == null) {
                user_login_text.text = "?????????"
                AVUser.loginByEmail(user_email.text.toString(), user_pass.text.toString())
                    .subscribe(object : Observer<AVObject> {
                        override fun onComplete() {}

                        override fun onSubscribe(d: Disposable) {}

                        override fun onNext(t: AVObject) {
                            val androidId = getAndroidId()
                            if (t.getString("androidId") != androidId && (Date().time - t.getDate("loginDate").time) < (60 * 60 * 24 * 1000)) {
                                EggUtil.toast("????????????????????????????????????????????????????????????")
                                AVUser.logOut()
                                user_login_text.text = "??????"
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
                                    EggUtil.toast("????????????????????????")
                                }

                            })
                            user_login_text.text = "??????"
                        }

                        override fun onError(e: Throwable) {
                            val aException = AVException(e)
                            println("?????????$e")
                            when (aException.code) {
                                210 -> {
                                    EggUtil.toast("????????????????????????")
                                    user_login_text.text = "??????"
                                }

                                401 -> {
                                    EggUtil.toast("???????????????????????????")
                                    user_login_text.text = "??????"
                                }

                                125 -> {
                                    EggUtil.toast("??????????????????")
                                }

                                211 -> {
                                    user_login_text.text = "?????????"
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
                                                EggUtil.toast("???????????????????????????????????????")
                                                user_login_text.text = "?????????"
                                                it.performClick()
                                            }

                                            override fun onError(e: Throwable) {
                                                if (AVException(e).code == 125) {
                                                    EggUtil.toast("??????????????????")
                                                }
                                                user_login_text.text = "??????"
                                            }
                                        })
                                }

                                else -> {
                                    AVException(e).message?.let { its ->
                                        EggUtil.toast(its)
                                    }
                                    user_login_text.text = "??????"
                                }
                            }
                        }
                    })
            } else {
                EggUtil.toast("????????????")
                user_login_text.text = "??????"
            }


 */