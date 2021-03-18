package com.popmain.droidmedia.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.popmain.droidmedia.R;
import com.popmain.droidmedia.sdk.CameraSDK;
import com.popmain.droidmedia.widget.CameraPreviewView;

public class CameraAPIActivity extends AppCompatActivity implements View.OnClickListener{

    private CameraPreviewView mCameraPreviewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_api);
        mCameraPreviewView = (CameraPreviewView) findViewById(R.id.camera_preview);
        findViewById(R.id.btn_switch_camera).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_switch_camera:
                CameraSDK.switchCamera(mCameraPreviewView.getHolder());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }
}
