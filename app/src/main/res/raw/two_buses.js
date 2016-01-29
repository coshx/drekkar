Drekkar.get("BarBus").register("AnEvent", function(name, data) {
    $('body').append('<p class="js">You should see me first and only once</p>');
});

Drekkar.get("FooBus").register("AnEvent", function(name, data) {
    $('body').append('<p class="js">You should see me after and only once</p>');
});