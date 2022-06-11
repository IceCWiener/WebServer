package server;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * @author Gerasimos Strecker & Konstantin Regenhardt
 */

public class WebServer {
  private static Person person = new Person("Claudia", 44);
  private static Person person2 = new Person("Clarence", 63);
  private static Person[] people = { person, person2 };

  public static void main(String args[]) throws IOException {
    try (ServerSocket server = new ServerSocket(8080)) {
      System.out.println("Listening for connection on port 8080 ....");
      while (true) {
        try (Socket client = server.accept()) {
          Date today = new Date();
          System.out.println(
              "\nVerbundener Client: " + client.getInetAddress().getHostAddress());

          DataInputStream input = new DataInputStream(client.getInputStream());
          System.out.println("Request des Clients" + input.readUTF());

          DataOutputStream output = new DataOutputStream(
              client.getOutputStream());
          String httpResponse =
              // "Sie haben sich mit dem Server um " +
              // today +
              // " Verbunden\n Antwort auf Ihren request:\n" +
              constructJson(people);
          output.writeUTF(httpResponse);

          client.close();
        }
      }
    }
  }

  public static String constructJson(Person[] people) {
    Gson gson = new Gson();

    String json = gson.toJson(people);
    System.out.println(json);
    return json;
  }
}
