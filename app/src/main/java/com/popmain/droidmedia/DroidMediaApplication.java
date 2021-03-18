package com.popmain.droidmedia;

import android.app.Application;

/**
 * Created by wzx on 2018/1/30.
 */

public class DroidMediaApplication extends Application {

    private static DroidMediaApplication sDroidMediaApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sDroidMediaApplication = this;
    }

    public static DroidMediaApplication getsDroidMediaApplication() {
        return sDroidMediaApplication;
    }
}
