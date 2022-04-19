package com.srcbox.file.ui.popup

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.Signature
import android.os.Build
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.core.BottomPopupView
import com.srcbox.file.R
import com.srcbox.file.adapter.AppFunSelect
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.util.EggUtil
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.security.MessageDigest
import java.text.SimpleDateFormat

class AppMessagePopup(context: Context) : BottomPopupView(context) {
    @SuppressLint("SimpleDateFormat")
    override fun onCreate() {
        super.onCreate()
        val appMsg = findViewById<RecyclerView>(R.id.app_message)
        EggUtil.loadIcon(
            context,
            AppSetting.colorStress,
            findViewById(R.id.more_info)
        )

        /**/
        val arrayList = ArrayList<String>()
        arrayList.add(ExtractSrcPopup.userAppData!!.name)
        arrayList.add(ExtractSrcPopup.userAppData?.appPackageName!!)
        arrayList.add(ExtractSrcPopup.userAppData!!.appSource.absolutePath)
        if (Build.VERSION.SDK_INT >= 24) {
            arrayList.add(SimpleDateFormat("YYYY MM dd hh:mm:ss").format(ExtractSrcPopup.userAppData!!.appInstallTime))
        } else {
            arrayList.add(SimpleDateFormat("yyyy MM dd hh:mm:ss").format(ExtractSrcPopup.userAppData!!.appInstallTime))
        }

        arrayList.add(EggUtil.getFileDiffType(ExtractSrcPopup.userAppData!!.appSize))
        arrayList.add(
            "SHA256签名\n${EggUtil.getAppSHA256(
                context,
                ExtractSrcPopup.userAppData?.appPackageName!!
            )!!}"
        )
        arrayList.add(
            "SHA1签名\n${EggUtil.getAppSHA1(
                context,
                ExtractSrcPopup.userAppData?.appPackageName!!
            )!!}"
        )

        arrayList.add(
            "MD5签名\n${getMD5MessageDigest(
                context,
                ExtractSrcPopup.userAppData?.appPackageName!!
            )!!}"
        )
        appMsg.layoutManager = LinearLayoutManager(context)
        appMsg.adapter = AppFunSelect(context, arrayList)
    }

    @SuppressLint("WrongConstant", "PackageManagerGetSignatures")
    fun getMD5MessageDigest(
        mContext: Context,
        str: String
    ): String? {
        return try {
            var i = 0
            val signature: Signature =
                mContext.packageManager.getPackageInfo(str, 64).signatures[0]
            val instance: MessageDigest = MessageDigest.getInstance("md5")
            instance.update(signature.toByteArray())
            val digest: ByteArray = instance.digest()
            val stringBuilder = StringBuilder()
            val length = digest.size
            while (i < length) {
                var toHexString =
                    Integer.toHexString(digest[i].toInt() and 255)
                if (toHexString.length == 1) {
                    val stringBuilder2 = StringBuilder()
                    stringBuilder2.append("0")
                    stringBuilder2.append(toHexString)
                    toHexString = stringBuilder2.toString()
                }
                stringBuilder.append(toHexString)
                i++
            }
            stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "null"
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.app_message_popup
    }
}