package com.srcbox.file.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.media.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.alibaba.fastjson.JSONArray
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.XPopupCallback
import com.srcbox.file.R
import com.srcbox.file.application.EggApplication
import com.srcbox.file.contract.MainContract
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.`object`.ScreenCaptureInfo
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.fragment.main_pager.FragmentFileLobby
import com.srcbox.file.ui.fragment.main_pager.FragmentHome
import com.srcbox.file.ui.fragment.main_pager.FragmentMe
import com.srcbox.file.ui.popup.CompressImagePopup
import com.srcbox.file.ui.popup.PublicContent
import com.srcbox.file.util.*
import kotlinx.android.synthetic.main.activity_main.*
import me.rosuh.filepicker.config.FilePickerManager
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), MainContract.View {
    private var transaction: FragmentTransaction? = null
    private var currentPageInt = 0
    private var isAnimRunning = false
    private var fragmentHome: FragmentHome? = null
    private var fragmentFileLobby: FragmentFileLobby? = null
    private var fragmentMe: FragmentMe? = null


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        GlobUtil.changeTitle(this, false)
        fragmentHome = FragmentHome()
        fragmentFileLobby = FragmentFileLobby()
        fragmentMe = FragmentMe()
        initEvent()
        initUi()
        switchNav(img_home_bg)
        GlobUtil.signAProtocol(this)


        if (EggApplication.isDebug) {
            EggUtil.toast("这是DeBug模式")
        }


//        checkIsNewVer()
        GlobUtil.checkIsNewVer(this) {
            val date = System.currentTimeMillis()
            val t = SpTool.getString("publicTime", "time", "0")
            if (date - t!!.toLong() > 60 * 60 * 6 * 1000 && SpTool.getSettingString(
                    "notice",
                    "true"
                ).toBoolean()
            ) {
                XPopup.Builder(this).setPopupCallback(object : XPopupCallback {
                    override fun onBackPressed(): Boolean {
                        return false
                    }

                    override fun onDismiss() {
                        val permissionOnce = SpTool.getSettingString("permissions2", "false")
                        if (permissionOnce == "false") {
                            XPopup.Builder(this@MainActivity)
                                .dismissOnTouchOutside(false)
                                .setPopupCallback(object : XPopupCallback {
                                    override fun onBackPressed(): Boolean {
                                        return true
                                    }

                                    override fun onDismiss() {
                                    }

                                    override fun beforeShow() {
                                    }

                                    override fun onCreated() {
                                    }

                                    override fun onShow() {
                                    }
                                })
                                .asConfirm("存储权限", "为了提取您手机上的文件，此应用需要您授予存储权限。", {
                                    val perArr = ArrayList<String>()

                                    perArr.addAll(Permission.Group.STORAGE)

                                    XXPermissions.with(this@MainActivity)
                                        .permission(perArr)
                                        .request { permissions, all ->
                                            SpTool.putSettingString("permissions2", "true")
                                            SpTool.putString(
                                                "publicTime",
                                                "time",
                                                date.toString()
                                            )
                                        }
                                }, {
                                    onDismiss()
                                    exitProcess(0)
                                }).show()
                        } else {
                            SpTool.putString("publicTime", "time", date.toString())
                        }
                    }

                    override fun beforeShow() {}

                    override fun onCreated() {}

                    override fun onShow() {}

                }).asCustom(PublicContent(this)).show()

            }
        }


    }

    override fun onRestart() {
        super.onRestart()
        initUi()
    }

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {


            0 -> {
                if (!Settings.canDrawOverlays(this)) {
                    EggUtil.toast("您没有授予权限哦")
                    return
                }
                FloatWin.Data.floatWin?.show()
                return
            }
            ScreenCaptureInfo.CODE -> {
                if (data != null) {
                    if (!ScreenCaptureInfo.isStart) {
                        FloatWin(this).show()
                    }

                    ScreenCaptureInfo.intentData = data
                    ScreenCaptureInfo.resultCode = resultCode
                    val intent = Intent(this, ScreenCaptureUtil::class.java)
                    if (Build.VERSION.SDK_INT >= 26) {
                        startForegroundService(intent)
                    } else {
                        startService(intent)
                    }
                } else {
                    EggUtil.toast("您没有授予权限哦")
                }
            }

            0x110 -> {
                if (requestCode == 0x110 && resultCode == Activity.RESULT_OK && data != null) {
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
                    CompressImagePopup.file = File(picturePath)
                    println("当前选择的图片是：${File(picturePath)}")
                    EggUtil.toast("已选择")
                }
            }

            5 -> {
                val asT = XPopup.Builder(this).asLoading("提取中...")
                asT.show()
                val list = FilePickerManager.obtainData()
                if (list.isNotEmpty()) {
                    val oneStr = list[0]
                    val duration = getDuration(oneStr)
//                    val fFmpegAsyncUtils = FFmpegAsyncUtils()
                    val outF = File(
                        AppStorageData.getFileOutFile(),
                        "视频转音频/${EggUtil.getFileNameNoEx(File(oneStr).name)}.mp3"
                    ).absolutePath
                    File(outF).apply {
                        if (exists()) delete()
                        parentFile?.let { if (!it.exists()) it.mkdirs() }
                    }
                    thread {
//                        convertToMP3(File(oneStr), File(outF))
//                        exactorMedia(File(oneStr))
                        extractAudio(oneStr, outF)


                        runOnUiThread {
                            asT.setTitle("提取完成")
                            asT.delayDismissWith(1500) {
                                XPopup.Builder(this@MainActivity)
                                    .asConfirm("温馨提示", "您的文件已保存在：${outF}", null).show()
                            }
                        }
                    }

                } else {
                    asT.dismiss()
                }
            }

            6 -> {
                val asT = XPopup.Builder(this).asLoading("提取中...")
                asT.show()
                val list = FilePickerManager.obtainData()
                if (list.isNotEmpty()) {
                    val oneStr = list[0]
                    val outF = File(
                        AppStorageData.getFileOutFile(),
                        "视频消音/${EggUtil.getFileNameNoEx(File(oneStr).name)}.mp4"
                    ).absolutePath
                    File(outF).apply {
                        if (exists()) delete()
                        parentFile?.let { if (!it.exists()) it.mkdirs() }
                    }
                    thread {
                        extractVideo(oneStr, outF)
                        runOnUiThread {
                            asT.setTitle("提取完成")
                            asT.delayDismissWith(1500) {
                                XPopup.Builder(this@MainActivity)
                                    .asConfirm("温馨提示", "您的文件已保存在：${outF}", null).show()
                            }
                        }
                    }
                } else {
                    asT.dismiss()
                }
            }
        }
    }


    private fun extractVideo(sourcePath: String, outPath: String) {
        val mediaExtractor = MediaExtractor()
        var mediaMuxer: MediaMuxer? = null
        try {
            // 设置视频源
            mediaExtractor.setDataSource(sourcePath)
            // 轨道索引 ID
            var videoIndex = -1
            // 视频轨道格式信息
            var mediaFormat: MediaFormat? = null
            // 数据源的轨道数（一般有视频，音频，字幕等）
            val trackCount = mediaExtractor.trackCount
            // 循环轨道数，找到我们想要的视频轨
            for (i in 0 until trackCount) {
                val format = mediaExtractor.getTrackFormat(i)
                val mimeType = format.getString(MediaFormat.KEY_MIME)
                // 找到要分离的视频轨
                if (mimeType!!.startsWith("video/")) {
                    videoIndex = i
                    mediaFormat = format
                    break
                }
            }
            if (mediaFormat == null) {
                return
            }

            // 最大缓冲区字节数
            val maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
            // 格式类型
            val mimeType = mediaFormat.getString(MediaFormat.KEY_MIME)
            // 视频的比特率
            var bitRate = 0
            if (mediaFormat.containsKey(MediaFormat.KEY_BIT_RATE)) {
                bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE)
            }
            // 视频宽度
            val width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH)
            // 视频高度
            val height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT)
            // 内容持续时间（以微妙为单位）
            val duration = mediaFormat.getLong(MediaFormat.KEY_DURATION)
            // 视频的帧率
            val frameRate = mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE)
            // 视频内容颜色空间
            val colorFormat = -1
            if (mediaFormat.containsKey(MediaFormat.KEY_COLOR_FORMAT)) {
                mediaFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT)
            }
            // 关键之间的时间间隔
            var iFrameInterval = -1
            if (mediaFormat.containsKey(MediaFormat.KEY_I_FRAME_INTERVAL)) {
                iFrameInterval = mediaFormat.getInteger(MediaFormat.KEY_I_FRAME_INTERVAL)
            }
            //  视频旋转顺时针角度
            var rotation = -1
            if (mediaFormat.containsKey(MediaFormat.KEY_ROTATION)) {
                rotation = mediaFormat.getInteger(MediaFormat.KEY_ROTATION)
            }
            // 比特率模式
            var bitRateMode = -1
            if (mediaFormat.containsKey(MediaFormat.KEY_BITRATE_MODE)) {
                bitRateMode = mediaFormat.getInteger(MediaFormat.KEY_BITRATE_MODE)
            }
            /* logger.info(
                 "mimeType:{}, maxInputSize:{}, bitRate:{}, width:{}, height:{}" +
                         ", duration:{}ms, frameRate:{}, colorFormat:{}, iFrameInterval:{}" +
                         ", rotation:{}, bitRateMode:{}",
                 mimeType,
                 maxInputSize,
                 bitRate,
                 width,
                 height,
                 duration / 1000,
                 frameRate,
                 colorFormat,
                 iFrameInterval,
                 rotation,
                 bitRateMode
             )*/
            //切换视频的轨道
            mediaExtractor.selectTrack(videoIndex)
            mediaMuxer = MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            //将视频轨添加到 MediaMuxer，并返回新的轨道
            val trackIndex = mediaMuxer.addTrack(mediaFormat)
            val byteBuffer = ByteBuffer.allocate(maxInputSize)
            val bufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
            // 开始合成
            mediaMuxer.start()
            while (true) {
                // 检索当前编码的样本并将其存储在字节缓冲区中
                val readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0)
                //  如果没有可获取的样本则退出循环
                if (readSampleSize < 0) {
                    mediaExtractor.unselectTrack(videoIndex)
                    break
                }
                // 设置样本编码信息
                bufferInfo.size = readSampleSize
                bufferInfo.offset = 0
                bufferInfo.flags = mediaExtractor.sampleFlags
                bufferInfo.presentationTimeUs = mediaExtractor.sampleTime
                //写入样本数据
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo)
                //推进到下一个样本，类似快进
                mediaExtractor.advance()
            }
        } catch (e: IOException) {

        } finally {
            if (mediaMuxer != null) {
                try {
                    mediaMuxer.stop()
                    mediaMuxer.release()
                } catch (e: java.lang.IllegalStateException) {

                }
            }
            mediaExtractor.release()
        }
    }

    private fun extractAudio(sourcePath: String, outPath: String) {
        val mediaExtractor = MediaExtractor()
        var mediaMuxer: MediaMuxer? = null
        try {
            mediaExtractor.setDataSource(sourcePath)
            val trackCount = mediaExtractor.trackCount
            var mediaFormat: MediaFormat? = null
            var audioIndex = -1
            for (i in 0 until trackCount) {
                val format = mediaExtractor.getTrackFormat(i)
                val mimeType = format.getString(MediaFormat.KEY_MIME)
                if (mimeType!!.startsWith("audio/")) {
                    audioIndex = i
                    mediaFormat = format
                    break
                }
            }
            if (mediaFormat == null) {
                return
            }
            // MediaFormat 封装了媒体数据（音频，视频，字幕）格式的信息，所有信息都以键值对形式表示。
            // MediaFormat 中定义的 key 对于不同媒体数据并不是全部通用的，某些 key 只适用于特定媒体数据。
            // 最大缓冲区字节数
            val maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
            // 格式
            val mimeType = mediaFormat.getString(MediaFormat.KEY_MIME)
            // 比特率
            val bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE)
            // 通道数
            val channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            // 采样率
            val sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            // 内容持续时间（以微妙为单位）
            val duration = mediaFormat.getLong(MediaFormat.KEY_DURATION)
            /*logger.info(
                "maxInputSize:{}, mimeType:{}, bitRate:{}, channelCount:{}" +
                        ", sampleRate:{}, duration:{}ms",
                maxInputSize,
                mimeType,
                bitRate,
                channelCount,
                sampleRate,
                duration / 1000
            )*/
            mediaExtractor.selectTrack(audioIndex)
            mediaMuxer = MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val trackIndex = mediaMuxer.addTrack(mediaFormat)
            val byteBuffer = ByteBuffer.allocate(maxInputSize)
            val bufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
            mediaMuxer.start()
            while (true) {
                val readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleSize < 0) {
                    mediaExtractor.unselectTrack(audioIndex)
                    break
                }
                bufferInfo.size = readSampleSize
                bufferInfo.flags = mediaExtractor.sampleFlags
                bufferInfo.offset = 0
                bufferInfo.presentationTimeUs = mediaExtractor.sampleTime
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo)
                mediaExtractor.advance()
            }
        } catch (e: IOException) {
        } finally {
            if (mediaMuxer != null) {
                try {
                    mediaMuxer.stop()
                    mediaMuxer.release()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
            mediaExtractor.release()
        }
    }


    fun getDuration(videoPath: String?): Int {
        return try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(videoPath)
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    private fun initUi() {
        EggUtil.loadIcon(this, AppSetting.colorStress, img_home, img_file_lobby, img_me)
        EggUtil.loadIcon(this, AppSetting.colorStress, search_icon, img_file_lobby, img_me)
        EggUtil.setViewRadius(
            img_file_lobby_bg,
            AppSetting.colorTransTress,
            1,
            AppSetting.colorTransTress,
            60f
        )

        EggUtil.setViewRadius(
            img_home_bg,
            AppSetting.colorTransTress,
            1,
            AppSetting.colorTransTress,
            60f
        )

        EggUtil.setViewRadius(
            img_me_bg,
            AppSetting.colorTransTress,
            1,
            AppSetting.colorTransTress,
            60f
        )
        img_file_lobby_bg.visibility = View.GONE
        img_me_bg.visibility = View.GONE
    }

    private fun initEvent() {
        switchFragment(fragmentHome!!)
        main_home_on.setOnClickListener {
            println(isAnimRunning)
            if (isAnimRunning) return@setOnClickListener
            if (currentPageInt == 0) return@setOnClickListener
            switchFragment(fragmentHome!!)
            switchNav(img_home_bg)
            currentPageInt = 0
        }

        main_file_lobby_on.setOnClickListener {
            if (isAnimRunning) return@setOnClickListener
            if (currentPageInt == 1) return@setOnClickListener
            switchFragment(fragmentFileLobby!!)
            switchNav(img_file_lobby_bg)
            currentPageInt = 1
        }

        main_me_on.setOnClickListener {
            if (isAnimRunning) return@setOnClickListener
            if (currentPageInt == 2) return@setOnClickListener
            switchFragment(fragmentMe!!)
            switchNav(img_me_bg)
            currentPageInt = 2
        }


        search_edit.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val ja = JSONArray()
                ja.add("all")
                AppList.typeAppsMessage = ja
                val intent = Intent(this, AppList::class.java)
                intent.putExtra("search_app_name", search_edit.text.toString())
                startActivity(intent)
                EggUtil.hideKeyboard(this)
            }
            false
        }
        search_icon.setOnClickListener {
            val ja = JSONArray()
            ja.add("all")
            AppList.typeAppsMessage = ja
            val intent = Intent(this, AppList::class.java)
            intent.putExtra("search_app_name", search_edit.text.toString())
            startActivity(intent)
        }
    }

    private fun switchFragment(targetFragment: Fragment) {
        supportFragmentManager.popBackStack(null, 1)
        transaction = supportFragmentManager.beginTransaction().setCustomAnimations(
            R.anim.slide_left_in,
            R.anim.slide_right_out,
            R.anim.slide_right_in,
            R.anim.slide_left_out
        )
        transaction!!.replace(R.id.main_fragment_view, targetFragment).commit()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val home = Intent(Intent.ACTION_MAIN)
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
        return true
    }

    private fun setNavAnimStyle(v: View, isIn: Boolean) {
        val duration: Long = 500
        if (isIn) {
            YoYo.with(Techniques.SlideInUp).duration(duration).onStart {
                isAnimRunning = true
                v.visibility = View.VISIBLE
            }.onEnd {
                isAnimRunning = false
            }.playOn(v)
        } else {
            YoYo.with(Techniques.SlideOutDown).duration(duration).onStart {
                isAnimRunning = true
            }.onEnd {
                isAnimRunning = false
                v.visibility = View.GONE
            }.playOn(v)
        }
    }

    private fun switchNav(v: View) {
        elseHideNav(v)
    }

    private fun elseHideNav(v: View) {
        var currI = 0
        val arrayListBg = arrayListOf<View>(img_home_bg, img_file_lobby_bg, img_me_bg)
        val arrayList = arrayListOf<TextView>(img_home, img_file_lobby, img_me)
        arrayListBg.forEach {
            if (v.id != it.id) {
                setNavAnimStyle(it, false)
                arrayList[currI].setTextColor(Color.parseColor(AppSetting.colorGray))
            } else {
                setNavAnimStyle(v, true)
                arrayList[currI].setTextColor(Color.parseColor(AppSetting.colorStress))
            }
            currI++
        }
    }
}