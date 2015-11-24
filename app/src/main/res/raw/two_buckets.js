var bus = Drekkar.getDefault();

bus.register("Foo", function() {
    $('body').append('<p>Foo</p>');
    bus.post("Bar");
});