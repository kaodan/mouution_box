package com.srcbox.file.util.resource.extract

class ResourceExtractManager {

    private val resourceExtractTasks = ArrayList<ResourceExtractTask>()
    private var successTaskNum = 0
    private val failTaskNum = 0
    private val startTaskNum = 0
    private val endTaskNum = 0
    private val pauseTaskNum = 0

    fun addTask(resourceExtractTask: ResourceExtractTask) {
        resourceExtractTasks.add(resourceExtractTask)
    }

    fun removeTask(position: Int) {
        resourceExtractTasks.removeAt(position)
        resourceExtractTasks[position].removeTask()
    }

    fun startTask(position: Int, progress: (num:Int) -> Unit) {
        Thread {
            resourceExtractTasks[position].startTask() {
                successTaskNum++
                progress(startTaskNum)
            }
        }.start()
    }

    fun pause(position: Int) {
        resourceExtractTasks[position].pause()
    }

    fun startTaskAll() {
        resourceExtractTasks.forEach {}
    }

    fun endTaskAll() {
        resourceExtractTasks.forEach {}
    }
}