Drekkar.getDefault().register("ThirdEvent", function(name, data) {
    $('body').append('<p class="js">You should see me and only me</p>');
});

Drekkar.getDefault().register("FourthEvent", function(name, data) {
    $('body').append('<p class="js">You should not see me</p>');
});

Drekkar.getDefault().post("FirstEvent");