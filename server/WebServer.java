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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @author Gerasimos Strecker & Konstantin Regenhardt
 */

public class WebServer {
  private static Gson gson = new Gson();

  public static void main(String args[]) throws IOException {
    try (ServerSocket server = new ServerSocket(8080)) {
      System.out.println(
          "Listening for connection on:\n " +
              getLocalIp() +
              ":" +
              server.getLocalPort());

      while (true) {
        try (Socket client = server.accept()) {
          System.out.println("\nVerbundener Client: " + client.toString());
          receiveRequest(client);

          client.close();
        }
      }
    }
  }

  private static void receiveRequest(Socket client) throws IOException {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(client.getInputStream()));
    String request = br.readLine();
    String[] requestParam = request.split(" ");
    String method = requestParam[0];
    String path = "." + requestParam[1];
    if (method.equals("POST")) {
      StringBuilder content = new StringBuilder();
      String line = br.readLine();
      String message = "";

      // Br with curly counter //Body needs empty line at he end
      for (int i = 0; i < 8; i++) {
        br.readLine();
      }
      int count1 = 0;
      int count2 = 0;

      while (((line = br.readLine()) != null)) {
        String str = line.toString();
        if (str.contains("{")) {
          count1++;
        }
        if (str.contains("}")) {
          count2++;
        }
        if (count1 == count2) {
          content.append(line);
          content.append(System.lineSeparator());
          message = content.toString();
          break;
        }
        content.append(line);
        content.append(System.lineSeparator());
        message = content.toString();
      }

      String name = createFile(message);
      sendResponse(client, "Inhalte wurden gespeichert in: " + name);
    } else if (method.equals("GET")) {
      String content = readFile(path);
      sendResponse(client, content);
    }
  }

  private static String createFile(String text) {
    String name = "file" + (new File(".\\files").list().length + 1);

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

    return name + ".txt";
  }

  public static void sendResponse(Socket client, String body)
      throws IOException {
    OutputStream clientOutput = client.getOutputStream();

    clientOutput.write("HTTP/1.1 200 OK".getBytes());
    clientOutput.write("Content-Type: text/plain\r\n".getBytes());
    clientOutput.write("\r\n".getBytes());
    clientOutput.write(body.getBytes());
    clientOutput.write("\r\n\r\n".getBytes());
    clientOutput.write("Connection: Closed\r\n".getBytes());
  }

  private static String readFile(String path) {
    String content = "";
    try {
      File file = new File(path);
      Scanner sc = new Scanner(file);
      while (sc.hasNextLine()) {
        content = content + sc.nextLine();
        // System.out.println(content);
      }
      sc.close();
    } catch (FileNotFoundException error) {
      System.out.println("Pfad wurde nicht gefunden.");
      // error.printStackTrace();
    }

    return content;
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
        if (split[0].equals("192") ||
            split[0].equals("10") ||
            split[0].equals("141")) {
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

  private static String readAllLinesWithStream(BufferedReader br) {
    return br.lines().collect(Collectors.joining(System.lineSeparator()));
  }
}
