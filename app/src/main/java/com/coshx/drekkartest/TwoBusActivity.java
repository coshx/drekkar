package com.coshx.drekkartest;

import android.os.Bundle;

import com.coshx.drekkar.Drekkar;
import com.coshx.drekkar.EventBus;
import com.coshx.drekkar.WhenReady;

/**
 * @class TwoBusActivity
 * @brief
 */
public class TwoBusActivity extends WebActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Drekkar.get(
            this, "FooBus", webView, new WhenReady() {
                @Override
                public void run(EventBus bus) {
                    bus.post("AnEvent");
                }
            }
        );

        Drekkar.get(
            this, "BarBus", webView, new WhenReady() {
                @Override
                public void run(EventBus bus) {
                    bus.post("AnEvent");
                }
            }
        );

        loadURL("two_buses");
    }
}
