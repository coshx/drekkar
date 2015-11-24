package com.coshx.drekkartest;

import android.os.Bundle;

import com.coshx.drekkar.Drekkar;
import com.coshx.drekkar.EventBus;
import com.coshx.drekkar.WhenReady;

/**
 * @class MultipleSubscriberActivity
 * @brief
 */
public class MultipleSubscriberActivity extends WebActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Drekkar.getDefault(
            this, webView, new WhenReady() {
                @Override
                public void run(EventBus bus) {
                    bus.post("AnEvent");
                }
            }
        );

        loadURL("multiple_subscribers");
    }
}
