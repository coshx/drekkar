package com.coshx.drekkartest;

import android.os.Bundle;

import com.coshx.drekkar.Callback;
import com.coshx.drekkar.Drekkar;
import com.coshx.drekkar.EventBus;
import com.coshx.drekkar.WhenReady;

/**
 * @class EventNameActivity
 * @brief
 */
public class EventNameActivity extends WebActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Drekkar.getDefault(
            this, webView, new WhenReady() {
                @Override
                public void run(final EventBus bus) {
                    bus.register(
                        "Bar", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                if (name.equals("Bar")) {
                                    bus.post("Foo");
                                } else {
                                    bus.post("Foobar");
                                }
                            }
                        }
                    );
                }
            }
        );

        loadURL("event_name");
    }
}
