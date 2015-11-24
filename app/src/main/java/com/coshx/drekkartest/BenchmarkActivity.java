package com.coshx.drekkartest;

import android.os.Bundle;

import com.coshx.drekkar.Callback;
import com.coshx.drekkar.Drekkar;
import com.coshx.drekkar.EventBus;
import com.coshx.drekkar.WhenReady;

import java.util.UUID;

/**
 * @class BenchmarkActivity
 * @brief
 */
public class BenchmarkActivity extends WebActivity {

    private EventBus bus;

    private void startTesting() {
        String name = UUID.randomUUID().toString();

        Drekkar.get(
            this, name, webView, new WhenReady() {
                @Override
                public void run(final EventBus bus) {
                    for (int i = 0; i < 1000; i++) {
                        bus.register(
                            "Background-" + i, new Callback() {
                                @Override
                                public void run(String name, Object data) {
                                    bus.post(name + "-confirmation");
                                }
                            }
                        );

                        bus.registerOnMain(
                            "Main-" + i, new Callback() {
                                @Override
                                public void run(String name, Object data) {
                                    bus.post(name + "-confirmation");
                                }
                            }
                        );
                    }

                    bus.post("Ready");
                }
            }
        );

        bus.post("BusName", name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Drekkar.getDefault(
            this, webView, new WhenReady() {
                @Override
                public void run(EventBus bus) {
                    BenchmarkActivity.this.bus = bus;
                    bus.register(
                        "Start", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                startTesting();
                            }
                        }
                    );
                }
            }
        );

        loadURL("benchmark");
    }
}
