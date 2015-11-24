package com.coshx.drekkartest;

import android.os.Bundle;

import com.coshx.drekkar.Drekkar;
import com.coshx.drekkar.EventBus;
import com.coshx.drekkar.WhenReady;

/**
 * @class InitializationActivity
 * @brief
 */
public class InitializationActivity extends WebActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Drekkar.getDefault(
            this, webView, new WhenReady() {
                @Override
                public void run(EventBus bus) {
                    bus.post("Before");
                }
            }
        );

        loadURL("initialization");

        Drekkar.getDefault(
            this, webView, new WhenReady() {
                @Override
                public void run(EventBus bus) {
                    bus.post("After");
                }
            }
        );
    }
}
