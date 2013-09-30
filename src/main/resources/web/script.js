var SERVER = "eval";

function update()
{
    $.get( SERVER + "/" + $("#scriptId").val(), function( data ) {
        var response = jQuery.parseJSON(data);

        $("#scriptId").val(response.id);
        $("#lang").val(response.lang);
        $("#script").val(response.script);
        $("#status").html(response.status);
        $("#output").html(response.result);
    });
}

$(document).ready(function(){
    $("#eval").click(function(){
        data = {
            "lang" : $("#lang").val(),
            "script" : $("#script").val()
        }

        $.post( SERVER, JSON.stringify(data), function( data ) {
            var response = jQuery.parseJSON(data);

            $("#scriptId").val(response.id);
            $("#status").html(response.status);
            $("#output").html("");
        });

        setTimeout(function() {
            update();
        }, 1000);
    });

    $("#update").click(function(){
        update();
    });
});