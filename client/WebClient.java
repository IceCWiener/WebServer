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

    // URL url;

    // try {
    //   url = new URL(args[0]);
    // } catch (MalformedURLException ex) {
    //   ex.printStackTrace();
    //   return;
    // }

    // String hostname = url.getHost();
    int port = 8080;

    try (Socket socket = new Socket(hostname, port)) {
      System.out.println("Socket connected to: " + hostname + " " + port);
      PrintStream output = new PrintStream(socket.getOutputStream());
      // PrintWriter writer = new PrintWriter(output, true);

      //writer.println("HEAD " + url.getPath() + " HTTP/1.1");
      // writer.println("Host: " + hostname);
      // writer.println("User-Agent: Simple Http Client");
      // writer.println("Accept: text/html");
      // writer.println("Accept-Language: en-US");
      // writer.println("Connection: close");
      // writer.println();

      // System.out.println(
      //   "Setting up print of InputStream and Scanner. Message:"
      // );
      // InputStream inputStream = socket.getInputStream();
      // Scanner sc = new Scanner(inputStream);
      // while (sc.hasNextLine()) {
      //   System.out.println(sc.nextLine());
      // }
      // sc.close();
      // System.out.println("Scanner closed");

      // BufferedReader reader = new BufferedReader(
      //   new InputStreamReader(inputStream)
      // );
      // String readLine = reader.readLine();
      // System.out.println(readLine);

      // String line;

      // while ((line = reader.readLine()) != null) {
      //   System.out.println(line);
      // }

      //TEST
      String message = createMessage(RequestTypes.GET, "/", "{test: test}");
      System.out.println(
        "Created request message: " + message + " END MESSAGE"
      );

      output.println(message);
      output.close();
      socket.close();
      System.out.println("OutputPrintStream and Socket closed.");
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
