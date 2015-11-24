package com.coshx.drekkar;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class WebViewJSEndpoint
 * @brief Dispatches events to subscribers
 */
class WebViewJSEndpoint {

    private static class DrekkarWebViewJSEndpoint {
        private WebViewJSEndpoint owner;
        private int               hashCode;

        DrekkarWebViewJSEndpoint(WebViewJSEndpoint owner, int hashCode) {
            this.owner = owner;
            this.hashCode = hashCode;
        }


        @JavascriptInterface
        public int getHashCode() {
            return hashCode;
        }

        @JavascriptInterface
        public void send(int webViewHash, String busName, String eventName, String data) {
            owner.handle(webViewHash, busName, eventName, data);
        }
    }

    private static final Object subscriberLock = new Object();

    /**
     * This mediator is singleton for only a single endpoint is allowed
     */
    private static final WebViewJSEndpoint singleton = new WebViewJSEndpoint();

    /**
     * All the subscribers. They are grouped by webview's hash
     */
    private Map<Integer, List<IWebViewJSEndpoint>> subscribers;

    private WebViewJSEndpoint() {
        this.subscribers = new HashMap<>();
    }

    static void subscribe(WebView webView, IWebViewJSEndpoint subscriber) {
        synchronized (subscriberLock) {
            if (!singleton.subscribers.containsKey(webView.hashCode())) {
                singleton.subscribers.put(webView.hashCode(), new ArrayList<IWebViewJSEndpoint>());
                webView.addJavascriptInterface(
                    new DrekkarWebViewJSEndpoint(singleton, webView.hashCode()),
                    "DrekkarWebViewJSEndpoint"
                );
            }

            singleton.subscribers.get(webView.hashCode()).add(subscriber);
        }
    }

    static void unsubscribe(WebView webView, IWebViewJSEndpoint subscriber) {
        synchronized (subscriberLock) {
            int key = webView.hashCode();

            if (singleton.subscribers.containsKey(key)) {
                int i = 0;
                for (IWebViewJSEndpoint e : singleton.subscribers.get(key)) {
                    if (e.hashCode() == subscriber.hashCode()) {
                        List<IWebViewJSEndpoint> a = singleton.subscribers.get(key);
                        a.remove(i);

                        if (a.size() == 0) {
                            singleton.subscribers.remove(key);
                        }
                        return;
                    }
                    i++;
                }
            }
        }
    }

    void handle(int webViewHash, String busName, String eventName, String data) {
        synchronized (subscriberLock) {
            List<IWebViewJSEndpoint> endpoints = singleton.subscribers.get(webViewHash);

            for (IWebViewJSEndpoint e : endpoints) {
                e.handle(busName, eventName, data);
            }
        }
    }
}
