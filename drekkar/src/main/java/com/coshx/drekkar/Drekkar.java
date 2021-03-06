//         _            _           _            _              _              _                   _
//        /\ \         /\ \        /\ \         /\_\           /\_\           / /\                /\ \
//       /  \ \____   /  \ \      /  \ \       / / /  _       / / /  _       / /  \              /  \ \
//      / /\ \_____\ / /\ \ \    / /\ \ \     / / /  /\_\    / / /  /\_\    / / /\ \            / /\ \ \
//     / / /\/___  // / /\ \_\  / / /\ \_\   / / /__/ / /   / / /__/ / /   / / /\ \ \          / / /\ \_\
//    / / /   / / // / /_/ / / / /_/_ \/_/  / /\_____/ /   / /\_____/ /   / / /  \ \ \        / / /_/ / /
//   / / /   / / // / /__\/ / / /____/\    / /\_______/   / /\_______/   / / /___/ /\ \      / / /__\/ /
//  / / /   / / // / /_____/ / /\____\/   / / /\ \ \     / / /\ \ \     / / /_____/ /\ \    / / /_____/
//  \ \ \__/ / // / /\ \ \  / / /______  / / /  \ \ \   / / /  \ \ \   / /_________/\ \ \  / / /\ \ \
//   \ \___\/ // / /  \ \ \/ / /_______\/ / /    \ \ \ / / /    \ \ \ / / /_       __\ \_\/ / /  \ \ \
//    \/_____/ \/_/    \_\/\/__________/\/_/      \_\_\\/_/      \_\_\\_\___\     /____/_/\/_/    \_\/
//

package com.coshx.drekkar;

import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

/**
 * Drekkar
 * <p/>
 * Main class of the library. Dispatches events among buses.
 */
public class Drekkar {
    private static final Object busLock          = new Object();
    static final         String DEFAULT_BUS_NAME = "default";

    private String         name;
    private List<EventBus> buses;

    Drekkar(String name) {
        this.name = name;
        this.buses = new ArrayList<>();
    }

    private void addBus(Object subscriber, WebView webView, WhenReady whenReady, Boolean
        inBackground) {
        EventBus bus;

        // Test if an existing bus matching provided pair already exists
        synchronized (busLock) {
            for (EventBus b : buses) {
                Object reference = b.getReference();
                WebView busWebView = b.getWebView();

                if (reference != null && reference.hashCode() == subscriber.hashCode()
                    && busWebView != null && busWebView.hashCode() == webView.hashCode()) {
                    if (inBackground) {
                        b.whenReady(whenReady);
                    } else {
                        b.whenReadyOnMain(whenReady);
                    }
                    return;
                }
            }
        }

        bus = new EventBus(this, subscriber, webView);

        synchronized (busLock) {
            buses.add(bus);
        }

        if (inBackground) {
            bus.whenReady(whenReady);
        } else {
            bus.whenReadyOnMain(whenReady);
        }

        // Clean unused buses
        ThreadingHelper.background(
            new Runnable() {
                @Override
                public void run() {
                    synchronized (busLock) {
                        int i = 0;
                        List<Integer> toRemove = new ArrayList<Integer>();

                        for (EventBus b : buses) {
                            if (b.getReference() == null && b.getWebView() == null) {
                                // Watched pair was garbage collected. This bus is not needed anymore
                                toRemove.add(i);
                                b.willBeDeleted();
                            }
                            i++;
                        }

                        i = 0;
                        for (Integer j : toRemove) {
                            buses.remove(j - i);
                            i++;
                        }
                    }
                }
            }
        );
    }

    <T> void post(final String eventName, final T eventData) {
        ThreadingHelper.background(
            new Runnable() {
                @Override
                public void run() {
                    String data;
                    String toRun;

                    if (eventData != null) {
                        data = DataSerializer.serialize(eventData);
                    } else {
                        data = "null";
                    }

                    toRun = "Drekkar.";

                    if (name.equals(DEFAULT_BUS_NAME)) {
                        toRun += "getDefault()";
                    } else {
                        toRun += "get(\"" + name + "\")";
                    }

                    toRun += ".raise(\"" + eventName + "\", " + data + ")";

                    synchronized (busLock) {
                        for (EventBus b : buses) {
                            b.forwardToJS(toRun);
                        }
                    }
                }
            }
        );
    }

    void deleteBus(EventBus bus) {
        synchronized (busLock) {
            int i = 0;

            for (EventBus b : buses) {
                if (b.equals(bus)) {
                    buses.remove(i);
                    return;
                }
                i++;
            }
        }
    }

    void dispatch(String busName, final String eventName, String rawData) {
        if (!busName.equals(name)) {
            // Different buses can use the same web view so same proxy
            // If not a potential receiver, ignore event
            return;
        }

        if (eventName.equals("DrekkarInit")) { // Reserved event name. Triggers whenReady
            synchronized (busLock) {
                for (EventBus b : buses) {
                    b.onInit(); // Run on main/current thread
                }
            }
        } else {
            final Object eventData = (rawData == null) ? null : DataSerializer.deserialize(rawData);
            ThreadingHelper.background(
                new Runnable() {
                    @Override
                    public void run() {
                        synchronized (busLock) {
                            for (EventBus b : buses) {
                                final EventBus finalBus = b;
                                ThreadingHelper.background(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            finalBus.raise(eventName, eventData);
                                        }
                                    }
                                );
                            }
                        }
                    }
                }
            );
        }
    }

    /**
     * Current name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns default bus
     *
     * @param subscriber Subscriber (usually the activity/current context)
     * @param webView    WebView to watch
     * @param whenReady  Action to run when JS counterpart is ready
     * @return Current instance
     */
    public static Drekkar getDefault(Object subscriber, WebView webView, WhenReady whenReady) {
        Drekkar d = DrekkarFactory.getDefault();
        d.addBus(subscriber, webView, whenReady, true);
        return d;
    }

    /**
     * Returns default bus and runs callback on main thread
     *
     * @param subscriber Subscriber (usually the activity/current context)
     * @param webView    WebView to watch
     * @param whenReady  Action to run when JS counterpart is ready
     * @return Current instance
     */
    public static Drekkar getDefault(Object subscriber, WebView webView, WhenReadyOnMain
        whenReady) {
        Drekkar d = DrekkarFactory.getDefault();
        d.addBus(subscriber, webView, whenReady, false);
        return d;
    }

    /**
     * Returns custom bus
     *
     * @param subscriber Subscriber (usually the activity/current context)
     * @param name       Bus name
     * @param webView    WebView to watch
     * @param whenReady  Action to run when JS counterpart is ready
     * @return Current instance
     */
    public static Drekkar get(Object subscriber, String name, WebView webView, WhenReady
        whenReady) {
        Drekkar d = DrekkarFactory.get(name);
        d.addBus(subscriber, webView, whenReady, true);
        return d;
    }

    /**
     * Returns custom bus and runs callback on main thread
     *
     * @param subscriber Subscriber (usually the activity/current context)
     * @param name       Bus name
     * @param webView    WebView to watch
     * @param whenReady  Action to run when JS counterpart is ready
     * @return Current instance
     */
    public static Drekkar get(Object subscriber, String name, WebView webView, WhenReadyOnMain
        whenReady) {
        Drekkar d = DrekkarFactory.get(name);
        d.addBus(subscriber, webView, whenReady, false);
        return d;
    }
}
