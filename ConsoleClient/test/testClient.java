import org.json.simple.JSONObject;
import org.junit.Test;
import main.java.Client;
import main.java.ClientEndpointChat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class testClient {


    @Test
    public void testClientWork() throws IOException {
        ClientEndpointChat clientEndpointChat = mock(ClientEndpointChat.class);
        ByteArrayInputStream bais = new ByteArrayInputStream("Hi\n\\exit".getBytes());
        System.setIn(bais);

        Client client = new Client(clientEndpointChat);
        client.start();

        JSONObject object = new JSONObject();
        object.put("Command","TEXT");
        object.put("Index","0");
        object.put("Size","1");
        object.put("Message","Hi");
        verify(clientEndpointChat).sendMessage(object.toJSONString());
    }
}