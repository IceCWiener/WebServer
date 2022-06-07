package client;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import server.Person;

/**
 * @author Gerasimos Strecker & Konstantin Regenhardt
 */

public class WebClient {

  public static void main(String[] args) {
    String hostname;

    if (args.length < 1) {
      hostname = "141.45.62.167";
    } else {
      hostname = args[0];
    }

    int port = 8080;

    try (Socket socket = new Socket(hostname, port)) {
      System.out.println("\nSocket connected to: " + hostname + " " + port);

      DataOutputStream output = new DataOutputStream(socket.getOutputStream());
      String message = createHttpRequest("GET", "/", "{test: test}");
      output.writeUTF(message);

      DataInputStream input = new DataInputStream(socket.getInputStream());
      System.out.println(input.readUTF());

      Gson gson = new Gson();
      Person[] PersonArray = gson.fromJson(output.toString(), Person[].class);
      System.out.println(PersonArray);

      socket.close();
    } catch (UnknownHostException ex) {
      System.out.println("Server not found: " + ex.getMessage());
    } catch (IOException ex) {
      System.out.println("I/O error: " + ex.getMessage());
    }
  }

  public static String createHttpRequest(
    String crud,
    String path,
    String body
  ) {
    String head = "";
    String request;
    String protocol = "HTTP/1.1";

    switch (crud) {
      case "GET":
        head = head + "GET";
        break;
      case "DELETE":
        head = head + "DELETE";
        break;
      case "POST":
        head = head + "POST";
        break;
      case "PUT":
        head = head + "PUT";
        break;
      default:
        break;
    }

    head += " " + path + ":" + protocol + "\n" + body;
    request = head + body;

    return request;
  }
}
