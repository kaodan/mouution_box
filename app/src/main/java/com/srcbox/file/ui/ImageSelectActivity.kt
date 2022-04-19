package com.srcbox.file.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.srcbox.file.R
import com.srcbox.file.adapter.ImageSelectAdapter
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.image_select.*
import java.io.File

class ImageSelectActivity : AppCompatActivity() {
    private var fileArr = ArrayList<File>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_select)
        GlobUtil.changeTitle(this, false)
        val filePath = intent.getStringExtra("filePath")
        path_text.text = filePath
        val f = File(filePath!!)
        listFile(f)
        fileArr = fileArr.filter {
            it.absolutePath.endsWith(".png") || it.absolutePath.endsWith(".jpg") || it.absolutePath.endsWith(
                ".gif"
            )
        } as ArrayList<File>
        image_select_r.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        image_select_r.adapter = ImageSelectAdapter(this, fileArr)
    }

    private fun listFile(file: File) {
        if (file.isDirectory) {
//            fileArr.addAll(file.listFiles()!!)
            file.listFiles()!!.forEach {
                listFile(it)
            }
        }
        if (file.isDirectory) {
            fileArr.addAll(file.listFiles()!!)
        } else {
            fileArr.add(file)
        }
    }
}