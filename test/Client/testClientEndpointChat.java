package Client;

import org.junit.Test;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class testClientEndpointChat {

    @Test(expected = DeploymentException.class)
    public void testErrorConnectionToServer() throws IOException, DeploymentException, URISyntaxException {
        new ClientEndpointChat(new URI("ws://localhost:8080/WebChat-0.0.1-SNAPSHOT/web"));
    }


}
