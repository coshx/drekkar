package com.coshx.drekkartest;

import android.os.Bundle;
import android.util.Log;

import com.coshx.drekkar.Callback;
import com.coshx.drekkar.Drekkar;
import com.coshx.drekkar.EventBus;
import com.coshx.drekkar.WhenReady;
import com.coshx.drekkar.WhenReadyOnMain;

/**
 * @class ThreadingActivity
 * @brief
 */
public class ThreadingActivity extends WebActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Drekkar.get(
            this, "First", webView, new WhenReady() {
                @Override
                public void run(final EventBus bus) {
                    bus.registerOnMain(
                        "FromJSForBackground", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                bus.post("FromBackgroundAfterFromJS");
                            }
                        }
                    );

                    try {
                        Thread.sleep(1 * 1000);
                    } catch (Exception e) {
                        Log.e(ThreadingActivity.class.getName(), e.getMessage());
                    }
                    bus.post("FromBackground");
                }
            }
        );

        Drekkar.get(
            this, "Second", webView, new WhenReadyOnMain() {
                @Override
                public void run(final EventBus bus) {
                    bus.register(
                        "FromJSForMain", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                try {
                                    Thread.sleep(2 * 1000);
                                } catch (Exception e) {
                                    Log.e(ThreadingActivity.class.getName(), e.getMessage());
                                }

                                bus.post("FromMainAfterFromJS");
                            }
                        }
                    );

                    bus.post("FromMain");
                }
            }
        );

        loadURL("threading");
    }
}
