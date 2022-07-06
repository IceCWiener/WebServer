package server;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.Scanner;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Gerasimos Strecker & Konstantin Regenhardt
 */

public class WebServer {
  private static String html =
    "<!DOCTYPE html> <html> <head> <title>Verteilte Systeme</title> <meta name='keywords' content='Vortrag' /> <link rel='stylesheet' href='style.css' /> <script src='https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js'></script> <style> input { padding: 15px; height: 1em; min-width: 20px; } #outterWrapper { display: flex; flex-direction: column; height: 100%; width: 100%; justify-content: space-evenly; align-items: center; position: absolute; }#wrapper { /* top: 50%; left: 50%; transform: translate(-50%, -50%); */ display: flex; flex-direction: column; gap: 20px; transition: all 0.5s ease-in-out; -webkit-transition: all 0.5s ease-in-out; background-color: lightgoldenrodyellow; padding: 15px; border-radius: 15px; } #box { margin: 0 50px; display: flex; flex-direction: column; width: fit-content; } .rows { display: flex; gap: 15px; } .rowElement { display: flex; flex-direction: column; } input, select, #button, #containerHTML { box-shadow: 5px 5px 7px rgb(171, 171, 171); border-radius: 15px; font-size: 2em; } ::placeholder { color: lightgray; }#Methods, #jsonXML, #button { height: 2em; } #button { width: 150px; background-color: #e248c0; align-self: baseline; } #containerHTML { background-color: lightgoldenrodyellow; width: calc(80%); height: fit-content; opacity: 1; padding: 15px; overflow-wrap: break-word; } #protocolInput, #acceptInput { background-color: white; }body { background-color: antiquewhite; /*background: url(https://cdn.dribbble.com/users/652916/screenshots/1925630/explosion_01_dribbble.gif); */ } .disabled { display: none; transition: opacity 0.5s ease-in-out; opacity: 0; } * { font-family: monospace, monospace; } .empty { flex-grow: 1; } </style> </head> <body> <div id='outterWrapper'> <div id='wrapper'> <h1 id='header'>Header:</h1> <div id='box'> <div class='rows'> <div class='rowElement'> <h1>Crud:</h1> <select name='Methods' id='Methods'> <!--<option value='' disabled selected>GET</option>--> <option value='GET'>GET</option> <option value='POST'>POST</option> <option value='POST'>PUT</option> <option value='POST'>DELETE</option> </select> </div> <div class='rowElement'> <h1>Path:</h1> <input type='text' placeholder='/<your Path>' id='pathInput' /> </div> </div> <div class='rows'> <div class='rowElement'> <h1>Host:</h1> <input type='text' id='host' placeholder='enter URL' /> </div> <div class='rowElement'> <h1>Protocol:</h1> <input type='text' value='HTTP/1.1' id='protocolInput' disabled /> </div> </div> <div class='rows'> <div class='rowElement'> <h1>Accept:</h1> <input type='text' value='application/' id='acceptInput' disabled /> </div> <div class='rowElement'> <h1>Format:</h1> <select id='jsonXML'> <option value='json'>JSON</option> <option value='xml'>XML</option> </select> </div> <div class='rowElement'> <h1 class='empty'></h1> <button id='button'>GO</button> </div> </div> </div> </div> <div id='containerHTML' class='disabled'></div> </div> </body> <script type='text/javascript'> $(document).ready(function () { console.log('stop inspecting element'); $('#button').on('click', function () { var method = document.getElementById('Methods').value; var path = document.getElementById('pathInput').value; var protocol = document.getElementById('protocolInput').value; var accept = document.getElementById('acceptInput').value; var format = document.getElementById('jsonXML').value; var urlInput = document.getElementById('host').value; document.getElementById('containerHTML').focus(); $('#containerHTML').text( createRequest(method, path, urlInput, protocol, accept, format) ); $('#containerHTML').removeClass('disabled');var url = new URL('ws://' + urlInput + ':8080');console.log(url); var socket = new WebSocket(url); console.log('socket:' + socket);console.log(socket.data);socket.onopen = function (e) { open = true; console.log('test'); socket.send( createRequest(method, path, urlInput, protocol, accept, format) ); };socket.onmessage = function (ev) { alert(ev.data); }; }); $('input').each(function () { var input = this; /* This is the jquery object of the input, do what you will*/ input.addEventListener('input', resizeInput); /* bind the 'resizeInput' callback on 'input' event*/ resizeInput.call(input); /* immediately call the function*/ });function resizeInput() { if (this.value.length == 0) { this.style.width = this.placeholder.length + 'ch'; } else { this.style.width = this.value.length + 'ch'; } /*console.log($('#containerHTML')[0], $('#outterWrapper')[0].clientWidth);*/ $('#containerHTML')[0].clientWidth = $('#outterWrapper')[0].clientWidth; /*console.log($('#containerHTML')[0].style.width, $('#outterWrapper'));*/ } function createRequest(method, path, urlInput, protocol, accept, format) { var combined = { method: method, path: path, urlInput: urlInput, protocol: protocol, accept: accept, format: format, }; return JSON.stringify(combined); } }); </script> </html> ";
  private static String testJson = "{\"name\":\"kaewin\"}";

