package com.popmain.droidmedia.util;

import android.media.audiofx.AcousticEchoCanceler;
import android.os.Build;

/**
 * Created by wzx on 2017/12/25.
 */

public class AudioUtils {

    /**
     * 是否支持AcousticEchoCanceler消除回声
     * @return true是；false否
     */
    public static boolean isDeviceAcousticEchoCancelerSupport()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            return AcousticEchoCanceler.isAvailable();
        else
            return false;
    }

}
