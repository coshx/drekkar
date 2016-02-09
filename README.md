![Logo](https://raw.githubusercontent.com/coshx/drekkar/master/logo.png)

[![Release](https://jitpack.io/v/coshx/drekkar.svg)](https://jitpack.io/#coshx/drekkar)

[![Join the chat at https://gitter.im/coshx/drekkar](https://img.shields.io/gitter/room/nwjs/nw.js.svg)](https://gitter.im/coshx/drekkar?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**An event bus for sending messages between WebView and embedded JS. [Alter ego of Caravel](https://github.com/coshx/caravel).**

## Features

* Easy, fast and reliable event bus system
* Multiple bus support
* Multithreading support
* WebView ~> JavaScript supported types:
  - `Bool`
  - `Int`
  - `Float`
  - `Double`
  - `String`
  - Any list (using types in this list, including maps)
  - Any map (using types in this list, including lists)
* JavaScript ~> Android supported types:
  - `Boolean`
  - `Int`
  - `Float` (available as a `Double`)
  - `String`
  - `Array` (available as a `List`)
  - `Object` (available as a `Map`)

## Installation

### Using JitPack

Merge this code into your root build.gradle file:

```groovy
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
```

Then, add this dependency to your module:

```groovy
dependencies {
    compile 'com.github.coshx:drekkar:v0.1.1'
}
```

Finally, you need to load the internal JS script Drekkar is using to make magic happen. You can either use `R.draw.drekkar_min` or add the minified JS script from [the latest release](https://github.com/coshx/drekkar/releases) to your `raw` resources. This script must be loaded in any webpage you are using Drekkar.

### Using as a submodule

Clone this repo and add the `drekkar` module to your workspace.

## Get started

Drekkar allows developers to communicate between their `WebView` and the embedded JS. You can send any kind of message between these two folks.

Have a glance at this super simple sample. Let's start with the Android part:

```java
class MyActivity extends Activity {
    WebView webView;

    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = (WebView) findViewById(R.id.my_webview_id);

        // Prepare your bus before loading your web view's content
        Drekkar.getDefault(this, webView, new WhenReady() {
            @Override
            public void run(EventBus bus) {
                // In this scope, the JS endpoint is ready to handle any event.
                // Register and post your events here
                List<Integer> list = new ArrayList<>();
                list.add(1);
                list.add(2);
                list.add(3);

                bus.post("MyEvent", list);

                MyActivity.this.bus = bus; // You can save your bus for firing events later
            }
        });

        // ... Load web view's content there
    }
}
```

And now, in your JS:

```javascript
var bus = Drekkar.getDefault();

bus.register("AnEventWithAString", function(name, data) {
    alert('I received this string: ' + data);
    bus.post("AnEventForAndroid");
});
```

And voilà!

## Porting your app from Caravel to Drekkar

Super duper easy. Just use the same codebase and use the JS script from Drekkar. Finally, add this after having loaded the Drekkar script:

```javascript
var Caravel = Drekkar;
```

## Troubleshooting

### 😕 Sometimes the bus is not working?!

Firstly, ensure you are using the bus correctly. Check if you are unregistering the bus when exiting the controller owning your web component. Use the [unregister method for this]().

Drekkar automatically cleans up any unused bus when you create a new one. However, this operation is run in the background to avoid any delay on your side. So, a thread collision might happen if you have not unsubscribed your bus properly.

However, if you think everything is good with your codebase, feel free to open a ticket.

### What object should I use as a subscriber?

A subscriber could be any object **except the watched target**. We recommend to use the activity/current context as a subscriber (it is a common pattern).

### Reserved names

`DrekkarInit` is an internal event, sent by the JS part for running the `WhenReady` object.

Also, the default bus is named `default`. If you use this name for a custom bus, Drekkar will automatically switch to the default one.

Finally, Drekkar names its JS interface `DrekkarWebViewJSEndpoint`.

### Keep in mind event and bus names are case-sensitive.
