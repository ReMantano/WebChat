package Server;

import Until.Profile;
import Until.Status;
import org.junit.*;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class testServerEndPoint {

    Session sessionTest1;
    Session sessionTest2;
    String message = "Hi";
    ServerEnpoint server;


    @Before
    public void init(){
        sessionTest1 = mock(Session.class);
        sessionTest2 = mock(Session.class);

        when(sessionTest1.isOpen()).thenReturn(true).thenReturn(false);
        when(sessionTest2.isOpen()).thenReturn(true).thenReturn(false);
        when(sessionTest1.getBasicRemote()).thenReturn(mock(RemoteEndpoint.Basic.class));
        when(sessionTest2.getBasicRemote()).thenReturn(mock(RemoteEndpoint.Basic.class));

        server = new ServerEnpoint();
    }

    @After
    public void clear(){
        ServerEnpoint.removeConnection(sessionTest1);
        ServerEnpoint.removeConnection(sessionTest2);
        ServerEnpoint.removeAgent(sessionTest1);
        ServerEnpoint.removeAgent(sessionTest2);
        ServerEnpoint.removeClient(sessionTest1);
        ServerEnpoint.removeClient(sessionTest2);

        server.closeConnection(sessionTest1);
        server.closeConnection(sessionTest2);
    }



    @Test
    public void testSendMessageButUserNotRegistered() throws IOException {
        server.message(sessionTest1,message);

        verify(sessionTest1.getBasicRemote()).sendText("Вы не зарегистрированны");

    }

    @Test
    public void testSendMessage() throws IOException {

        Profile profile = new Profile(sessionTest1);
        profile.setStatus(Status.CLIENT);
        profile.setConnection(sessionTest2);
        ServerEnpoint.setNewConnection(sessionTest1,profile);

        server.message(sessionTest1,message);

        verify(sessionTest2.getBasicRemote()).sendText(profile.getName() + ": " + message);

    }

    @Test
    public void testAgentWantSendMessage() throws IOException {

        Profile profile = new Profile(sessionTest1);
        profile.setStatus(Status.AGENT);
        ServerEnpoint.setNewConnection(sessionTest1,profile);

        server.message(sessionTest1,message);

        verify(sessionTest1.getBasicRemote()).sendText("Дождитесь клиента");

    }

    @Test
    public void testClientWantSendMessageButNotAgent() throws IOException {

        Profile profile = new Profile(sessionTest1);
        profile.setStatus(Status.CLIENT);
        ServerEnpoint.setNewConnection(sessionTest1,profile);

        server.message(sessionTest1,message);

        verify(sessionTest1.getBasicRemote()).sendText("Нет свободных агентов");
        Assert.assertTrue(profile.getMessageInVoid().equals(profile.getName() + ": "+message+"\n"));

    }

    @Test
    public void testConnectionClientToAgent() throws IOException {

        Profile profile = new Profile(sessionTest1);
        profile.setStatus(Status.CLIENT);
        ServerEnpoint.setNewConnection(sessionTest1,profile);

        server.message(sessionTest2,"\\register agent A");
        server.message(sessionTest1,message);

        verify(sessionTest2.getBasicRemote()).sendText("A вы зарегистрированы как agent");
        Assert.assertTrue(profile.getConnection() == sessionTest2);
        Assert.assertTrue(ServerEnpoint.getProfileFromSession(sessionTest2).getConnection() == sessionTest1);

    }

    @Test
    public void testWhenAgentConnectToWaitClient()  {

        Profile profile = new Profile(sessionTest1);
        profile.setStatus(Status.CLIENT);

        ServerEnpoint.setNewConnection(sessionTest1,profile);
        server.message(sessionTest1,"Help");

        server.message(sessionTest2,"\\register agent A");

        Profile agent = ServerEnpoint.getProfileFromSession(profile.getConnection());

        Assert.assertTrue(agent.getConnection() == sessionTest1);
        Assert.assertTrue(profile.getConnection() == agent.getSelfWriter());

    }
}

