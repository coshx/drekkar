package com.coshx.drekkar;

import android.webkit.WebView;

import java.util.HashMap;
import java.util.Map;

/**
 * @class WebViewJSEndpointMediator
 * @brief
 */
class WebViewJSEndpointMediator {
    private final static Object creationLock = new Object();

    private static Map<Integer, WebViewJSEndpoint> proxies = new HashMap<>();

    static void subscribe(WebView webView, IWebViewJSEndpoint observer) {
        int key = webView.hashCode();

        if (proxies.containsKey(key)) {
            proxies.get(key).subscribe(observer);
        } else {
            synchronized (creationLock) {
                if (proxies.containsKey(key)) {
                    proxies.get(key).subscribe(observer);
                } else {
                    WebViewJSEndpoint proxy = new WebViewJSEndpoint(webView);
                    proxy.subscribe(observer);
                    proxies.put(key, proxy);
                }
            }
        }
    }

    static void unsubscribe(WebView webView, IWebViewJSEndpoint observer) {
        int key = webView.hashCode();

        if (proxies.containsKey(key)) {
            WebViewJSEndpoint proxy = proxies.get(key);

            proxy.unsubscribe(observer);

            if (!proxy.hasSubscribers()) {
                synchronized (creationLock) {
                    if (!proxy.hasSubscribers()) {
                        proxy.willBeDeleted(webView);
                        proxies.remove(key);
                    }
                }
            }
        }
    }
}
