package Client;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.websocket.DeploymentException;

public class Client {

	static Logger log = Logger.getLogger(Client.class);

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
			
		} catch (URISyntaxException|DeploymentException|IOException e) {
			log.error(e);
			System.out.println("Сервер недоступен.");
		}
	}
}
