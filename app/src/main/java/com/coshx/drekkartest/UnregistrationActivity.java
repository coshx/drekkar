package com.coshx.drekkartest;

import android.os.Bundle;
import android.util.Log;

import com.coshx.drekkar.Callback;
import com.coshx.drekkar.Drekkar;
import com.coshx.drekkar.EventBus;
import com.coshx.drekkar.WhenReady;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @class UnregistrationActivity
 * @brief
 */
public class UnregistrationActivity extends WebActivity {

    private EventBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Drekkar.getDefault(
            this, webView, new WhenReady() {
                @Override
                public void run(final EventBus bus) {
                    UnregistrationActivity.this.bus = bus;

                    bus.registerOnMain(
                        "Whazup?", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                bus.post("Bye");
                                Timer t = new Timer();

                                t.schedule(
                                    new TimerTask() {
                                        @Override
                                        public void run() {
                                            UnregistrationActivity.this.bus.unregister(UnregistrationActivity.this);
                                        }
                                    },
                                    2 * 1000
                                );
                            }
                        }
                    );

                    bus.register(
                        "Still around?", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                Log.e(
                                    UnregistrationActivity.class.getName(), "You should not see this " +
                                                                            "message"
                                );
                            }
                        }
                    );

                    bus.post("Hello!");
                }
            }
        );

        loadURL("unregistration");
    }
}
