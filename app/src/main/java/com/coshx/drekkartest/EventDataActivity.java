package com.coshx.drekkartest;

import android.os.Bundle;
import android.util.Log;

import com.coshx.drekkar.Callback;
import com.coshx.drekkar.Drekkar;
import com.coshx.drekkar.EventBus;
import com.coshx.drekkar.WhenReady;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class EventDataActivity
 * @brief
 */
public class EventDataActivity extends WebActivity {

    private void raise(String msg) {
        Log.e(EventDataActivity.class.getName(), msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Drekkar.getDefault(
            this, webView, new WhenReady() {
                @Override
                public void run(EventBus bus) {
                    bus.post("Bool", true);
                    bus.post("Int", 42);
                    bus.post("Float", 19.92);
                    bus.post("Double", 20.15);
                    bus.post("String", "Churchill");
                    bus.post("HazardousString", "There is a \" and a '");

                    List<Integer> list = new ArrayList<>();
                    list.add(1);
                    list.add(2);
                    list.add(3);
                    list.add(5);
                    bus.post("List", list);

                    Map<String, Integer> dictionary = new HashMap<String, Integer>();
                    dictionary.put("foo", 45);
                    dictionary.put("bar", 89);
                    bus.post("Dictionary", dictionary);

                    List<Object> complexList = new ArrayList<Object>();
                    Map d1 = new HashMap(), d2 = new HashMap();
                    d1.put("name", "Alice");
                    d1.put("age", 24);
                    d2.put("name", "Bob");
                    d2.put("age", 23);
                    complexList.add(d1);
                    complexList.add(d2);
                    bus.post("ComplexList", complexList);

                    Map complexDictionary = new HashMap();
                    complexDictionary.put("name", "Paul");
                    Map d3 = new HashMap();
                    d3.put("street", "Hugo");
                    d3.put("city", "Bordeaux");
                    complexDictionary.put("address", d3);
                    String sArray[] = { "Fifa", "Star Wars" };
                    complexDictionary.put("games", sArray);
                    bus.post("ComplexDictionary", complexDictionary);


                    bus.register(
                        "True", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                boolean b = (boolean) data;

                                if (b != true) {
                                    raise("True - wrong value");
                                }
                            }
                        }
                    );

                    bus.register(
                        "False", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                boolean b = (boolean) data;

                                if (b != false) {
                                    raise("False - wrong value");
                                }
                            }
                        }
                    );

                    bus.register(
                        "Int", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                int i = (int) data;

                                if (i != 987) {
                                    raise("Int - wrong value");
                                }
                            }
                        }
                    );

                    bus.register(
                        "Double", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                double d = (double) data;

                                if (d != 15.15) {
                                    raise("Double - wrong value");
                                }
                            }
                        }
                    );

                    bus.register(
                        "String", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                String s = (String) data;

                                if (!s.equals("Napoleon")) {
                                    raise("String - wrong value");
                                }
                            }
                        }
                    );

                    bus.register(
                        "UUID", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                String s = (String) data;

                                if (!s.equals("9658ae60-9e0d-4da7-a63d-46fe75ff1db1")) {
                                    raise("UUID - wrong value");
                                }
                            }
                        }
                    );

                    bus.register(
                        "List", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                List<Integer> l = (List<Integer>) data;

                                if (l.size() != 3) {
                                    raise("List - wrong length");
                                }
                                if (l.get(0) != 3) {
                                    raise("List - wrong first length");
                                }
                                if (l.get(1) != 1) {
                                    raise("List - wrong second element");
                                }
                                if (l.get(2) != 4) {
                                    raise("List - wrong third element");
                                }
                            }
                        }
                    );

                    bus.register(
                        "Dictionary", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                Map<String, Object> dictionary = (Map<String, Object>) data;

                                if (dictionary.size() != 2) {
                                    raise("Dictionary - wrong length");
                                }
                                if (!((String) dictionary.get("movie")).equals(
                                    "Once upon a time " +
                                    "in the West"
                                )) {
                                    raise("Dictionary - wrong first pair");
                                }
                                if (!((String) dictionary.get("actor")).equals(
                                    "Charles Bronson"
                                )) {
                                    raise("Dictionary - wrong second pair");
                                }
                            }
                        }
                    );

                    bus.register(
                        "ComplexArray", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                List<Object> list = (List<Object>) data;

                                if (list.size() != 3) {
                                    raise("ComplexArray - wrong length");
                                }
                                if (((Integer) list.get(0)) != 87) {
                                    raise("ComplexArray - wrong first element");
                                }

                                Map<String, Object> d = (Map<String, Object>) list.get(1);
                                if (!((String) d.get("name")).equals("Bruce Willis")) {
                                    raise("ComplexArray - wrong second element");
                                }

                                if (!((String) list.get(2)).equals("left-handed")) {
                                    raise("ComplexArray - wrong third element");
                                }
                            }
                        }
                    );

                    bus.register(
                        "ComplexDictionary", new Callback() {
                            @Override
                            public void run(String name, Object data) {
                                Map<String, Object> dictionary = (Map<String, Object>) data;

                                if (!((String) dictionary.get("name")).equals("John Malkovich")) {
                                    raise("ComplexDictionary - wrong first pair");
                                }
                                List<String> l = (List<String>) dictionary.get("movies");
                                if (l.size() != 2) {
                                    raise("ComplexDictionary - wrong length");
                                }
                                if (!l.get(0).equals("Dangerous Liaisons")) {
                                    raise("ComplexDictionary - wrong first element in array");
                                }
                                if (!l.get(1).equals("Burn after reading")) {
                                    raise("ComplexDictionary - wrong second element in array");
                                }

                                if (((int) dictionary.get("kids")) != 2) {
                                    raise("ComplexDictionary - wrong third pair");
                                }
                            }
                        }
                    );

                    bus.post("Ready");
                }
            }
        );

        loadURL("event_data");
    }
}
