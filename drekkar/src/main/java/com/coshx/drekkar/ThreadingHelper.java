package com.coshx.drekkar;


import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

/**
 * @class ThreadingHelper
 * @brief
 */
class ThreadingHelper {
    static void background(final Runnable action) {
        main(new Runnable() {
            @Override
            public void run() {
                AsyncTask.execute(action);
            }
        });
    }

    static void main(final Runnable action) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(action);
    }
}
