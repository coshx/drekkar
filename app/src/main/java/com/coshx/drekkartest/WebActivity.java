package com.coshx.drekkartest;

import android.app.Activity;
import android.webkit.WebView;

/**
 * @class WebActivity
 * @brief
 */
public class WebActivity extends Activity {

    void loadURL(WebView webView, String filename) {
        webView.loadUrl("file:///android_res/raw/" + filename + "_page.html");
        webView.getSettings().setJavaScriptEnabled(true);
    }
}
