package com.egg.extractmanager

import com.srcbox.file.ui.extractManager.code.ExtractTaskMessage
import java.io.Serializable

interface ExtractInstance : Serializable {

    //开始
    fun start()

    //暂停
    fun pause()

    //关闭
    fun cancel()

    //设置监听
    fun setListener(extractListener: ExtractListener?)

    //获得监听
    fun getListener(): ExtractListener?

    //获取状态
    fun getState(): Int

    //获取信息
    fun getMessages(): ExtractTaskMessage

    //是否为新任务
    fun getIsNewTask(): Boolean

    //是否为新任务
    fun setIsNewTask(boolean: Boolean)

    //设置当前索引
    fun setPosition(int: Int)

    //初始化状态
    fun initData()
}