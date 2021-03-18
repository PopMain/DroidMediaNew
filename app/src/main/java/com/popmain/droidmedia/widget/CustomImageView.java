package com.popmain.droidmedia.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.popmain.droidmedia.R;

/**
 * Created by wzx on 2017/10/24.
 */

public class CustomImageView extends View {

    private Bitmap mBitmap;

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }


    public CustomImageView(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                invalidate();
                super.onPostExecute(o);
            }
        };
        asyncTask.execute();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
         if (mBitmap != null) {
             Rect dest = new Rect(0, 0, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
             ViewGroup.LayoutParams layoutParams = getLayoutParams();
             layoutParams.height = dest.height();
             layoutParams.width = dest.width();
             requestLayout();
             canvas.drawBitmap(mBitmap, null, dest, paint);
         }
    }
}
