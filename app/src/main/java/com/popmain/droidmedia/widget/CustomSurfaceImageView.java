package com.popmain.droidmedia.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.popmain.droidmedia.R;


/**
 * Created by wzx on 2017/10/25.
 */

public class CustomSurfaceImageView extends SurfaceView implements SurfaceHolder.Callback , Runnable{


    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;

    private BitmapFactory.Options mBitmapOptions;

    private int mBitmapWidth;
    private int mBitmapHeight;


    public CustomSurfaceImageView(Context context) {
        this(context, null);
    }

    public CustomSurfaceImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CustomSurfaceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mBitmapOptions = new BitmapFactory.Options();
        mBitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.image, mBitmapOptions);
        float scale = (float) mBitmapOptions.inTargetDensity / mBitmapOptions.inDensity;
        mBitmapWidth = (int) (mBitmapOptions.outWidth * scale + 0.5f);
        mBitmapHeight = (int) (mBitmapOptions.outHeight * scale + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int finalWidth = 0;
        int finalHeight = 0;

        if (widthMeasureMode == MeasureSpec.EXACTLY) {
            finalWidth = widthSize;
        } else if (widthMeasureMode == MeasureSpec.AT_MOST) {
            finalWidth = mBitmapWidth / 2;
        }

        if (heightMeasureMode == MeasureSpec.EXACTLY) {
            finalHeight = heightSize;
        } else if (heightMeasureMode == MeasureSpec.AT_MOST) {
            finalHeight = mBitmapHeight / 2;
        }

        setMeasuredDimension(finalWidth, finalHeight);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void run() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        mCanvas = mSurfaceHolder.lockCanvas();
        Rect dest = new Rect(0, 0, mBitmapWidth / 2, mBitmapHeight / 2);
        Paint paint = new Paint();
        mCanvas.drawBitmap(bitmap, null, dest, paint);
        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
    }
}
