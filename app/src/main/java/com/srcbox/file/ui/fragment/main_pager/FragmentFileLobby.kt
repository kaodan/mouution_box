package com.srcbox.file.ui.fragment.main_pager

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.srcbox.file.R
import com.srcbox.file.adapter.MainHomeFunAdapter
import com.srcbox.file.ui.popup.CompressImagePopup
import com.srcbox.file.util.EggIO
import org.json.JSONObject
import java.io.File


class FragmentFileLobby : Fragment() {
    private var v: View? = null
    private var fileArr = ArrayList<File>()
    private var imgFiles = ArrayList<File>()
    private var videoFiles = ArrayList<File>()
    private var musicFiles = ArrayList<File>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = LayoutInflater.from(context).inflate(R.layout.fragment_file_lobby, container, false)
        this.v = v
        initView()
        return v
    }

    private fun initView() {


        val homeFunRecyclerV = this.v?.findViewById<RecyclerView>(R.id.picture_recycler_view)

        homeFunRecyclerV?.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        homeFunRecyclerV?.adapter =
            MainHomeFunAdapter(
                activity as Context,
                JSONObject(EggIO.readFile(activity?.assets!!.open("json/main_home_util_fun.json")))
            )

    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("已执行")
        if (requestCode == 0x110 && resultCode == RESULT_OK && data != null) {
            val selectedImage = data.data
            val filePathColumn =
                arrayOf<String>(MediaStore.Images.Media.DATA)
            val cursor: Cursor = activity?.contentResolver?.query(
                selectedImage!!,
                filePathColumn, null, null, null
            )!!
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            val picturePath: String = cursor.getString(columnIndex)
            cursor.close()
            CompressImagePopup.file = File(picturePath)
            println("当前选择的图片是：${File(picturePath)}")
        }
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


/*Handler().postDelayed({

    Thread {
        val f = AppStorageData.getFileOutFile()
        if (f.exists()){
            listFile(f)
        }
        fileArr.forEach {
            if (it.name.endsWith(".png") || it.name.endsWith(".jpg") || it.name.endsWith(".gif")) {
                imgFiles.add(it)
                println(it)
            }
        *//*    if (it.name.endsWith(".mp4")) {
                        videoFiles.add(it)
                    }
                    if (it.name.endsWith(".mp3")) {
                        musicFiles.add(it)
                    }*//*
                }
                val spanCount = 3
                val orientation = StaggeredGridLayoutManager.VERTICAL
//        val latelyRecyclerView = this.v?.findViewById<RecyclerView>(R.id.lately_recycler_view)
                val pictureRecyclerView =
                    this.v?.findViewById<RecyclerView>(R.id.picture_recycler_view)
                val noneContainer =
                    this.v?.findViewById<LinearLayout>(R.id.none_container)
                val noneLoad =
                    this.v?.findViewById<ImageView>(R.id.none_load)

                noneLoad?.setOnClickListener {
                    noneContainer?.visibility = View.GONE
                    initView()
                }

                *//*
                val videoRecyclerView = this.v?.findViewById<RecyclerView>(R.id.video_recycler_view)
                val musicRecyclerView = this.v?.findViewById<RecyclerView>(R.id.music_recycler_view)*//*

                activity?.runOnUiThread {

                    if (imgFiles.size == 0){
                        noneContainer?.visibility = View.VISIBLE
                        return@runOnUiThread
                    }

                    pictureRecyclerView?.layoutManager =
                        StaggeredGridLayoutManager(spanCount, orientation)
                    *//*videoRecyclerView?.layoutManager =
                        StaggeredGridLayoutManager(spanCount, orientation)
                    musicRecyclerView?.layoutManager =
                        StaggeredGridLayoutManager(spanCount, orientation)*//*
                    *//*latelyRecyclerView?.layoutManager =
                        StaggeredGridLayoutManager(spanCount, orientation)*//*
                    pictureRecyclerView?.adapter = ImageSelectAdapter(
                        activity as Context,
                        imgFiles
                    )
//                    imgFiles.subList(0,10) as List<File>
                    *//* videoRecyclerView?.adapter = ImageSelectAdapter(activity as Context,
                         videoFiles.subList(0,10) as List<File>
                     )*//*
                    *//*musicRecyclerView?.adapter = ImageSelectAdapter(activity as Context,
                        musicFiles.subList(0,10) as List<File>
                    )*//*
                }


            }.start()


        }, 1500)*/


/*latelyRecyclerView?.adapter =
    MainHomeFunAdaoter(
        activity as Context,
        JSONObject(EggIO.readFile(activity?.assets!!.open("json/file_lobby_lately_fun.json")))
    )
pictureRecyclerView?.adapter =
    MainHomeFunAdaoter(
        activity as Context,
        JSONObject(EggIO.readFile(activity?.assets!!.open("json/file_lobby_picture_fun.json")))
    )
videoRecyclerView?.adapter =
    MainHomeFunAdaoter(
        activity as Context,
        JSONObject(EggIO.readFile(activity?.assets!!.open("json/file_lobby_video_fun.json")))
    )
musicRecyclerView?.adapter =
    MainHomeFunAdaoter(
        activity as Context,
        JSONObject(EggIO.readFile(activity?.assets!!.open("json/file_lobby_music_fun.json")))
    )*/


/*   latelyRecyclerView?.layoutManager =
       LinearLayoutManager(activity)
   pictureRecyclerView?.layoutManager =
       LinearLayoutManager(activity)
   videoRecyclerView?.layoutManager =
       LinearLayoutManager(activity)
   musicRecyclerView?.layoutManager =
       LinearLayoutManager(activity)*/


/* latelyRecyclerView?.layoutManager = object : GridLayoutManager(activity, 3) {
     override fun canScrollVertically() = false
 }
 pictureRecyclerView?.layoutManager = object : GridLayoutManager(activity, 3) {
     override fun canScrollVertically() = false
 }
 videoRecyclerView?.layoutManager = object : GridLayoutManager(activity, 3) {
     override fun canScrollVertically() = false
 }
 musicRecyclerView?.layoutManager = object : GridLayoutManager(activity, 3) {
     override fun canScrollVertically() = false
 }*/

/*

*/

/*latelyRecyclerView?.layoutManager =
    GridLayoutManager(activity, 3)
*/