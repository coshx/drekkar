package com.coshx.drekkartest;

import android.os.Bundle;

import com.coshx.drekkar.Callback;
import com.coshx.drekkar.Drekkar;
import com.coshx.drekkar.EventBus;
import com.coshx.drekkar.WhenReady;

/**
 * @class TwoEventActivity
 * @brief
 */
public class TwoEventActivity extends WebActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Drekkar.getDefault(
            this, webView, new WhenReady() {
                @Override
                public void run(final EventBus bus) {
                    bus.register(
                        "FirstEvent", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                bus.post("ThirdEvent");
                            }
                        }
                    );

                    bus.register(
                        "NeverTriggeredEvent", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                bus.post("FourthEvent");
                            }
                        }
                    );
                }
            }
        );

        loadURL("two_events");
    }
}
