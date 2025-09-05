package com.vlc.lib.listener.util;

import android.util.Log;



/**
 * Created by yyl on 2016/10/12/012.
 */

public class LogUtils {
    public static boolean debug =false;

    public static void i(String tag, String msg) {
        if (debug)
            Log.i(tag, msg);
    }

    public static void i(String msg) {
        if (debug) Log.i("vlc", msg);
    }
}
