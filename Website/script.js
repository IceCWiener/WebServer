$(document).ready(function () {
  console.log("stop inspecting element");
  $("#button").on("click", function () {
    var method = document.getElementById("Methods").value;
    var path = document.getElementById("pathInput").value;
    var protocol = document.getElementById("protocolInput").value;
    var accept = document.getElementById("acceptInput").value;
    var format = document.getElementById("jsonXML").value;
    var urlInput = document.getElementById("host").value;
    document.getElementById("containerHTML").focus();
    $("#containerHTML").text(
      createRequest(method, path, urlInput, protocol, accept, format)
    );
    $("#containerHTML").removeClass("disabled");
    
    var url = new URL("ws://" + urlInput + ":8080");
    
    console.log(url);
    var socket = new WebSocket(url);
    console.log("socket:" + socket);

    
    console.log(socket.data)

    socket.onopen = function (e){
      open = true;
      console.log("test");
      socket.send(
        createRequest(method, path, urlInput, protocol, accept, format)
      );
    };

    socket.onmessage = function(ev){
      alert(ev.data);
    };
    
  });
  $("input").each(function () {
    var input = this; // This is the jquery object of the input, do what you will
    input.addEventListener("input", resizeInput); // bind the "resizeInput" callback on "input" event
    resizeInput.call(input); // immediately call the function
  });

  function resizeInput() {
    if (this.value.length == 0) {
      this.style.width = this.placeholder.length + "ch";
    } else {
      this.style.width = this.value.length + "ch";
    }
    //console.log($("#containerHTML")[0], $("#outterWrapper")[0].clientWidth);
    $("#containerHTML")[0].clientWidth = $("#outterWrapper")[0].clientWidth;
    //console.log($("#containerHTML")[0].style.width, $("#outterWrapper"));
  }
  function createRequest(method, path, urlInput, protocol, accept, format) {
    var combined = {
      method: method,
      path: path,
      urlInput: urlInput,
      protocol: protocol,
      accept: accept,
      format: format,
    };
    return JSON.stringify(combined);
  }
});
