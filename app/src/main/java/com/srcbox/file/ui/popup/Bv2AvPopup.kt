package com.srcbox.file.ui.popup

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.lxj.xpopup.core.CenterPopupView
import com.srcbox.file.R
import com.srcbox.file.util.EggUtil
import okhttp3.*
import org.jsoup.Jsoup
import www.linwg.org.lib.LCardView
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

class Bv2AvPopup(context: Context) : CenterPopupView(context) {
    override fun onCreate() {
        super.onCreate()
        val resultAvNum = findViewById<TextView>(R.id.result_av_num)
        val bv2AvText = findViewById<TextView>(R.id.bv2av_text)
        findViewById<LCardView>(R.id.bv2av_on).setOnClickListener {
            bv2AvText.text = "转换中"
            val bvNum = findViewById<TextView>(R.id.bv_edit).text.toString()
            if (bvNum.isEmpty()){
                EggUtil.toast("不能为空")
            }
            val client = OkHttpClient()
            val request = Request.Builder().url("http://bv2av.com/index.php?BV=$bvNum").build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    (context as Activity).runOnUiThread {
                        resultAvNum.text = "获取失败"
                        resultAvNum.visibility = View.VISIBLE
                        bv2AvText.text = "转换"
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.body == null) {
                        (context as Activity).runOnUiThread {
                            resultAvNum.text = "获取失败"
                            resultAvNum.visibility = View.VISIBLE
                            bv2AvText.text = "转换"
                            return@runOnUiThread
                        }
                    }
                    val jSoup = Jsoup.parse(response.body!!.string())
                    val result = jSoup.getElementById("result").getElementsByTag("font").text()
                    (context as Activity).runOnUiThread {
                        resultAvNum.text = result
                        bv2AvText.text = "转换"
                        resultAvNum.visibility = View.VISIBLE
                    }
                }
            })
            /*
            if (bvNum.isEmpty()){
                EggUtil.toast("不能为空!")
                return@setOnClickListener
            }

            if (isContainChinese(bvNum)){
                EggUtil.toast("格式错误!")
                return@setOnClickListener
            }
            resultAvNum.text = EggUtil.bv2av(bvNum)*/

        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.bv_av_popup
    }

    fun isContainChinese(str: String?): Boolean {
        val p: Pattern = Pattern.compile("[\u4e00-\u9fa5]")
        val m: Matcher = p.matcher(str!!)
        return m.find()
    }
}