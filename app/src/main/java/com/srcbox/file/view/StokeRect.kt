package com.srcbox.file.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

@SuppressLint("ViewConstructor")
class StokeRect(
    context: Context,
    private var mLeft: Float,
    private var mTop: Float,
    private var mRight: Float,
    private var mBottom: Float
) : View(context) {
    private var color = "#2196f3"
    private var stokeWidth = 4f

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.strokeWidth = stokeWidth
        paint.color = Color.parseColor(color)
        paint.style = Paint.Style.FILL
        canvas?.drawRect(mLeft, mTop, mRight, mBottom, paint);
    }

    fun setStokeColor(color: String) {
        this.color = color
        invalidate()
    }

    fun setBound(mLeft: Float, mTop: Float, mRight: Float, mBottom: Float) {
        this.mLeft = mLeft
        this.mTop = mTop
        this.mRight = mRight
        this.mBottom = mBottom
        invalidate()
    }

    fun setStokeWidth(f:Float){
        this.stokeWidth = f
        invalidate()
    }
}