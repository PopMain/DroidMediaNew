package com.popmain.droidmedia.util

import android.text.TextUtils
import android.util.Log
import com.popmain.droidmedia.BuildConfig
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.DateFormat
import java.util.*

object SLogUtil {
    private const val TAG = "SLogs"
    private const val CAMERA_WARNING_TAG = "CAMERA_WARNING_TAG"
    const val AR_WARNING_TAG = "AR_WARNING_TAG"

    private var mOutputPath: String? = null
    private var mFileWriter: FileWriter? = null
    private var mDate: Date? = null
    private var mDateFormat: DateFormat? = null


    /**
     * 简单自定义日志输出路径，注意会开启新线程，尽量只在调试中必要情况下使用，如要断开adb的情况下
     */
    fun out(outputPath: String?) {
        if (!BuildConfig.DEBUG) {
            return
        }
        if (!TextUtils.equals(mOutputPath, outputPath)) {
            if (mFileWriter != null) {
                try {
                    mFileWriter!!.close()
                    mFileWriter = null
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (!TextUtils.isEmpty(outputPath)) {
                var file = File(outputPath)
                if (file.isDirectory) {
                    file = File(file, "Slogs.log")
                }
                try {
                    if (!file.parentFile.exists()) {
                        file.parentFile.mkdirs()
                    }
                    mFileWriter = FileWriter(file.absoluteFile, true)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            mOutputPath = outputPath
        }
    }


    /**
     * 在正式包中打日志
     *
     * @param msg
     * @param args
     */
    fun logStack(msg: String?, vararg args: Any?) {
        Log.w(TAG, Log.getStackTraceString(Throwable(String.format(Locale.US, msg!!, *args))))
    }

    /**
     * 在正式包中打日志
     *
     * @param msg
     * @param args
     */
    fun logStack(tag: String?, msg: String?, vararg args: Any?) {
        Log.w(tag
                ?: TAG, Log.getStackTraceString(Throwable(String.format(Locale.US, msg!!, *args))))
    }

    fun v(msg: String?, vararg args: Any?) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, String.format(Locale.US, msg!!, *args))
        }
    }



    fun cl(msg: String?, vararg args: Any?) {
        val result = if (args.isNotEmpty()) String.format(Locale.US, msg!!, *args) else msg!!
        Log.w(CAMERA_WARNING_TAG, result)
    }

    fun al(msg: String?, vararg args: Any?) {
        val result = if (args.isNotEmpty()) String.format(Locale.US, msg!!, *args) else msg!!
        Log.w(AR_WARNING_TAG, result)
    }



}