package main.java;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint
public class ClientEndpointChat {
	
	private Session session;
	
	public ClientEndpointChat(URI uri) throws DeploymentException, IOException {
		ContainerProvider.getWebSocketContainer().connectToServer(this,uri);
	}

	@OnOpen
	public void openConnection(Session session) {
		this.session = session;
	}
	
	@OnMessage
	public void getMessage(String message) {
	    JSONObject object = getJSONFromString(message);
		String name = (String) object.get("name");
	    try {
			JSONArray array = (JSONArray) object.get("text");
			for(Object ob : array){
				System.out.print(name+": "+ob);
			}
		}catch (ClassCastException e){
			System.out.println(name+": "+object.get("text"));
		}


	}
	
	@OnClose
	public void closeConnection() {
		if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                Client.log.error(e);
            }
        }
	}
	

	
	public void sendMessage(String message) throws IOException {
		session.getBasicRemote().sendText(message);
	}

    private JSONObject getJSONFromString(String jString){
        try {
            Object o = new JSONParser().parse(jString);
            return (JSONObject) o;
        } catch (ParseException e) {

            return null;
        }
    }
}
