package com.coshx.drekkar;

/**
 * Callback
 * <p/>
 * Handles Drekkar's event.
 */
public interface Callback {

    /**
     * Run when event is raised
     *
     * @param name Event's name
     * @param data Event's data (if any)
     */
    void run(String name, Object data);
}
