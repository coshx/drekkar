package com.coshx.drekkar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @class DataSerializer
 * @brief
 */
class DataSerializer {
    private static Map toMap(JSONObject object) throws JSONException {
        Map dictionary = new HashMap();
        Iterator<String> iterator = object.keys();

        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = object.get(key);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }
            dictionary.put(key, value);
        }

        return dictionary;
    }

    private static List toList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        for (int i = 0, s = array.length(); i < s; i++) {
            Object o = array.get(i);
            if (o instanceof JSONObject) {
                o = toMap((JSONObject) o);
            }
            list.add(o);
        }
        return list;
    }

    static String serialize(Object data) {
        String outcome;

        if (data instanceof Boolean) {
            outcome = ((Boolean) data) ? "true" : "false";
        } else if (data instanceof Integer || data instanceof Float || data instanceof Double) {
            outcome = data.toString();
        } else if (data instanceof String) {
            // As this string is going to be unwrapped from quotes, when passed to JS, all quotes need to be escaped
            outcome = data.toString();
            outcome = outcome.replaceAll("\"", "\\\\\\\"");
            outcome = outcome.replaceAll("'", "\\\\\\'");
            outcome = "\"" + outcome + "\"";
        } else if (data instanceof List) {
            // Array and Dictionary are serialized to JSON.
            // They should wrap only "basic" data (same types than supported ones)
            outcome = new JSONArray((List) data).toString();
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
                        return toList(new JSONArray(input));
                    } catch (Exception e) {
                        return null;
                    }
                } else if (first == '{' && last == '}') {
                    try {
                        return toMap(new JSONObject(input));
                    } catch (Exception e) {
                        return null;
                    }
                }
            }

            if (input.equals("true")) {
                return true;
            } else if (input.equals("false")) {
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
