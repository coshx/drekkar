Drekkar.getDefault().register("From Android", function(name, data) {
    $('body').append('<p class="js">Received From Android!</p>');
});

Drekkar.getDefault().post("From JS");