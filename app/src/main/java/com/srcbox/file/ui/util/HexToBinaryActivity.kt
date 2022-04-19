package com.srcbox.file.ui.util

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnSelectListener
import com.srcbox.file.R
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.activity_hex_to_binary.*
import java.lang.Exception
import java.util.function.LongToDoubleFunction

class HexToBinaryActivity : AppCompatActivity() {


    private var numberView = arrayListOf(
        R.id.on,
        R.id.tn,
        R.id.thn,
        R.id.fn,
        R.id.fin,
        R.id.sn,
        R.id.sen,
        R.id.en,
        R.id.nn,
        R.id.af,
        R.id.bf,
        R.id.cf,
        R.id.df,
        R.id.ef,
        R.id.ff,
        R.id.zn
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hex_to_binary)
        GlobUtil.changeTitle(this)
        grayNoneText()

        EggUtil.loadIcon(this, AppSetting.colorStress, coverIcon)
        EggUtil.loadIcon(this, AppSetting.colorStress, deleteN)

        beforeNumber.setOnClickListener {
            XPopup.Builder(this)
                .atView(it)
                .asAttachList(arrayOf("二进制", "八进制", "十进制", "十六进制"), null,
                    OnSelectListener { position, text ->
                        beforeNumberText.text = text
                        grayNoneText()
                        inputNumber.text = ""
                        resultN.text = ""
                    }).show()
        }

        afterNumber.setOnClickListener {
            XPopup.Builder(this)
                .atView(it)
                .asAttachList(arrayOf("二进制", "八进制", "十进制", "十六进制"), null,
                    OnSelectListener { position, text ->
                        afterNumberText.text = text
                    }).show()
        }


        deleteN.setOnClickListener {
            inputNumber.apply {
                val t = text.toString()
                if (length() == 0) return@setOnClickListener
                text = t.substring(0, length() - 1)
            }
        }

        inputNumber.addTextChangedListener {
            try {
                val inpN = it.toString().replace(" ", "")
                if (beforeNumberText.text == "二进制" && afterNumberText.text == "八进制") {
                    resultN.text = Integer.toOctalString(Integer.parseInt(inpN, 2))
                }

                if (beforeNumberText.text == "二进制" && afterNumberText.text == "十进制") {
                    resultN.text = Integer.parseInt(inpN, 2).toString()
                }


                if (beforeNumberText.text == "二进制" && afterNumberText.text == "十六进制") {
                    resultN.text = Integer.toHexString(Integer.parseInt(inpN, 2))
                }

                if (beforeNumberText.text == "八进制" && afterNumberText.text == "二进制") {
                    resultN.text = Integer.toBinaryString(Integer.parseInt(inpN, 8))
                }

                if (beforeNumberText.text == "八进制" && afterNumberText.text == "十进制") {
                    resultN.text = Integer.parseInt(inpN, 8).toString()
                }

                if (beforeNumberText.text == "八进制" && afterNumberText.text == "十六进制") {
                    resultN.text = Integer.toHexString(Integer.parseInt(inpN, 8))
                }

                if (beforeNumberText.text == "十进制" && afterNumberText.text == "二进制") {
                    resultN.text = Integer.toBinaryString(Integer.parseInt(inpN))
                }


                if (beforeNumberText.text == "十进制" && afterNumberText.text == "八进制") {
                    resultN.text = Integer.toOctalString(Integer.parseInt(inpN))
                }


                if (beforeNumberText.text == "十进制" && afterNumberText.text == "十六进制") {
                    resultN.text = Integer.toHexString(Integer.parseInt(inpN))
                }

                if (beforeNumberText.text == "十六进制" && afterNumberText.text == "二进制") {
                    resultN.text = Integer.toBinaryString(Integer.parseInt(inpN, 16))
                }


                if (beforeNumberText.text == "十六进制" && afterNumberText.text == "八进制") {
                    resultN.text = Integer.toOctalString(Integer.parseInt(inpN, 16))
                }


                if (beforeNumberText.text == "十六进制" && afterNumberText.text == "十进制") {
                    resultN.text = Integer.parseInt(inpN, 16).toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        resultN.setOnClickListener {
            EggUtil.copyText(this, resultN.text.toString())
            EggUtil.toast("已复制")
        }

    }

    @SuppressLint("SetTextI18n")
    public fun plusNumber(view: View) {
        view as TextView
        inputNumber.apply {
            text = text.toString() + view.text
            if (beforeNumberText.text == "二进制") {
                if ((inputNumber.text.toString().replace(" ", "")).length % 4 == 0) {
                    text = "$text "
                }
            }
        }
    }

    private fun grayNoneText() {
        var currNumber = arrayOf(0)
        if (beforeNumberText.text == "二进制") {
            currNumber = arrayOf(R.id.zn, R.id.on)
        }

        if (beforeNumberText.text == "十进制") {
            currNumber = arrayOf(
                R.id.zn,
                R.id.on,
                R.id.tn,
                R.id.thn,
                R.id.fn,
                R.id.fin,
                R.id.sn,
                R.id.sen,
                R.id.en,
                R.id.nn
            )
        }

        if (beforeNumberText.text == "八进制") {
            currNumber =
                arrayOf(R.id.zn, R.id.on, R.id.tn, R.id.thn, R.id.fn, R.id.fin, R.id.sn, R.id.sen)
        }


        numberView.forEach {
            val v = findViewById<View>(it) as TextView
            v.setTextColor(Color.parseColor("#cccccc"))
            v.setOnClickListener { }
        }

        if (beforeNumberText.text == "十六进制") {
            numberView.forEach {
                val v = findViewById<View>(it) as TextView
                v.setTextColor(Color.parseColor("#000000"))
                v.setOnClickListener { plusNumber(v) }
            }
            return
        }

        currNumber.forEach {
            val v = findViewById<View>(it) as TextView
            v.setTextColor(Color.parseColor("#000000"))
            v.setOnClickListener { plusNumber(v) }
        }
    }
}