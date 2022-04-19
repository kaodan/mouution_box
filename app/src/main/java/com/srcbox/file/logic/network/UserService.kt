package com.srcbox.file.logic.network

import com.srcbox.file.logic.data.MountUserData
import com.srcbox.file.util.SpTool
import okhttp3.ResponseBody
import okhttp3.internal.http.RealResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UserService {
    /*
    获取当前用户信息
     */
    @GET("/api/user/getuserinfo")
    fun getUserInfo(@Query("token") token: String): Call<ResponseBody>

    /*
    注册账号
     */
    @GET("/api/user/register")
    fun registerUser(
        @Query("username") name: String,
        @Query("password") pass: String,
        @Query("email") email: String,
        @Query("mobile") mobile: String,
        @Query("code") code: String
    ): Call<ResponseBody>

    /*
    登录账号
     */
    @GET("/api/user/login")
    fun loginUser(
        @Query("account") account: String,
        @Query("password") pass: String
    ): Call<ResponseBody>

    /*
    获取验证码图片
     */
    @GET("/captcha")
    fun getImageValidCode(): Call<ResponseBody>
}