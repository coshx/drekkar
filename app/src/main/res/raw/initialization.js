Drekkar.getDefault().register("Before", function(name, data) {
    $('body').append('<p class="js">Before</p>');
});

Drekkar.getDefault().register("After", function(name, data) {
    $('body').append('<p class="js">After</p>');
});