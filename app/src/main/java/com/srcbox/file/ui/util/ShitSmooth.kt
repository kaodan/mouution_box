package com.srcbox.file.ui.util

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hjq.bar.OnTitleBarListener
import com.srcbox.file.R
import com.srcbox.file.contract.ContractShitSmooth
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.presenter.ShitSmoothPresenter
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.shit_smooth.*
import java.io.InputStream

class ShitSmooth : ContractShitSmooth.View, AppCompatActivity() {
    private val shitSmoothPresenter = ShitSmoothPresenter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shit_smooth)
        GlobUtil.changeTitle(this)

        EggUtil.setViewRadius(
            makeWord,
            AppSetting.colorStress,
            1,
            AppSetting.colorStress,
            EggUtil.dp2px(this, 8f).toFloat()
        )

        EggUtil.setViewRadius(
            copyWord,
            AppSetting.colorStress,
            1,
            AppSetting.colorStress,
            EggUtil.dp2px(this, 8f).toFloat()
        )
        makeWord.setOnClickListener {
            shitSmoothPresenter.makeShitSmooth()
        }

        copyWord.setOnClickListener {
            copyShitSmooth()
        }
        ShitSmoothToolBar.setOnTitleBarListener(object :OnTitleBarListener{
            override fun onLeftClick(v: View?) {
                finish()
            }

            override fun onRightClick(v: View?) {
            }

            override fun onTitleClick(v: View?) {
            }
        })
    }

    override fun resultShitSmooth(string: String) {
        content.text = SpannableStringBuilder(string)
    }

    override fun getTitleV(): String {
        return titleContent.text.toString()
    }

    override fun getLen(): Long {
        if (lenContent.text.toString().isEmpty()) {
            return 0
        }
        return lenContent.text.toString().toLong()
    }

    override fun getWordTab(): InputStream {
        return assets.open("json/worldTab.json")
    }

    override fun copyShitSmooth() {
        EggUtil.copyText(this, content.text.toString())
        EggUtil.toast("复制成功")
    }
}