$(document).ready(function () {
  console.log("stop inspecting element");
  var receivedHTML = "Platzhalter";
  $("#button").on("click", function () {
    
    $("#containerHTML").text(createRequest());
    $("#containerHTML").removeClass("disabled");
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
  }
  function createRequest() {
    var methods = document.getElementById("Methods").value;
    var path = document.getElementById("pathInput").value;
    var urlInput = document.getElementById("host").value;
    var protocol = document.getElementById("protocolInput").value;
    var accept = document.getElementById("acceptInput").value;
    var format = document.getElementById("jsonXML").value;
    console.log(methods, path, urlInput, protocol, accept, format);
    return methods + path + urlInput + protocol + accept + format;
  }
});
