package com.egg.extractmanager

interface ExtractListener {
    fun onProgress(float: Float)
    fun onStart()
    fun onPause()
    fun onCancel()
    fun onSuccess()
}