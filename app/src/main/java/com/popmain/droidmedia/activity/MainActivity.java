package com.popmain.droidmedia.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.popmain.droidmedia.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE_AUDIO_RECORD = 2;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_go2_draw_image).setOnClickListener(this);
        findViewById(R.id.btn_go2_audio_recorder).setOnClickListener(this);
        findViewById(R.id.btn_go2_camera_api).setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_go2_draw_image:
                go2DrawImage();
                break;
            case R.id.btn_go2_audio_recorder:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        go2AudioRecorder();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE_AUDIO_RECORD);
                    }
                } else {
                    go2AudioRecorder();
                }
                break;
            case R.id.btn_go2_camera_api:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        go2CamerApi();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
                    }
                } else {
                    go2CamerApi();
                }
                break;
        }
    }

    private void go2DrawImage() {
        Intent intent = new Intent(this, DrawImageActivity.class);
        startActivity(intent);
    }


    private void go2AudioRecorder() {
        Intent intent = new Intent(this, AudioActivity.class);
        startActivity(intent);
    }

    private void go2CamerApi() {
        Intent intent = new Intent(this, CameraAPIActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_AUDIO_RECORD:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    go2AudioRecorder();
                }
                break;
            case PERMISSION_REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    go2CamerApi();
                }
                break;
        }
    }
}
