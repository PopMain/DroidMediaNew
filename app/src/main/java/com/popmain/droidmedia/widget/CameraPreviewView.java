package com.popmain.droidmedia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.popmain.droidmedia.sdk.CameraSDK;

import java.io.IOException;

/**
 * Created by wzx on 2017/11/30.
 */

public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;

    public CameraPreviewView(Context context) {
        this(context, null);
    }

    public CameraPreviewView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);

    }

    public CameraPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraSDK.surfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder.removeCallback(this);
        try {
            CameraSDK.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
