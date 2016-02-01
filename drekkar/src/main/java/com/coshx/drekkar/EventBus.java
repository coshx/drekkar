package com.coshx.drekkar;

import android.webkit.WebView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventBus
 * <p/>
 * In charge of watching a subscriber / webview pair.
 * <p/>
 * Deals with any request from the user side, as well as the watched pair, and forward them to the
 * dispatcher.
 */
public class EventBus {
    private class Initializer {
        WhenReady callback;
        Boolean   inBackground;

        Initializer(WhenReady callback, Boolean inBackground) {
            this.callback = callback;
            this.inBackground = inBackground;
        }
    }

    private final Object initializationLock = new Object();
    private final Object subscriberLock     = new Object();

    private WeakReference<Object>  reference;
    private WeakReference<WebView> webView;
    private WeakReference<Drekkar> dispatcher;
    /**
     * The field below prevents the onMessage method to be public and accessible by users
     */
    private IWebViewJSEndpoint     embeddedEndpoint;

    /**
     * Bus subscribers
     */
    private List<EventSubscriber> subscribers;

    /**
     * Denotes if the bus has received init event from JS
     */
    private Boolean isInitialized;

    /**
     * Pending initialization subscribers
     */
    private List<Initializer>         initializers;
    // Buffer to save initializers temporary, in order to prevent them from being garbage collected
    private Map<Integer, Initializer> onGoingInitializers;
    private int                       onGoingInitializersId;

    EventBus(Drekkar dispatcher, Object reference, WebView webView) {
        this.dispatcher = new WeakReference<>(dispatcher);
        this.reference = new WeakReference<>(reference);
        this.webView = new WeakReference<>(webView);
        this.isInitialized = false;
        this.subscribers = new ArrayList<>();
        this.initializers = new ArrayList<>();
        this.onGoingInitializers = new HashMap<>();
        this.onGoingInitializersId = 0;

        this.embeddedEndpoint = new IWebViewJSEndpoint() {
            @Override
            public void onMessage(String busName, String eventName, String rawData) {
                Drekkar d = EventBus.this.dispatcher.get();

                if (d != null) {
                    d.dispatch(busName, eventName, rawData);
                }
            }
        };

        WebViewJSEndpointMediator.subscribe(webView, this.embeddedEndpoint);
    }

    private void unsubscribeFromProxy() {
        WebView w = webView.get();

        if (w != null) {
            WebViewJSEndpointMediator.unsubscribe(w, this.embeddedEndpoint);
        }
    }

    Object getReference() {
        return reference.get();
    }

    WebView getWebView() {
        return webView.get();
    }

    void willBeDeleted() {
        unsubscribeFromProxy();
    }

    /**
     * Runs JS script into current context
     */
    void forwardToJS(final String toRun) {
        ThreadingHelper.main(
            new Runnable() {
                @Override
                public void run() {
                    WebView w = webView.get();
                    if (w != null) {
                        w.loadUrl("javascript:" + toRun);
                    }
                }
            }
        );
    }

    void onInit() {
        // Initialization must be run on the main thread. Otherwise, some events would be triggered before onReady
        // has been run and hence be lost.
        if (isInitialized) {
            return;
        }

        synchronized (initializationLock) {
            if (isInitialized) {
                return;
            }

            for (Initializer initializer : initializers) {
                final int index = onGoingInitializersId;
                final WhenReady callback = initializer.callback;
                Runnable action = new Runnable() {
                    @Override
                    public void run() {
                        callback.run(EventBus.this);
                        onGoingInitializers.remove(index);
                    }
                };

                onGoingInitializers.put(index, initializer);
                onGoingInitializersId++;

                if (initializer.inBackground) {
                    ThreadingHelper.background(action);
                } else {
                    ThreadingHelper.main(action);
                }
            }

            initializers = new ArrayList<>();
            isInitialized = true;
        }
    }

    /**
     * Allows dispatcher to fire any event on this bus
     */
    void raise(final String name, final Object data) {
        synchronized (subscriberLock) {
            for (EventSubscriber s : subscribers) {
                if (s.name.equals(name)) {
                    final EventSubscriber finalS = s;
                    Runnable action = new Runnable() {
                        @Override
                        public void run() {
                            finalS.callback.run(name, data);
                        }
                    };

                    if (finalS.inBackground) {
                        ThreadingHelper.background(action);
                    } else {
                        ThreadingHelper.main(action);
                    }
                }
            }
        }
    }

    void whenReady(final WhenReady callback) {
        ThreadingHelper.background(
            new Runnable() {
                @Override
                public void run() {
                    if (isInitialized) {
                        ThreadingHelper.background(
                            new Runnable() {
                                @Override
                                public void run() {
                                    callback.run(EventBus.this);
                                }
                            }
                        );
                    } else {
                        synchronized (initializationLock) {
                            if (isInitialized) {
                                ThreadingHelper.background(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.run(EventBus.this);
                                        }
                                    }
                                );
                            } else {
                                initializers.add(new Initializer(callback, true));
                            }
                        }
                    }
                }
            }
        );
    }

    void whenReadyOnMain(final WhenReady callback) {
        ThreadingHelper.background(
            new Runnable() {
                @Override
                public void run() {
                    if (isInitialized) {
                        ThreadingHelper.main(
                            new Runnable() {
                                @Override
                                public void run() {
                                    callback.run(EventBus.this);
                                }
                            }
                        );
                    } else {
                        synchronized (initializationLock) {
                            if (isInitialized) {
                                ThreadingHelper.main(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.run(EventBus.this);
                                        }
                                    }
                                );
                            } else {
                                initializers.add(new Initializer(callback, false));
                            }
                        }
                    }
                }
            }
        );
    }

    /**
     * Current name
     */
    public String getName() {
        return dispatcher.get().getName();
    }

    /**
     * Posts event
     *
     * @param eventName Event's name
     */
    public void post(String eventName) {
        Drekkar d = dispatcher.get();
        if (d != null) {
            d.post(eventName, null);
        }
    }

    /**
     * Posts event with extra data
     *
     * @param eventName Event's name
     * @param data      Data to post (see documentation for supported types)
     */
    public <T> void post(String eventName, T data) {
        Drekkar d = dispatcher.get();

        if (d != null) {
            d.post(eventName, data);
        }
    }

    /**
     * Subscribes to event. Callback is run with the event's name and extra data (if any).
     *
     * @param eventName Event to watch
     * @param callback  Action to run when fired
     */
    public void register(String eventName, Callback callback) {
        synchronized (subscriberLock) {
            subscribers.add(new EventSubscriber(eventName, callback, true));
        }
    }

    /**
     * Subscribes to event. Callback is run on main thread with the event's name and extra data (if
     * any).
     *
     * @param eventName Event to watch
     * @param callback  Action to run when fired
     */
    public void registerOnMain(String eventName, Callback callback) {
        synchronized (subscriberLock) {
            subscribers.add(new EventSubscriber(eventName, callback, false));
        }
    }

    /**
     * Unregisters subscriber from bus
     */
    public void unregister() {
        Drekkar d = dispatcher.get();

        d.deleteBus(this);

        unsubscribeFromProxy();

        dispatcher = new WeakReference<Drekkar>(null);
        reference = new WeakReference<Object>(null);
        webView = new WeakReference<WebView>(null);
    }
}