  public static void main(String args[]) throws IOException {
    try (ServerSocket server = new ServerSocket(8080)) {
      System.out.println(
        "Listening for connection on:\n " +
        getLocalIp() +
        ":" +
        server.getLocalPort()
      );

      while (true) {
        try (Socket client = server.accept()) {
          System.out.println("\nVerbundener Client: " + client.toString());

          receiveRequest(client);
          sendResponse(client);

          client.close();
        }
      }
    }
  }

  private static void receiveRequest(Socket client) throws IOException {
    // System.out.println("Debug: got new client " + client.toString());
    BufferedReader br = new BufferedReader(
      new InputStreamReader(client.getInputStream()));
      String request = br.readLine(); // Now you get GET index.html HTTP/1.1
      String[] requestParam = request.split(" ");
      String method = requestParam[0];
      String path = requestParam[1];
      if (method.equals("POST")) {
        StringBuilder content =new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
          content.append(line);
          content.append(System.lineSeparator());
          String message = content.toString();
          System.out.println(message);
        }
  
  
      } else if (method.equals("GET")) {
        StringBuilder content =new StringBuilder();
        String line = "";
        while ((line = br.readLine()) != null) {
            content.append(line);
            content.append(System.lineSeparator());
            line = line + content.toString();
            System.out.println(line);
        }
        // TODO
  
      }
    }
    

  public static void sendResponse(Socket client) throws IOException {
    OutputStream clientOutput = client.getOutputStream();
    clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
    clientOutput.write(("ContentType: text/html\r\n").getBytes());
    clientOutput.write("\r\n".getBytes());
    clientOutput.write(html.getBytes());
    clientOutput.write("\r\n\r\n".getBytes());
  }

  private static String getLocalIp() throws SocketException {
    String localIp = null;
    String address;
    String[] split;

    Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
    while (netInterfaces.hasMoreElements()) {
      NetworkInterface netface = netInterfaces.nextElement();
      Enumeration<InetAddress> inetAddresses = netface.getInetAddresses();
      while (inetAddresses.hasMoreElements()) {
        InetAddress i = inetAddresses.nextElement();
        address = i.getHostAddress();
        split = address.split("\\.");
        if (
          split[0].equals("192") ||
          split[0].equals("10") ||
          split[0].equals("141")
        ) {
          localIp = address;
          break;
        }
      }
      if (localIp != null) {
        break;
      }
    }

    return localIp;
  }

  public static String constructJson(Person[] people, String... msg) {
    Gson gson = new Gson();
    String msgJson = gson.toJson(msg);
    String peopleJson = gson.toJson(people);
    String json = gson.toJson(peopleJson + ", " + msgJson);
    return json;
  }

  public Response createHttpResponse() throws IOException {
    OkHttpClient client = new OkHttpClient().newBuilder().build();
    MediaType mediaType = MediaType.parse("text/plain");
    RequestBody body = RequestBody.create("message", mediaType);
    Request request = new Request.Builder()
      .url("192.168.0.24:8080")
      .method("POST", body)
      .build();
    Response response = client.newCall(request).execute();

    return response;
  }

  private static void createFile(String text) {
    String name = "person" + (new File(".\\files").list().length + 1);

    try {
      File file = new File(".//files//" + name + ".txt");
      if (file.createNewFile()) {
        System.out.println("Neuen Eintrag erstellt: " + file.getName());
      } else {
        System.out.println("Dateiname schon vergeben.");
      }

      FileWriter writer = new FileWriter(file);
      writer.write(text);
      writer.close();
    } catch (IOException error) {
      System.out.println("IOError:\n" + error);
    }
  }

  private static String readFile(String path) {
    String content = "";
    try {
      File file = new File(path);
      Scanner sc = new Scanner(file);
      while (sc.hasNextLine()) {
        content = content + sc.nextLine();
        System.out.println(content);
      }
      sc.close();
    } catch (FileNotFoundException error) {
      System.out.println("Pfad wurde nicht gefunden.");
      // error.printStackTrace();
    }

    return content;
  }
}
