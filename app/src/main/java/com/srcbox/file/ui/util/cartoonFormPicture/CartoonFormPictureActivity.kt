package com.srcbox.file.ui.util.cartoonFormPicture

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.lxj.xpopup.XPopup
import com.srcbox.file.R
import com.srcbox.file.ui.util.cartoonFormPicture.adapter.CartoonAdapter
import com.srcbox.file.ui.util.cartoonFormPicture.data.CartoonData
import com.srcbox.file.ui.util.cartoonFormPicture.data.DocData
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.activity_cartoon_form_picture.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.lang.Exception
import kotlin.concurrent.thread

class CartoonFormPictureActivity : AppCompatActivity() {
    companion object {
        const val SELECT_IMAGE = 0xf00
        const val URL = "https://trace.moe/api"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cartoon_form_picture)
        GlobUtil.changeTitle(this)
        selectImg.setOnClickListener {
            goMediaImg()
        }

        title_bar.rightView.setOnClickListener {
            goMediaImg()
        }
    }

    private fun goMediaImg() {
        startActivityForResult(
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
            SELECT_IMAGE
        )
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            val filePathColumn =
                arrayOf<String>(MediaStore.Images.Media.DATA)
            val cursor: Cursor = contentResolver?.query(
                selectedImage!!,
                filePathColumn, null, null, null
            )!!
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            val picturePath: String = cursor.getString(columnIndex)


            cursor.close()
            val asLoading = XPopup.Builder(this).asLoading()
            asLoading.show()

            thread {
                try {
                    val responseBody = uploadImage(File(picturePath))
                    val responseStr = responseBody!!.string()
                    println(responseStr)
                    val json = JSON.parseObject(responseStr)
                    val docsJson = json.getJSONArray("docs")
                    val docs = ArrayList<DocData>()
                    println(docsJson)
                    docsJson.forEach {
                        it as JSONObject
                        docs.add(
                            DocData(
                                it.getDouble("from").toFloat(),
                                it.getDouble("to").toFloat(),
                                it.getDouble("at").toFloat(),
                                it.getInteger("anilist_id"),
                                it.getString("season"),
                                it.getString("episode"),
                                it.getDouble("similarity"),
                                it.getString("anime"),
                                it.getString("filename"),
                                it.getString("tokenthumb"),
                                it.getString("title_native"),
                                it.getString("title_chinese"),
                                it.getString("title_english")
                            )
                        )
                    }

                    val cartoonData =
                        CartoonData(
                            json.getInteger("RawDocsCount"),
                            json.getInteger("RawDocsSearchTime"),
                            docs
                        )
                    runOnUiThread {
                        selectImg.visibility = View.GONE
//                        cartoonListV.layoutManager = GridLayoutManager(this, 1)
                        cartoonListV.layoutManager = LinearLayoutManager(this)
                        cartoonListV.adapter = CartoonAdapter(this, cartoonData.docs)
                        asLoading.dismiss()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        asLoading.dismiss()
                        EggUtil.toast("失败，请重新上传截图${e.message}")
                    }
                }
            }

        }
    }

    private fun uploadImage(file: File): ResponseBody? {
        val okHttpClient = OkHttpClient()
        val reqBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("image", file.name, reqBody)
            .build()
        val uploadRequest = Request.Builder().url("$URL/search").post(multipartBody).build()
        val response = okHttpClient.newCall(uploadRequest).execute()
        return response.body
    }
}

/*
fun uploadImage(file: File): ResponseBody? {
    val okHttpClient = OkHttpClient.Builder().readTimeout(10000L, TimeUnit.SECONDS).build()
    val reqBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    println(reqBody)
    val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
        "image", file.name, reqBody
    ).build()

    val uploadRequest =
        Request.Builder().url("https://trace.moe/api/search").post(requestBody).build()

    val response = okHttpClient.newCall(uploadRequest).execute()
    return response.body
}

fun main() {
    val r = uploadImage(File("D:\\http\\TIM图片20200410214813.jpg"))
    println(r?.string())
}*/
