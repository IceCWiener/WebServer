package server;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Gerasimos Strecker & Konstantin Regenhardt
 */

public class WebServer {
  private static Person person1 = new Person("Claudia", 44);
  private static Person person2 = new Person("Clarence", 63);
  private static Person[] people = { person1, person2 };

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
          if (client.isConnected()){
          }
          Date today = new Date();
          System.out.println(
            "\nVerbundener Client: " + client.getInetAddress().getHostAddress()
          );
          

          handleClient(client);
          System.out.println("Send Output:");
          sendOutput(client);
          // DataInputStream input = new DataInputStream(client.getInputStream());
          // System.out.println("Request des Clients" + input.readUTF());

          // DataOutputStream output = new DataOutputStream(
          //   client.getOutputStream()
          // );
          // String httpResponse =
          //   // "Sie haben sich mit dem Server um " +
          //   // today +
          //   // " Verbunden\n Antwort auf Ihren request:\n" +
          //   constructJson(people);
          // output.writeUTF(httpResponse);

          // client.close();
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
        if (split[0].equals("192") || split[0].equals("10")) {
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

  private static void handleClient(Socket client) throws IOException {
    System.out.println("Debug: got new client " + client.toString());
    BufferedReader br = new BufferedReader(
      new InputStreamReader(client.getInputStream())
    );

    StringBuilder requestBuilder = new StringBuilder();
    String line;
    while (!(line = br.readLine()).isBlank()) {
      requestBuilder.append(line + "\r\n");
    }

    String request = requestBuilder.toString();
    System.out.println(request);
  }

  public static void sendOutput(Socket client) throws IOException {
    OutputStream clientOutput = client.getOutputStream();
    clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
    clientOutput.write(("ContentType: text/html\r\n").getBytes());
    clientOutput.write("\r\n".getBytes());
    clientOutput.write("<b>It works!</b>".getBytes());
    clientOutput.write("\r\n\r\n".getBytes());
    clientOutput.flush();
    client.close();
  }
}
