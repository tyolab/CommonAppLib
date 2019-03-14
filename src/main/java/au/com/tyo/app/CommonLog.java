/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.app;

import android.util.Log;

public class CommonLog {

    public static void d(Object object, String message) {
        Log.d(object.getClass().getSimpleName(), message);
    }

    public static void e(Object object, String message) {
        Log.e(object.getClass().getSimpleName(), message);
    }

    public static void i(Object object, String message) {
        Log.i(object.getClass().getSimpleName(), message);
    }

    public static void w(Object object, String message) {
        Log.w(object.getClass().getSimpleName(), message);
    }

    public static void v(Object object, String message) {
        Log.v(object.getClass().getSimpleName(), message);
    }

    public static void wtf(Object object, String message) {
        Log.wtf(object.getClass().getSimpleName(), message);
    }
}
