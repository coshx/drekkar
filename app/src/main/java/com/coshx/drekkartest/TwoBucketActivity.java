package com.coshx.drekkartest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.coshx.drekkar.Callback;
import com.coshx.drekkar.Drekkar;
import com.coshx.drekkar.EventBus;
import com.coshx.drekkar.WhenReadyOnMain;

/**
 * @class TwoBucketActivity
 * @brief
 */
public class TwoBucketActivity extends Activity {

    private WebView webView1;
    private WebView webView2;

    private boolean isOtherOneReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_buckets);

        webView1 = (WebView) findViewById(R.id.two_bucket_webview_1);
        webView2 = (WebView) findViewById(R.id.two_bucket_webview_2);

        Drekkar.getDefault(
            this, webView1, new WhenReadyOnMain() {
                @Override
                public void run(EventBus bus) {
                    bus.register(
                        "Bar", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                Log.d(TwoBucketActivity.class.getName(), "Bar 1");
                            }
                        }
                    );

                    if (isOtherOneReady) {
                        bus.post("Foo");
                    } else {
                        isOtherOneReady = true;
                    }
                }
            }
        );

        Drekkar.getDefault(
            this, webView2, new WhenReadyOnMain() {
                @Override
                public void run(EventBus bus) {
                    bus.register(
                        "Bar", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                Log.d(TwoBucketActivity.class.getName(), "Bar 2");
                            }
                        }
                    );

                    if (isOtherOneReady) {
                        bus.post("Foo");
                    } else {
                        isOtherOneReady = true;
                    }
                }
            }
        );

        webView1.loadUrl("file:///android_res/raw/two_buckets_page.html");
        webView1.getSettings().setJavaScriptEnabled(true);
        webView2.loadUrl("file:///android_res/raw/two_buckets_page.html");
        webView2.getSettings().setJavaScriptEnabled(true);
    }
}
