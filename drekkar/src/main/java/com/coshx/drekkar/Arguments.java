package com.coshx.drekkar;

/**
 * @class Arguments
 * @brief
 */
class Arguments {
    String busName;
    String eventName;
    String eventData;

    Arguments(String busName, String eventName, String eventData) {
        this.busName = busName;
        this.eventName = eventName;
        this.eventData = eventData;
    }
}
