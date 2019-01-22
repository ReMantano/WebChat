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
		jObject.put("size","1");
		jObject.put("index","0");

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
				object.put("command", array[0].toUpperCase());
				object.put("profile", array[1].toUpperCase());
				object.put("name", array[2].toUpperCase());
				System.out.println(object.toJSONString());
			}catch (IndexOutOfBoundsException e){
				System.out.println("Неверная команда");
			}
		}else{
			object.put("command","TEXT");
			object.put("text",text);
		}
	}

	private boolean checkCommand(String text){
		if(text.charAt(0) == '\\')
			return true;
		else
			return false;
	}
}
