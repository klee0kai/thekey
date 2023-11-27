package com.kee0kai.thekey.utils;

import android.util.Log;

import com.kee0kai.thekey.BuildConfig;

import java.util.logging.Logger;

public class Logs {

    private static final String LOG_TAG = "TheKeyTag";


    public static void log(String mes) {
        if (BuildConfig.DEBUG)
            Log.d(LOG_TAG, mes);
    }

    public static void e(Throwable e) {
        Log.e(LOG_TAG, e.getMessage(), e);
    }


    public static void w(Throwable e) {
        Log.w(LOG_TAG, e.getMessage(), e);
    }

    public static void i(Throwable e) {
        Log.i(LOG_TAG, e.getMessage(), e);
    }

    public static void crashForDebug(Exception e) {
        if (BuildConfig.DEBUG) {
            throw new RuntimeException(e);
        } else Logs.e(e);
    }


}
