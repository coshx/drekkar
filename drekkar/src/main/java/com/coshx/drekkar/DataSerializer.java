package com.coshx.drekkar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @class DataSerializer
 * @brief
 */
class DataSerializer {
    static String serialize(Object data) {
        String outcome;

        if (data instanceof Boolean) {
            outcome = ((Boolean) data) ? "true" : "false";
        } else if (data instanceof Integer || data instanceof Float || data instanceof Double) {
            outcome = data.toString();
        } else if (data instanceof String) {
            // As this string is going to be unwrapped from quotes, when passed to JS, all quotes need to be escaped
            outcome = data.toString();
            outcome = outcome.replaceAll("\"", "\\\"");
            outcome = outcome.replaceAll("'", "\'");
        } else if (data instanceof List) {
            // Array and Dictionary are serialized to JSON.
            // They should wrap only "basic" data (same types than supported ones)
            outcome = new JSONArray((List) data).toString();
        } else if (data.getClass().isArray()) {
            outcome = new JSONArray(Arrays.asList(data)).toString();
        } else if (data instanceof Map) {
            outcome = new JSONObject((Map) data).toString();
        } else {
            outcome = null;
        }

        return outcome;
    }

    static Object deserialize(String input) {
        if (input.length() > 0) {
            if (input.length() >= 2) {
                char first = input.charAt(0), last = input.charAt(input.length() - 1);

                if (first == '[' && last == ']') {
                    try {
                        JSONArray a = new JSONArray(input);
                        List outcome = new ArrayList();

                        for (int i = 0, s = a.length(); i < s; i++) {
                            outcome.add(a.get(i));
                        }

                        return outcome;
                    } catch (Exception e) {
                        return null;
                    }
                } else if (first == '{' && last == '}') {
                    try {
                        JSONObject o = new JSONObject(input);
                        Iterator<String> iterator = o.keys();
                        Map<String, Object> outcome = new HashMap<>();

                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            outcome.put(key, o.get(key));
                        }

                        return outcome;

                    } catch (Exception e) {
                        return null;
                    }
                }
            }

            if (input == "true") {
                return true;
            } else if (input == "false") {
                return false;
            }

            boolean isNumber = true;
            for (int i = 0, s = input.length(); i < s; i++) {
                char c = input.charAt(i);
                if (Character.isDigit(c) || c == '.' || c == ',') {
                    // Do nothing
                } else {
                    isNumber = false;
                    break;
                }
            }

            if (isNumber) {
                try {
                    return Integer.parseInt(input);
                } catch (Exception e) {
                    return Double.parseDouble(input);
                }
            }
        }

        return input;
    }
}
