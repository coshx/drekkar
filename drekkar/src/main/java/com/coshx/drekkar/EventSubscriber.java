package com.coshx.drekkar;

/**
 * @class EventSubscriber
 * @brief Internal class storing bus subscribers
 */
class EventSubscriber {
    String   name;
    Callback callback;
    Boolean  inBackground;

    EventSubscriber(String name, Callback callback, Boolean inBackground) {
        this.name = name;
        this.callback = callback;
        this.inBackground = inBackground;
    }
}
