package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import javax.management.relation.RelationException;

public class WebServer {

  public static void main(String args[]) throws IOException {
    ServerSocket server = new ServerSocket(8080);
    System.out.println("Listening for connection on port 8080 ....");
    while (true) {
      try (Socket client = server.accept()) {
        Date today = new Date();
        System.out.println(
          "Verbundener Client: " + client.getInetAddress().getHostAddress()
        );

        DataInputStream input = new DataInputStream(client.getInputStream());
        System.out.println(input.readUTF());

        DataOutputStream output = new DataOutputStream(
          client.getOutputStream()
        );
        String httpResponse =
          ("Sie haben sich mit dem Server um " + today + " Verbunden");
        output.writeUTF(httpResponse);

        client.close();
      }
    }
  }
}
