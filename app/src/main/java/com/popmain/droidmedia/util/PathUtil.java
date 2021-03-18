package com.popmain.droidmedia.util;

import android.content.Context;

import java.io.File;

/**
 * Created by wzx on 2017/11/16.
 */

public class PathUtil {


    public static String getApplicationDir(Context context) {
        File file = context.getExternalCacheDir();
        if (file != null && file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }
}
