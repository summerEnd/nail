package com.finger.support.util;

import android.util.Log;

import com.finger.BuildConfig;

/**
 * Created by acer on 2014/9/28.
 */
public class Logger {
    private final static String TAG = "SLOG";
    private static boolean isLogAble = BuildConfig.DEBUG;

    public static void setIsLogAble(boolean isLogAble) {
        Logger.isLogAble = isLogAble;
    }

    public static final void e(String msg) {
        e(TAG, msg);
    }

    public static final void w(String msg) {
        w(TAG, msg);
    }

    public static final void d(String msg) {
        d(TAG, msg);
    }

    public static final void i_format(String msg, Object... formats) {
        i(String.format(msg, formats));
    }

    public static final void i(String msg) {
        i(TAG, msg);
    }

    public static final void e(String tag, String msg) {
        if (isLogAble) Log.e(tag, msg);
    }

    public static final void w(String tag, String msg) {
        if (isLogAble) Log.w(tag, msg);
    }

    public static final void i(String tag, String msg) {
        if (isLogAble) Log.i(tag, msg);
    }

    public static final void d(String tag, String msg) {
        if (isLogAble) Log.d(tag, msg);
    }

    public static final void debug(Runnable runnable) {
        if (isLogAble) runnable.run();
    }
}
