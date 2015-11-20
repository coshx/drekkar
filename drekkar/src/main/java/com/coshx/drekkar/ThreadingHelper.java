package com.coshx.drekkar;


import android.os.Handler;
import android.os.Looper;

/**
 * @class ThreadingHelper
 * @brief
 */
class ThreadingHelper {
    static void background(final Runnable action) {
        Thread thread = new Thread(action);
        thread.start();
    }

    static void main(final Runnable action) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(action);
    }
}
