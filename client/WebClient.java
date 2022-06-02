package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * This program demonstrates a client socket application that connects to
 * a web server and send a HTTP HEAD request.
 *
 * @author www.codejava.net
 */
public class WebClient {

  public static void main(String[] args) {
    String hostname;

    if (args.length < 1) {
      hostname = "localhost";
    } else {
      hostname = args[0];
    }

    int port = 8080;

    try (Socket socket = new Socket(hostname, port)) {
      System.out.println("Socket connected to: " + hostname + " " + port);

      DataOutputStream output = new DataOutputStream(socket.getOutputStream());
      String message = createMessage(RequestTypes.GET, "/", "{test: test}");
      output.writeUTF(message);

      DataInputStream input = new DataInputStream(socket.getInputStream());
      System.out.println(input.readUTF());

      socket.close();
    } catch (UnknownHostException ex) {
      System.out.println("Server not found: " + ex.getMessage());
    } catch (IOException ex) {
      System.out.println("I/O error: " + ex.getMessage());
    }
  }

  public static String createMessage(
    RequestTypes requestType,
    String path,
    String body
  ) {
    String head = "";
    String request;
    String protocol = "HTTP/1.1";

    switch (requestType) {
      case GET:
        head = head + "GET";
        break;
      case DELETE:
        head = head + "DELETE";
        break;
      case POST:
        head = head + "POST";
        break;
      case PUT:
        head = head + "PUT";
        break;
      default:
        break;
    }

    head += " " + path + ":" + protocol + "\n" + body;
    request = head + body;

    return request;
  }

  enum RequestTypes {
    GET,
    POST,
    PUT,
    DELETE,
  }
}
