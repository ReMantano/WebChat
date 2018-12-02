package Client;

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
		ContainerProvider.getWebSocketContainer().toString();
		ContainerProvider.getWebSocketContainer().connectToServer(this,uri);
	}

	@OnOpen
	public void openConnection(Session session) {
		this.session = session;
	}
	
	@OnMessage
	public void getMessage(String message) {
		System.out.println(message);
	}
	
	@OnClose
	public void closeConnection() {
		if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
	}
	

	
	void sendMessage(String message) throws IOException {
		session.getBasicRemote().sendText(message);
	}
}