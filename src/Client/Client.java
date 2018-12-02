package Client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.websocket.DeploymentException;

public class Client {

	public static void main(String args[]) {
		
		URI uri;
		try {
			uri = new URI("ws://localhost:8080/web");
			ClientEndpointChat client = new ClientEndpointChat(uri);
			Scanner sc = new Scanner(System.in);
			String message = "start";
			
			while(!message.equals("\\exit")) {
				if(message.length() > 0) {
					message = sc.nextLine();
					client.sendMessage(message);
				}
			}
			
			client.closeConnection();
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeploymentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
