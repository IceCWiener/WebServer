//Ekaterina Bychkova und Lisa Genters

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;


public class Server {

	private ServerSocket server;
	
	//Constructor
	public Server(int port) {

		try{
		
			server = new ServerSocket(port);
			server.setSoTimeout(100000);
			
		}catch (SocketException e){
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
	
		
	}
	public void run() {
		while(true) {
			try {
				// Output
				System.out.println("Hi! I am the server with the port " + server.getLocalPort());
				System.out.println("Waiting for Client... ");
				Socket client = server.accept();

				// We can send something to the Client
				DataOutputStream output = new DataOutputStream(client.getOutputStream());
				output.writeUTF("Hi! I am the server with the port " + server.getLocalPort());
								
				//Data the client sends us
				DataInputStream input = new DataInputStream(client.getInputStream());
				System.out.println(input.readUTF());
				System.out.println("RemoteSocket address:  " + client.getRemoteSocketAddress() + "\n" );

				output.writeUTF("Hi! Here you can perform a very simple caesar encryption. Enter your text here: "+"\n");
				
				//Input is stored and processed in string
				String clientText = input.readUTF();
				System.out.println("Input before encryption:  " + clientText);
				// Loop for the encryption
				String newText = "";
				for ( int i = 0; i < clientText.length(); i++) {
					char letter = clientText.charAt(i);
					
					if(letter == 'z') {
						newText += 'a';
					}
					else if(letter == ' '){
						newText += ' ';
					}
					else {
						newText += (char) (letter + 1);
					}
				}
				
				System.out.println("Input after encryption: " + newText + "\n");
				
				//Encrypted text is sent to the client
				output.writeUTF(newText);

				client.close();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Enter your port: "); 
		Scanner sc = new Scanner(System.in);
		int portnumber = sc.nextInt(); 
		sc.close(); 
		Server s = new Server(portnumber);
		s.run();
	}

}
