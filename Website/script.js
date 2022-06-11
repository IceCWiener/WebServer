$( document ).ready(function() {
    console.log( "stop inspecting element" );
    var receivedHTML = "Platzhalter"
    $("#button").on("click", function()
    {
        var url = $("#URL_input").val();
        $("#containerHTML").text(receivedHTML);
        $("#containerHTML").removeClass("disabled");

        console.log(url);
    })

    $("input").each(function(){
        var input = this; // This is the jquery object of the input, do what you will
        input.addEventListener('input', resizeInput); // bind the "resizeInput" callback on "input" event
        resizeInput.call(input); // immediately call the function
    });

    function resizeInput() {
        if(this.value.length == 0){
            this.style.width = this.placeholder.length + "ch";
        }else{
            this.style.width = this.value.length + "ch";
        }
    }
 });