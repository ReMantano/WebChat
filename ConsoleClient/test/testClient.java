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

        verify(clientEndpointChat).sendMessage("Hi");
        verify(clientEndpointChat).closeConnection();
    }
}