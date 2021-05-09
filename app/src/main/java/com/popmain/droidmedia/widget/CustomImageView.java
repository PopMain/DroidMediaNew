package com.popmain.droidmedia.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.popmain.droidmedia.DroidMediaApplication;
import com.popmain.droidmedia.R;
import com.popmain.droidmedia.util.AsyncRunnableWorker;

import java.lang.ref.WeakReference;

/**
 * Created by wzx on 2017/10/24.
 */

public class CustomImageView extends View {

    private Bitmap mBitmap;
    private final Paint paint = new Paint();
    private final Rect dest = new Rect();

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }


    public CustomImageView(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        AsyncRunnableWorker.execute(new InnerRunnable(this));
    }

    private void setBitmapData(Bitmap bitmap) {
        mBitmap = bitmap;
        if (Looper.myLooper() == Looper.getMainLooper()) {
//            invalidate();
            requestLayout();
        } else {
//            postInvalidate();
            post(this::requestLayout);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mBitmap != null) {
            int with = mBitmap.getWidth() / 2;
            int height = mBitmap.getHeight() / 2;
            setMeasuredDimension(with, height);
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
         if (mBitmap != null) {
             dest.set(0, 0, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
             canvas.drawBitmap(mBitmap, null, dest, paint);
         }
    }


    static class InnerRunnable implements Runnable {

        final WeakReference<CustomImageView> viewWeakRef;

        InnerRunnable(CustomImageView view) {
            viewWeakRef = new WeakReference(view);
        }

        @Override
        public void run() {
            Bitmap bitmap = BitmapFactory.decodeResource(DroidMediaApplication.getsDroidMediaApplication().getResources(), R.drawable.image);
            CustomImageView customImageView = viewWeakRef.get();
            if (customImageView != null) {
                customImageView.setBitmapData(bitmap);
            }
        }
    }
}
