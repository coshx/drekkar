package com.coshx.drekkartest;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * @class WebActivity
 * @brief
 */
public class WebActivity extends Activity {

    protected WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview_layout);
        webView = (WebView) findViewById(R.id.embedded_webview);
    }

    void loadURL(String filename) {
        webView.loadUrl("file:///android_res/raw/" + filename + "_page.html");
        webView.getSettings().setJavaScriptEnabled(true);
    }
}
