package Beispielcode.LisasCode;

//Ekaterina Bychkova und Lisa Genters

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

  public static void main(String[] args) {
    try {
      //Enter portnumber on console
      System.out.println("Enter your port: ");
      Scanner sc = new Scanner(System.in);
      int portnumber = sc.nextInt();
      sc.nextLine(); //catch the \n, because nextInt() does use it

      Socket client = new Socket("localhost", portnumber);

      //Data the server sends us
      DataInputStream input = new DataInputStream(client.getInputStream());
      String text = input.readUTF();
      System.out.println(text);

      //Data the client sends
      DataOutputStream output = new DataOutputStream(client.getOutputStream());
      output.writeUTF(
        "Hi! I am the client with the address " + client.getLocalSocketAddress()
      );

      text = input.readUTF();
      System.out.println(text);

      //Data that the client sends to the server to encrypt
      String sendtext = "default";
      sendtext = sc.nextLine();
      output.writeUTF(sendtext);

      //Encrypted text is received and output
      String encryptedText = input.readUTF();
      System.out.println("Encrypted text:  " + encryptedText);

      client.close();
      sc.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
