package com.megvii.smartretail.log;

import android.text.TextUtils;

import com.megvii.smartretail.BuildConfig;

/**
 * Created by luojianding on 2014/9/6.
 */
public class LogUtil {

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (valide(tag, msg)) {
                android.util.Log.d(tag, msg);
            }
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG) {
            if (valide(tag, msg)) {
                android.util.Log.d(tag, msg, t);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (valide(tag, msg)) {
                android.util.Log.e(tag, msg);
            }
        }
    }

    public static void e(Exception e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }

    public static void e(Throwable t) {
        if (BuildConfig.DEBUG) {
            t.printStackTrace();
        }
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (valide(tag, msg)) {
                android.util.Log.v(tag, msg);
            }
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            if (valide(tag, msg)) {
                android.util.Log.v(tag, msg, tr);
            }
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (valide(tag, msg)) {
                android.util.Log.w(tag, msg);
            }
        }
    }

    public static void w(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG) {
            if (valide(tag, msg)) {
                android.util.Log.w(tag, msg, t);
            }
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (valide(tag, msg)) {
                android.util.Log.i(tag, msg);
            }
        }
    }

    static boolean valide(String tag, String msg) {
        return !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg);
    }
}
