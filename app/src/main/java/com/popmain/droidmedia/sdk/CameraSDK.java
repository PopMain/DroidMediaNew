package com.popmain.droidmedia.sdk;


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.popmain.droidmedia.DroidMediaApplication;

import java.io.IOException;



/**
 * Created by paomian on 2017/11/26.
 */

public class CameraSDK {

    private static final String DEBUG_TAG = "CameraSDK";

    public static final int DEFAULT_CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private static Camera.Parameters sParameters;

    private static Camera sCamera;

    private static int mCurrentCameraId = DEFAULT_CAMERA_ID;


    public static Camera getCameraInstance() {
        if (sCamera == null) {
            synchronized (CameraSDK.class) {
                if (sCamera == null) {
                    mCurrentCameraId = DEFAULT_CAMERA_ID;
                    sCamera = Camera.open(DEFAULT_CAMERA_ID);
                    sParameters = sCamera.getParameters();
                }
            }
        }
        return sCamera;
    }

    public static Camera getCameraInstance(int cameraId) {
        if (sCamera == null) {
            synchronized (CameraSDK.class) {
                if (sCamera == null) {
                    mCurrentCameraId = cameraId;
                    sCamera = Camera.open(cameraId);
                    sParameters = sCamera.getParameters();
                }
            }
        }
        return sCamera;
    }

    /**
     * SurfaceHolder#surfaceCreated调用
     *
     * @param holder
     */
    public static void surfaceCreated(SurfaceHolder holder) {
        sCamera = getCameraInstance(Camera.CameraInfo.CAMERA_FACING_FRONT);
        try {
            sCamera.setPreviewDisplay(holder);
            setDisplayOrientation();
            sCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相机资源
     *
     * @throws IOException
     */
    public static void release() throws IOException {
        sCamera.setPreviewDisplay(null);
        sCamera.stopPreview();
        sCamera.release();
        mCurrentCameraId = DEFAULT_CAMERA_ID;
        sCamera = null;
    }

    /**
     * 切换摄像头
     */
    public static void switchCamera(SurfaceHolder surfaceHolder) {
        sCamera.stopPreview();
        sCamera.release();
        sCamera = null;
        if (mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        sCamera = getCameraInstance(mCurrentCameraId);
        try {
            sCamera.setPreviewDisplay(surfaceHolder);
            setDisplayOrientation();
            sCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @core
     * 调整预览方向、拍照的方向
     */
    public static void setDisplayOrientation() {
        Log.d(DEBUG_TAG, "=========================  setDisplayOrientation START =====================");
        //通过相机ID获得相机信息
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(mCurrentCameraId, info);

        //获得当前屏幕方向
        Display display = ((WindowManager) DroidMediaApplication.getsDroidMediaApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
            int degrees = ScrrenOrientation.SCREEN_ORIENTATION_0;
            switch (rotation) {
            case Surface.ROTATION_0:
                // 若屏幕方向与水平轴负方向的夹角为0度，如果方向锁定一直是0
                degrees = ScrrenOrientation.SCREEN_ORIENTATION_0;
                break;
            case Surface.ROTATION_90:
                // 若屏幕方向与水平轴负方向的夹角为90度
                degrees = ScrrenOrientation.SCREEN_ORIENTATION_90;
                break;
            case Surface.ROTATION_180:
                // 若屏幕方向与水平轴负方向的夹角为180度
                degrees = ScrrenOrientation.SCREEN_ORIENTATION_180;
                break;
            case Surface.ROTATION_270:
                // 若屏幕方向与水平轴负方向的夹角为270度
                degrees = ScrrenOrientation.SCREEN_ORIENTATION_270;
                break;
        }
        Log.d(DEBUG_TAG, "SCREEN_ROTATION  " + rotation + (mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT ? "  前置" : "  后置") + "  CAMERA ORIENTATION " + info.orientation);
        int cameraRotation = calcCameraRotation(info, degrees);
        Log.d(DEBUG_TAG, "CAMERA_ROTATION " + cameraRotation);
        sParameters.setRotation(cameraRotation);
        sCamera.setParameters(sParameters);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            Log.d(DEBUG_TAG, "FACING FRONT");
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror (镜像补偿：前置的画面是有被镜像翻转的)
        } else {  // back-facing
            Log.d(DEBUG_TAG, "FACING BACK");
            result = (info.orientation - degrees + 360) % 360;
        }
        Log.d(DEBUG_TAG, "DISPLAY_ORIENTATION_RESULT " + result);
        sCamera.setDisplayOrientation(result);
        Log.d(DEBUG_TAG, "=========================  setDisplayOrientation END =====================");
    }


    /**
     * 计算camera rotation需要的角度 [0, 360]
     *
     * @param cameraInfo               Camera.CameraInfo
     * @param screenOrientationDegrees 屏幕旋转角度
     * @return
     */
    private static int calcCameraRotation(Camera.CameraInfo cameraInfo, int screenOrientationDegrees) {
        Log.d(DEBUG_TAG, "calcCameraRotation  facing : " + (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT ? "  前置" : "  后置") + "  screenOrientationDegrees  " + screenOrientationDegrees);
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (cameraInfo.orientation + screenOrientationDegrees) % 360;
        } else {  // back-facing
            final int landscapeFlip = isLandscape(screenOrientationDegrees) ? 180 : 0;
            return (cameraInfo.orientation + screenOrientationDegrees + landscapeFlip) % 360;
        }
    }

    private static boolean isLandscape(int orientationDegrees) {
        return (orientationDegrees == ScrrenOrientation.SCREEN_ORIENTATION_90 ||
                orientationDegrees == ScrrenOrientation.SCREEN_ORIENTATION_270);
    }

    /**
     * 获取相机sensor个数
     *
     * @return
     */
    public static int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }

    public static class ScrrenOrientation {
        public static final int SCREEN_ORIENTATION_0 = 0;
        public static final int SCREEN_ORIENTATION_90 = 90;
        public static final int SCREEN_ORIENTATION_180 = 180;
        public static final int SCREEN_ORIENTATION_270 = 270;
    }

}
