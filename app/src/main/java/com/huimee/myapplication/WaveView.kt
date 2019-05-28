package com.huimee.myapplication

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 *   Created by YX on 2019/5/10 15:47.
 */
class WaveView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    public var mData: LinkedBlockingQueue<Int>? = null
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var wavePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var oldPointx = 0f

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.RED
        mPaint.strokeWidth = 3f
        wavePaint.style = Paint.Style.STROKE
        wavePaint.color = Color.BLUE
        wavePaint.strokeWidth = 10f

    }

    private var space = width / 2f
    private var isAdd = true
    var aaa = 12f
    override fun onDraw(canvas: Canvas) {
        canvas.translate(space, height / 2f)
        canvas.drawLine(-width / 2f, 0f, width / 2f, 0f, mPaint)
    }

    public fun onTranslate(){
        space += 20f
        isAdd = false
        invalidate()
    }

    public fun setData() {
        mData = LinkedBlockingQueue<Int>()
        Thread(Runnable {
            while (true) {
                Thread.sleep(1000)
                mData?.put(Random().nextInt(200) + 10)
                (context as Activity).runOnUiThread {
                    invalidate()
                }
            }
        }).start()
    }
}