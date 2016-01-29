package com.coshx.drekkar;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

/**
 * @class WebViewJSEndpoint
 * @brief Dispatches events to subscribers
 */
class WebViewJSEndpoint {
    private final Object subscriberLock = new Object();

    private List<IWebViewJSEndpoint> subscribers = new ArrayList<>();

    WebViewJSEndpoint(final WebView webView) {
        ThreadingHelper.main(
            new Runnable() {
                @Override
                public void run() {
                    webView.addJavascriptInterface(
                        WebViewJSEndpoint.this,
                        "DrekkarWebViewJSEndpoint"
                    );
                }
            }
        );
    }

    void subscribe(IWebViewJSEndpoint subscriber) {
        synchronized (subscriberLock) {
            for (IWebViewJSEndpoint e : subscribers) {
                if (e.hashCode() == subscriber.hashCode()) {
                    return;
                }
            }

            subscribers.add(subscriber);
        }
    }

    void unsubscribe(IWebViewJSEndpoint subscriber) {
        synchronized (subscriberLock) {
            int i = 0;

            for (IWebViewJSEndpoint e : subscribers) {
                if (e.hashCode() == subscriber.hashCode()) {
                    subscribers.remove(i);
                    return;
                }
                i++;
            }
        }
    }

    boolean hasSubscribers() {
        return subscribers.size() > 0;
    }

    void willBeDeleted(final WebView webview) {
        ThreadingHelper.main(
            new Runnable() {
                @Override
                public void run() {
                    webview.removeJavascriptInterface("DrekkarWebViewJSEndpoint");
                }
            }
        );
    }

    @JavascriptInterface
    public void send(final String busName, final String eventName, final String eventData) {
        synchronized (subscriberLock) {
            for (IWebViewJSEndpoint e : subscribers) {
                final IWebViewJSEndpoint finalE = e;

                ThreadingHelper.background(
                    new Runnable() {
                        @Override
                        public void run() {
                            finalE.onMessage(busName, eventName, eventData);
                        }
                    }
                );
            }
        }
    }
}
