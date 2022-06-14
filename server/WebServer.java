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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
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
  private static Person person1 = new Person("Claudia", 44);
  private static Person person2 = new Person("Clarence", 63);
  private static Person[] people = { person1, person2 };
  private static String ip = "localhost";

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
          if (client.isConnected()) {}
          // Date today = new Date();
          // System.out.println(
          //   "\nVerbundener Client: " + client.getInetAddress().getHostAddress()
          // );

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
        if (
          split[0].equals("192") ||
          split[0].equals("10") ||
          split[0].equals("141")
        ) {
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
    MediaType mediaType = MediaType.parse("text/html");
    RequestBody body = RequestBody.create("message", mediaType);
    Request request = new Request.Builder()
      .url(ip + ":8080")
      .method("POST", body)
      .build();
    Response response = client.newCall(request).execute();

    return response;
  }

  private static void handleClient(Socket client) throws IOException {
    System.out.println("Debug: got new client " + client.toString());
    DataInputStream dis = new DataInputStream(client.getInputStream());
    // BufferedReader br = new BufferedReader(
    //   new InputStreamReader(client.getInputStream())
    // );

    // StringBuilder requestBuilder = new StringBuilder();
    // String line;
    // while (!(line = br.readLine()).isBlank()) {
    //   requestBuilder.append(line + "\r\n");
    // }

    // String request = requestBuilder.toString();
    //TODO: dataInputStream dis in httpRequest aufschlüsseln (deseriealizen)
    String request = dis.readUTF();
    String[] requestsLines = request.split("\r\n");
    String[] requestLine = requestsLines[0].split(" ");
    String method = requestLine[0];
    String path = requestLine[1];
    String version = requestLine[2];
    String host = requestsLines[1].split(" ")[1];

    List<String> headers = new ArrayList<>();
    for (int h = 2; h < requestsLines.length; h++) {
      String header = requestsLines[h];
      headers.add(header);
    }

    String accessLog = String.format(
      "client: %s,\n method: %s,\n path: %s,\n version: %s,\n host: %s,\n headers: %s",
      client.toString(),
      method,
      path,
      version,
      host,
      headers.toString()
    );
    System.out.println(accessLog);

    Path filePath = getFilePath(path);
    if (Files.exists(filePath)) {
      // file exist
      String contentType = guessContentType(filePath);
      sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath));
    } else {
      // 404
      byte[] notFoundContent = "<h1>Not found :(</h1>".getBytes();
      sendResponse(client, "404 Not Found", "text/html", notFoundContent);
    }

    String request = requestBuilder.toString();
    System.out.println(request);
  }

  private static void sendResponse(
    Socket client,
    String status,
    String contentType,
    byte[] content
  )
    throws IOException {
    OutputStream clientOutput = client.getOutputStream();
    clientOutput.write(("HTTP/1.1 \r\n" + status).getBytes());
    clientOutput.write(("ContentType: " + contentType + "\r\n").getBytes());
    clientOutput.write("\r\n".getBytes());
    clientOutput.write(content);
    clientOutput.write("\r\n\r\n".getBytes());
    clientOutput.flush();
    client.close();
  }

  private static String guessContentType(Path filePath) throws IOException {
    return Files.probeContentType(filePath);
  }

  private static Path getFilePath(String path) {
    if ("/".equals(path)) {
      path = "/index.html";
    }

    return Paths.get("/website", path);
  }
}
