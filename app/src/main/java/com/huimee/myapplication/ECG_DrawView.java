package com.huimee.myapplication;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 心电View
 * Created by Administrator on 2019/4/19.
 */

public class ECG_DrawView extends SurfaceView implements SurfaceHolder.Callback {

    public DrawBGThread drawBGThread;
    private boolean isFirstInit;
    private Bitmap backgroundBitmap;
    public Rect BG_Rect;
    private SurfaceHolder holder;
    public boolean isDraw_BG;
    public int wide_Color;
    public int bk_Color;
    public int thin_Color;
    private Bitmap waveBitmap;
    private Canvas canvas_Wav;
    private boolean isStart;
    private long BETWEEN_TIME = 31;
    private long BG_TIME = 20;
    private Handler handler;
    private LinkedBlockingQueue<Integer> mWaveQueue;

    //当前绘制的波形位置
    private int drawPos;
    /**
     * 绘制波形的结束位置
     */
    private int drawRight;
    /**
     * 绘制波形的开始位置
     */

    private int drawLeft = 0;
    //基线
    private int drawBaseline = 0;

    private Paint wave_Pen;
    private Rect deleteLine_Rect;

    private float lastEcgData;

    public ECG_DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        (this.holder = this.getHolder()).addCallback((SurfaceHolder.Callback) this);
        this.handler = new Handler();
        mWaveQueue = new LinkedBlockingQueue<>();
        this.wave_Pen = new Paint();
        this.wave_Pen.setAntiAlias(true);
        this.wave_Pen.setDither(true);
        this.wave_Pen.setStrokeWidth(1.5f);
        this.wave_Pen.setColor(Color.rgb(255, 255, 255));
        this.deleteLine_Rect = new Rect();
    }

    /**
     * 添加心电数据
     *
     * @param dat
     */
    public void add(int dat) {
        try {
            mWaveQueue.put(dat);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 开始绘制波形
     */
    public void start() {
        mWaveQueue.clear();
        handler.postDelayed(drawRunnable, BETWEEN_TIME);
        isStart = true;
    }

    Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(drawRunnable, BETWEEN_TIME);
            for (int i = 0; i < 8; i++) {
                try {
                    Integer take = mWaveQueue.poll();
                    drawDeleteLine(drawPos, backgroundBitmap, canvas_Wav);
                    drawWave(take, canvas_Wav);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    };

    /**
     * 绘制波形
     */
    private void drawWave(int ecgData, Canvas canvas_Wav) {

//        float ecgIndex = getDJMEcgIndex(ecgData);
        float ecgIndex = getWXBEcgIndex(ecgData);
        canvas_Wav.drawLine((float) (drawPos), lastEcgData, (float) (this.drawPos + 1), ecgIndex, this.wave_Pen);
        lastEcgData = ecgIndex;
        drawPos++;
        if (drawPos >= drawRight) {
            drawPos = drawLeft;
        }
    }

    public int gain = 1;
    public float adjustParameters = 1.3F;
    //    public float adjustParameters=1.5F;
    public int baselineValues;

    /**
     * DJM绘制心电图的位置
     */
    public float getDJMEcgIndex(int ecgData) {
        if (ecgData <= 0 || ecgData == 255) {
            ecgData = drawBaseline;
        }
        int reversalEcgData = -ecgData + drawBaseline;
        reversalEcgData = (int) (reversalEcgData * gain * adjustParameters);
        int poor = (int) (drawBaseline - (baselineValues * gain * adjustParameters));
        int ecgIndex = reversalEcgData + poor;
        return ecgIndex;
    }

    /**
     * WXB绘制心电图的位置
     */
    public float getWXBEcgIndex(int ecgData) {
        if (ecgData <= 0 || ecgData == 255) {
            ecgData = drawBaseline;
        }
        int reversalEcgData = (int) (ecgData * gain * adjustParameters);
        int poor = (int) (drawBaseline - (baselineValues * gain * adjustParameters));
        int ecgIndex = reversalEcgData + poor;
        return ecgIndex;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!this.isFirstInit) {
            this.init(this.getWidth(), this.getHeight());
        }
        if (this.drawBGThread == null) {
            this.drawBGThread = new DrawBGThread();
            this.drawBGThread.isRun = true;
            this.drawBGThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.drawBGThread != null) {
            this.drawBGThread.isRun = false;
            this.drawBGThread = null;
        }
    }

    private void init(final int screenWidth, final int viewHight) {
        final int mantissaWidth = screenWidth / megaPixel * megaPixel;  //去除尾数后的宽度
        final int averageHeight = viewHight / megaPixel * megaPixel;  //去除尾数后的平均高度
        final int bG_RectStart_x = (screenWidth - mantissaWidth) / 2;  //背景的开始x坐标
        final int bG_RectStart_y = viewHight - averageHeight;  //背景的开始y坐标
        this.BG_Rect = new Rect(bG_RectStart_x, bG_RectStart_y, bG_RectStart_x + mantissaWidth, viewHight);
        this.backgroundBitmap = Bitmap.createBitmap(screenWidth, viewHight, Bitmap.Config.ARGB_8888);
        this.get_Configuration_Params();
        isFirstInit = true;
        drawRight = this.BG_Rect.right;
        drawBaseline = viewHight / 2;
        baselineValues = drawBaseline - 128;
        lastEcgData = drawBaseline;
    }

    /**
     * 清除当前绘制区域内容
     *
     * @param drawPos
     * @param bitmap
     * @param canvas
     */
    public void drawDeleteLine(final int drawPos, final Bitmap bitmap, final Canvas canvas) {
        this.deleteLine_Rect.set(drawPos, this.BG_Rect.top, drawPos + 5, this.BG_Rect.bottom);
        canvas.drawBitmap(bitmap, this.deleteLine_Rect, this.deleteLine_Rect, (Paint) null);
    }


    public int bigPixel = 10;
    public int megaPixel = bigPixel * 5;

    /**
     * 绘制格式背景
     *
     * @param rect
     * @param b
     * @return
     */
    public boolean draw_BG(final Rect rect, final boolean b) {
        if (!this.isDraw_BG) {
            final Canvas canvas = new Canvas();
            final Paint paint = new Paint();
            final Paint paint2 = new Paint();
            paint.setColor(thin_Color);
            paint2.setColor(wide_Color);
            canvas.setBitmap(this.backgroundBitmap);
            canvas.drawColor(bk_Color);
            for (int i = rect.left; i < rect.right; i += bigPixel) {
                if ((i - rect.left) % megaPixel != 0) {
                    canvas.drawLine((float) i, (float) rect.top, (float) i, (float) rect.bottom, paint);
                }
            }
            for (int j = rect.top; j < rect.bottom; j += bigPixel) {
                if ((j - rect.top) % megaPixel != 0) {
                    canvas.drawLine((float) rect.left, (float) j, (float) rect.right, (float) j, paint);
                }
            }
            for (int k = rect.left; k <= rect.right; k += megaPixel) {
                canvas.drawLine((float) k, (float) rect.top, (float) k, (float) rect.bottom, paint2);
            }
            for (int l = rect.top; l <= rect.bottom; l += megaPixel) {
                canvas.drawLine((float) rect.left, (float) l, (float) rect.right, (float) l, paint2);
            }
            this.isDraw_BG = true;
            this.waveBitmap = Bitmap.createBitmap(this.backgroundBitmap);
            this.canvas_Wav = new Canvas(this.waveBitmap);
        }
        if (b) {
            this.re_FreshBG(null);
        }
        return true;
    }

    /**
     * 提交画布
     *
     * @param rect
     */
    public void re_FreshBG(final Rect rect) {
        final Canvas canvas = null;
        final Canvas canvas2 = null;
        final Canvas canvas3 = null;
        Canvas canvas4 = canvas;
        Canvas canvas5 = canvas2;
        try {
            final SurfaceHolder holder = this.holder;
            canvas4 = canvas;
            canvas5 = canvas2;
            // monitorenter(holder)
            if (null == rect) {
                canvas5 = canvas3;
                try {
                    Canvas canvas6 = canvas5 = this.holder.lockCanvas();
                    canvas6.drawBitmap(this.backgroundBitmap, 0.0f, 0.0f, (Paint) null);

                    canvas5 = canvas6;
                    // monitorexit(holder)
                    return;
                } finally {
                    // monitorexit(holder)
                    canvas4 = canvas5;
                }
            } else {
                canvas5 = canvas3;
                Canvas canvas6 = (canvas5 = this.holder.lockCanvas(rect));
                canvas6.drawBitmap(this.waveBitmap, 0.0f, 0.0f, (Paint) null);
            }

        } catch (Exception ex) {
            canvas5 = canvas4;
            ex.printStackTrace();
        } finally {
            if (canvas5 != null) {
                this.holder.unlockCanvasAndPost(canvas5);
            }
        }
    }

    public void get_Configuration_Params() {
        wide_Color = Color.rgb(102, 174, 163);
        bk_Color = Color.rgb(1, 37, 37);
        thin_Color = Color.rgb(21, 75, 93);
    }


    public void initDraw() {
        get_Configuration_Params();
        this.isDraw_BG = false;
        this.draw_BG(this.BG_Rect, true);
    }


    class DrawBGThread extends Thread {
        public boolean isRun;

        @Override
        public void run() {
            ECG_DrawView.this.initDraw();
            while (this.isRun) {
                if (ECG_DrawView.this.isStart) {
                    ECG_DrawView.this.re_FreshBG(ECG_DrawView.this.BG_Rect);

                } else {
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
