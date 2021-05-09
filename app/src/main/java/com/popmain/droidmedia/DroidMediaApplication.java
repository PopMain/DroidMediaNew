package com.popmain.droidmedia;

import android.app.Application;

import com.popmain.droidmedia.util.BitmapLruCacheMemoryReuse;

/**
 * Created by wzx on 2018/1/30.
 */

public class DroidMediaApplication extends Application {

    private static DroidMediaApplication sDroidMediaApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sDroidMediaApplication = this;
        BitmapLruCacheMemoryReuse.getInstance().init(this);
    }

    public static DroidMediaApplication getsDroidMediaApplication() {
        return sDroidMediaApplication;
    }
}
