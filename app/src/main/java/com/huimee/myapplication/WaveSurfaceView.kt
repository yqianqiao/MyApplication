package com.huimee.myapplication

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.lang.NullPointerException
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 *   Created by YX on 2019/5/8 17:19.
 */
class WaveSurfaceView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback, Runnable {

    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var wavePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mThread: Thread? = null
    private var isDrawing = false

    public var mData: LinkedBlockingQueue<Byte> = LinkedBlockingQueue(100)

    public var isStart = true

    private var oldPointx = 0f
    private var newPointx = 0f
    private var oldPointy = 0f
    private var newPointy = 0f
    private var space = 13


    init {
        holder.addCallback(this)
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.RED
        mPaint.strokeWidth = 3f

        wavePaint.style = Paint.Style.STROKE
        wavePaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        wavePaint.strokeWidth = 10f


    }


    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        isDrawing = true
        mThread = Thread(this).apply { start() }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        isDrawing = false
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {

    }

    var take = 0
    private var canvas: Canvas? = null
    override fun run() {
        while (isDrawing) {
            if (mData.isNullOrEmpty()) {
                continue
            }
//            canvas = holder.lockCanvas(Rect(oldPointx.toInt(), 0, oldPointx.toInt() + space, height))
            canvas = holder.lockCanvas(Rect(0, 0, width, height))

            canvas?.let {
                it.drawColor(Color.WHITE)
                it.translate(0f, height / 2f)
                for (i in mData) {
                    it.drawLine(
                            oldPointx + wavePaint.strokeWidth,
                            -i.toFloat(),
                            oldPointx + wavePaint.strokeWidth,
                            i.toFloat(),
                            wavePaint
                    )

                    if (oldPointx >= width / 2 - wavePaint.strokeWidth / 2)
                        mData.take()
                    oldPointx += space
                }
                it.drawLine(
                        oldPointx + wavePaint.strokeWidth,
                        -height / 2f,
                        oldPointx + wavePaint.strokeWidth,
                        height / 2f,
                        mPaint
                )


                oldPointx = 0f

                it.drawLine(0f, 0f, width.toFloat(), 0f, mPaint)
                it.save()
                holder.unlockCanvasAndPost(it)
            }


        }
//


    }

    fun setData(data: ByteArray) {

        for (i in data) {
            mData.put(i)
        }

    }

//    fun setData() {
//       for(i in 0..100){
//           mData.put(Random().nextInt(200) + 10)
//       }

//    }

}