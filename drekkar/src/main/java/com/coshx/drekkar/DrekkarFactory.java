package com.coshx.drekkar;

import java.util.HashMap;
import java.util.Map;

/**
 * @class DrekkarFactory
 * @brief Manages the different Drekkar instances and ensures they are all singletons
 */
class DrekkarFactory {
    private static final Object defaultBusLock = new Object();
    private static Drekkar defaultBus;

    private static final Object               creationLock = new Object();
    // A lock per bus. Uses lock above when initializing
    private static       Map<String, Object>  busLocks     = new HashMap<>();
    private static       Map<String, Drekkar> buses        = new HashMap<>();

    private static Object getLock(String name) {
        if (busLocks.containsKey(name)) {
            return busLocks.get(name);
        } else {
            synchronized (creationLock) {
                if (busLocks.containsKey(name)) {
                    return busLocks.get(name);
                } else {
                    Object o = new Object();
                    busLocks.put(name, o);
                    return o;
                }
            }
        }
    }

    static Drekkar getDefault() {
        if (defaultBus != null) {
            return defaultBus;
        } else {
            synchronized (defaultBusLock) {
                if (defaultBus == null) {
                    defaultBus = new Drekkar(Drekkar.DEFAULT_BUS_NAME);
                }
                return defaultBus;
            }
        }
    }

    static Drekkar get(String name) {
        if (name == Drekkar.DEFAULT_BUS_NAME) {
            return getDefault();
        } else {
            if (buses.containsKey(name)) {
                return buses.get(name);
            } else {
                synchronized (getLock(name)) {
                    if (buses.containsKey(name)) {
                        return buses.get(name);
                    } else {
                        Drekkar bus = new Drekkar(name);
                        buses.put(name, bus);
                        return bus;
                    }
                }
            }
        }
    }
}
