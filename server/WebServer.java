package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
  private Person person = new Person("Claudia", 44);

  public static void main(String args[]) throws IOException {
    try (ServerSocket server = new ServerSocket(8080)) {
      System.out.println("Listening for connection on port 8080 ....");
      while (true) {
        try (Socket client = server.accept()) {
          Date today = new Date();
          System.out.println(
            "\nVerbundener Client: " + client.getInetAddress().getHostAddress()
          );

          DataInputStream input = new DataInputStream(client.getInputStream());
          System.out.println(input.readUTF());

          DataOutputStream output = new DataOutputStream(
            client.getOutputStream()
          );
          String httpResponse =
            ("Sie haben sich mit dem Server um " + today + " Verbunden\n");
          output.writeUTF(httpResponse);

          client.close();
        }
      }
    }
  }

  public String constructJson() {
    Gson gson = new Gson();

    String json = gson.toJson(person);
    return json;
  }
}
