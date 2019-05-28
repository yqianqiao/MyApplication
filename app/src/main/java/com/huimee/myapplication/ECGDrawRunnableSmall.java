//package com.huimee.myapplication;
//
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PixelFormat;
//import android.graphics.Rect;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//
//import java.util.concurrent.LinkedBlockingQueue;
//
///**
// * Created by Administrator on 2018/1/8.
// */
//
//public class ECGDrawRunnableSmall implements Runnable {
//
//    private Paint mPaint;
//    private Paint mPaintText;
//    private int WAVE_PADDING = 1;
//    private int TEXT_STROKE_WIDTH = 20;
//    private int STROKE_WIDTH = 3;
//    private LinkedBlockingQueue<Integer> mQueue;
//    private SurfaceHolder mSurfaceHolder;
//    private SurfaceView mSurfaceView;
//    private WaveParse mWaveParas;
//
//    public boolean isStop;
//
//    public String nameText;
//
//
//    public ECGDrawRunnableSmall(LinkedBlockingQueue<Integer> queue,
//                                SurfaceView surfaceView, SurfaceHolder surfaceHolder,
//                                WaveParse waveParas, String nameText, int color) {
//        mPaint = new Paint();
//        mPaint.setColor(color);
//        mPaint.setStrokeWidth(STROKE_WIDTH);
//        mPaint.setAntiAlias(true);  //抗锯齿
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setStyle(Paint.Style.STROKE);//描边
//
//
//        mPaintText = new Paint();
//        mPaintText.setColor(color);
//        mPaintText.setTextSize(TEXT_STROKE_WIDTH);
//        mPaintText.setStrokeWidth(STROKE_WIDTH);
//        mPaint.setAntiAlias(true);
//
//        this.nameText = nameText;
//
//        this.mQueue = queue;
//        this.mSurfaceHolder = surfaceHolder;
//        this.mSurfaceView = surfaceView;
//        this.mWaveParas = waveParas;
//
//        surfaceView.setZOrderOnTop(true);
//        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
//        surfaceView.setBackgroundColor(Color.parseColor("#00000000"));
//
//
//    }
//
//    private float temp = 0;
//    private float[] tempArray = new float[10];
//    private int counter = 0;  //计数器
//    private Path mPath = new Path();
//    private float oldPointx = 0;
//    private float newPointx = 0;
//    private float oldPointy = 0;
//    private float newPointy = 0;
//    private Canvas mCanvas;
//
//    @Override
//    public void run() {
//
//        while (true) {
//            Log.e("yx", "isStop: "+ isStop);
//            Log.e("yx", "mQueue: "+ mQueue.size());
//            if (isStop) {
//                mQueue.clear();
//                break;
//            }
//            for (counter = 0; counter < mWaveParas.bufferCounter; counter++) {
//                try {
//                    temp = mQueue.take();
//                    //    Log.e("516", "mQueue:" + mQueue.size());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                tempArray[counter] = temp;
//            }
//            synchronized (this) {
//                mCanvas = mSurfaceHolder.lockCanvas(new Rect((int) oldPointx, 0, (int) oldPointx + STROKE_WIDTH * 5, mSurfaceView.getHeight() - WAVE_PADDING));
//                if (mCanvas != null) {
//                    mCanvas.drawColor(Color.BLACK);
//                    mPath.reset();
//                    Log.e("yx", "tempArray[counter]: "+tempArray[counter]);
//                    for (counter = 0; counter < mWaveParas.bufferCounter; counter++) {
//                        newPointx = (oldPointx + mWaveParas.xStep) % mSurfaceView.getWidth();
//                        if (tempArray[counter] <= 0) {
//                            newPointy = mSurfaceView.getHeight() / 2;
//                        } else {
//                            int i_00016 = (int) (0.0016 * tempArray[counter] * (mSurfaceView.getHeight() - WAVE_PADDING * 2));
//                            int i_0004 = (int) (0.004 * tempArray[counter] * (mSurfaceView.getHeight() - WAVE_PADDING * 2));
//                            newPointy = (mSurfaceView.getHeight() - WAVE_PADDING * 2 - (int) (0.0016 * tempArray[counter] * (mSurfaceView.getHeight() - WAVE_PADDING * 2)) - (i_0004 - i_00016)) + 10;
//                        }
//                        mPath.moveTo(oldPointx, oldPointy);
//                        mPath.quadTo((newPointx + oldPointx) / 2, (newPointy + oldPointy) / 2, newPointx, newPointy);
//
//                        oldPointx = newPointx;
//                        oldPointy = newPointy;
//
//                    }
//                    mCanvas.drawPath(mPath, mPaint);
//                    mCanvas.save();
//                    mCanvas.drawText(nameText, 10, 30, mPaintText);
//                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
//                }
//            }
//        }
//    }
//
//}