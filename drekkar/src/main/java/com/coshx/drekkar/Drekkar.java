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
 * @class Drekkar
 * @brief
 */
public class Drekkar {
    private static final Object busLock          = new Object();
    private static final String DEFAULT_BUS_NAME = "default";

    private String         name;
    private List<EventBus> buses;

    Drekkar(String name) {
        this.name = name;
        this.buses = new ArrayList<>();
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

                    if (name == DEFAULT_BUS_NAME) {
                        toRun = "Caravel.getDefault()";
                    } else {
                        toRun = "Caravel.get(\"" + name + "\")";
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

    void addBus(Object subscriber, WebView webView, WhenReady whenReady, Boolean inBackground) {
        EventBus bus;

        // Test if an existing bus matching provided pair already exists
        synchronized (busLock) {
            for (EventBus b : buses) {
                Object reference = b.getReference();
                WebView webView = b.getWebView();

                if (reference != null && reference.hashCode() == subscriber.hashCode()
                    && webView != null && webView.hashCode() == subscriber.hashCode()) {
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

    void deleteBus(EventBus bus) {
        synchronized (busLock) {
            int i = 0;

            for (EventBus b : buses) {
                if (b == bus) {
                    buses.remove(i);
                    return;
                }
                i++;
            }
        }
    }

    
}
