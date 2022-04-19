package com.srcbox.file.logic.`object`

import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.srcbox.file.application.EggApplication
import com.srcbox.file.util.SpTool
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ServiceCreator {
    private const val BASE_URL = "http://192.168.1.54:8045/"
    val okHttpClient = OkHttpClient().newBuilder().addInterceptor {
        val original = it.request()
        val url = original.url.newBuilder()
            .addQueryParameter("token", SpTool.getSettingString("token", "")).build()
        val req = original.newBuilder().method(original.method, original.body).url(url).build()
        it.proceed(req)
    }
    private val retrofit: Retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)
}