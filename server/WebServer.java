package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/** * Java program to create a simple HTTP Server to demonstrate how to use * ServerSocket and Socket class. * * @author Javin Paul */
public class WebServer {

  public static void main(String args[]) throws IOException {
    ServerSocket server = new ServerSocket(8080);
    System.out.println("Listening for connection on port 8080 ....");
    while (true) {
      try (Socket socket = server.accept()) {
        Date today = new Date();
        String httpResponse =
          ("Sie haben sich mit dem Server um " + today + " Verbunden");
        socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
        //server.close();
        System.out.println(
          "Verbundener Client: " + socket.getInetAddress().getHostAddress()
        );
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(
          new InputStreamReader(input)
        );

        String line;

        while ((line = reader.readLine()) != null) {
          System.out.println(line);
        }
      }
    }
  }
}
