package com.popmain.droidmedia.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by wzx on 2017/11/11.
 */

public class SurfaceSinView extends SurfaceView implements SurfaceHolder.Callback , Runnable {

    private SurfaceHolder mSurfaceHolder;

    private volatile boolean mIsDrawing;

    private Paint mPaint;

    private Path mPath;

    private Canvas mCanvas;

    private volatile  int mX, mY;

    private int mScreenWidth;
    private volatile boolean mReserve;
    private volatile int mDx;

    public SurfaceSinView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SurfaceSinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        mPaint = new Paint();
        mPath = new Path();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(8);
        mPaint.setAntiAlias(true);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            drawSomething();
            if (!mReserve) {
                mX += 1;
                mY = (int) (100 * Math.sin(2 * mX * Math.PI / 180 + mDx) + 300);
            } else {
                mX -= 1;
                mY = (int) (100 * Math.cos(2 * mX * Math.PI / 180 + mDx) + 300);
            }
            mPath.lineTo(mX, mY);
            if (mX > mScreenWidth) {
                mReserve = true;
            }
            if (mX < 0) {
                mReserve = false;
                mDx += 10;
            }
        }
    }

    private void drawSomething() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(Color.WHITE);
            mCanvas.drawPath(mPath, mPaint);
        } catch (Exception e) {

        } finally {
            if (mSurfaceHolder != null && mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}
