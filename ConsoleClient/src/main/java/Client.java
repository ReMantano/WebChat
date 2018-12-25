package main.java;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.websocket.DeploymentException;

public class Client {

	static Logger log = Logger.getLogger(Client.class);

	private final ClientEndpointChat client;

	public Client(ClientEndpointChat client){
		this.client = client;
	}

	public static void main(String args[]) {

		URI uri;
		try {
			uri = new URI("ws://localhost:8080/Server/web");
			ClientEndpointChat client = new ClientEndpointChat(uri);

			new Client(client).start();
			
		} catch (URISyntaxException|DeploymentException|IOException e) {
			log.error(e);
			System.out.println("Сервер недоступен.");
		}
	}

	public void start() throws IOException {
		log.info("Соединение установлено");
		Scanner sc = new Scanner(System.in);
		String message = sc.nextLine();

		JSONObject jObject = new JSONObject();
		jObject.put("Size","1");
		jObject.put("Index","0");

		while(!message.equals("\\exit")) {
			if(message.length() > 0) {
				fillJSONObject(message,jObject);
				client.sendMessage(jObject.toJSONString());
			}
			message = sc.nextLine();
		}

	}

	private void fillJSONObject(String text, JSONObject object){
		if (checkCommand(text)){
			text = text.substring(1);
			String[] array = text.split(" ");
			try {
				object.put("Command", array[0].toUpperCase());
				object.put("Status", array[1].toUpperCase());
				object.put("Name", array[2].toUpperCase());
			}catch (IndexOutOfBoundsException e){
				System.out.println("Неверная команда");
			}
		}else{
			object.put("Command","TEXT");
			object.put("Message",text);
		}
	}

	private boolean checkCommand(String text){
		if(text.charAt(0) == '\\')
			return true;
		else
			return false;
	}
}
