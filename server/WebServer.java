package server;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;

/**
 * @author Gerasimos Strecker & Konstantin Regenhardt
 */

public class WebServer {
  private static Person person = new Person("Claudia", 44);
  private static Person person2 = new Person("Clarence", 63);
  private static Person[] people = { person, person2 };

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
          Date today = new Date();
          System.out.println(
            "\nVerbundener Client: " + client.getInetAddress().getHostAddress()
          );

          DataInputStream input = new DataInputStream(client.getInputStream());
          System.out.println("Request des Clients" + input.readUTF());

          DataOutputStream output = new DataOutputStream(
            client.getOutputStream()
          );
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
        if (split[0].equals("192")) {
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

  public String receiveRequest(String json) {
    return "";
  }
}
