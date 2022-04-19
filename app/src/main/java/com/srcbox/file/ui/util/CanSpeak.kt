package com.srcbox.file.ui.util

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.srcbox.file.R
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.can_speak.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.lang.Exception

class CanSpeak : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.can_speak)
        GlobUtil.changeTitle(this)
        translate.setOnClickListener {
            result_text.visibility = View.VISIBLE
            result_text.text = ""
            translate as TextView
            translate.text = "翻译中"
            val client = OkHttpClient()
            val pJsonO = JSONObject()
            pJsonO["text"] = speak_content.text
            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = RequestBody.create(mediaType, pJsonO.toString())
            val request = Request.Builder().post(requestBody)
                .url("https://lab.magiconch.com/api/nbnhhsh/guess").build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        EggUtil.toast("失败，请重试")
                        translate.text = "翻译"
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val result = response.body?.string()
                        val results = JSON.parseArray(result)
                        if (results.size == 0) {
                            runOnUiThread {
                                result_text.text = "没有结果"
                            }
                        }
                        runOnUiThread {
                            results.forEach {
                                it as JSONObject
                                var transStr = ""
                                val name = it["name"]
                                val jsonTrans = it.getJSONArray("trans")
                                val inputting = it.getJSONArray("inputting")
                                jsonTrans?.forEach { its ->
                                    transStr += "$its   "
                                }
                                inputting?.forEach { its ->
                                    transStr += "$its   "
                                }

//                            inputting
                                result_text.text =
                                    result_text.text.toString() + "$name\n$transStr\n"
                            }
//                              result_text.text = result_text.text.toString() + it.toString() + "\n"

                            translate.text = "翻译"
                        }
                    } catch (e: Exception) {
                        translate.text = "翻译"
                        result_text.text = "失败"
                    }

                }
            })
        }
    }
}