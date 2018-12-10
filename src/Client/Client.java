package Client;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.websocket.DeploymentException;

public class Client {

	static Logger log = Logger.getLogger(Client.class);

	private final ClientEndpointChat client;

	Client(ClientEndpointChat client){
		this.client = client;
	}

	public static void main(String args[]) {
		
		URI uri;
		try {
			uri = new URI("ws://localhost:8080/WebChat-0.0.1-SNAPSHOT/web");
			ClientEndpointChat client = new ClientEndpointChat(uri);

			new Client(client).start();
			
		} catch (URISyntaxException|DeploymentException|IOException e) {
			log.error(e);
			System.out.println("Сервер недоступен.");
		}
	}

	public void start() throws IOException {
		Scanner sc = new Scanner(System.in);
		String message = sc.nextLine();

		while(!message.equals("\\exit")) {
			if(message.length() > 0) {
				client.sendMessage(message);
			}
			message = sc.nextLine();
		}

		client.closeConnection();
	}
}
