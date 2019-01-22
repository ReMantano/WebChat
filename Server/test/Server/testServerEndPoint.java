package Server;

import main.java.Server.ServerEnpoint;
import main.java.Profile.AgentProfile;
import main.java.Profile.ClientProfile;
import main.java.Until.Command;
import main.java.Until.Message;
import main.java.Until.Status;
import org.junit.*;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class testServerEndPoint {

    private Session sessionTest1;
    private Session sessionTest2;
    private String message = "Hi";
    private ServerEnpoint server;


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
        server.removeConnection(sessionTest1);
        server.removeConnection(sessionTest2);
        server.removeAgent(sessionTest1);
        server.removeAgent(sessionTest2);
        server.removeClient(sessionTest1);
        server.removeClient(sessionTest2);

        server.closeConnection(sessionTest1);
        server.closeConnection(sessionTest2);
    }



    @Test
    public void testSendMessageButUserNotRegistered() throws IOException {
        Message message = new Message();
        message.setCommand(Command.TEXT);

        server.message(sessionTest1,message.toJsonString());

        message.setText("Вы не зарегистрированны");

        verify(sessionTest1.getBasicRemote()).sendText(message.toJsonString());

    }

    @Test
    public void testSendMessage() throws IOException {

        ClientProfile profile = new ClientProfile(sessionTest1);
        AgentProfile agent = new AgentProfile(sessionTest2,1);
        agent.addSession(sessionTest1);
        profile.setConnection(sessionTest2);
        server.setNewConnection(sessionTest1,profile);
        server.setNewConnection(sessionTest2,agent);

        Message message = new Message();
        message.setCommand(Command.TEXT);
        message.setIndex(0);

        server.message(sessionTest1,message.toJsonString());

        verify(sessionTest2.getBasicRemote()).sendText(message.toJsonString());

    }

    @Test
    public void testAgentWantSendMessage() throws IOException {

        AgentProfile agent = new AgentProfile(sessionTest2,1);
        server.setNewConnection(sessionTest2,agent);

        Message message = new Message();
        message.setCommand(Command.TEXT);
        message.setIndex(0);

        server.message(sessionTest2,message.toJsonString());
        message.setText("Дождитесь клиента");

        verify(sessionTest2.getBasicRemote()).sendText(message.toJsonString());

    }

    @Test
    public void testClientWantSendMessageButNotAgent() throws IOException {

        ClientProfile profile = new ClientProfile(sessionTest1);
        server.setNewConnection(sessionTest1,profile);
        Message message = new Message();
        message.setCommand(Command.TEXT);
        message.setText(this.message);

        server.message(sessionTest1,message.toJsonString());
        message.setText("Нет свободных агентов");
        ArrayList<String> list = new ArrayList<>();
        list.add(this.message+"\n");

        verify(sessionTest1.getBasicRemote()).sendText(message.toJsonString());
        Assert.assertTrue(profile.getNotSentMessagesList().equals(list));

    }

    @Test
    public void testConnectionClientToAgent() throws IOException {

        ClientProfile profile = new ClientProfile(sessionTest1);
        server.setNewConnection(sessionTest1,profile);

        Message message = new Message();
        message.setCommand(Command.REGISTER);
        message.setProfile(Status.AGENT);
        message.setName("A");
        message.setSize(1);
        server.message(sessionTest2,message.toJsonString());
        message.setText("A вы зарегистрированы как AGENT");

        Message message1 = new Message();
        message1.setCommand(Command.TEXT);
        server.message(sessionTest1,message1.toJsonString());

        message.setName("Сервер");

        verify(sessionTest2.getBasicRemote()).sendText(message.toJsonString());
        Assert.assertTrue(profile.getConnection() == sessionTest2);
        Assert.assertTrue(((AgentProfile) server.getProfileFromSession(sessionTest2))
                .getConnection(0) == sessionTest1);

    }

    @Test
    public void testWhenAgentConnectToWaitClient()  {

        ClientProfile profile = new ClientProfile(sessionTest1);

        server.setNewConnection(sessionTest1,profile);
        Message message = new Message();
        message.setCommand(Command.TEXT);
        server.message(sessionTest1,message.toJsonString());

        Message message1 = new Message();
        message1.setCommand(Command.REGISTER);
        message1.setProfile(Status.AGENT);
        message1.setName("A");
        message1.setSize(1);
        server.message(sessionTest2,message1.toJsonString());

        AgentProfile agent = (AgentProfile) server.getProfileFromSession(sessionTest2);
        System.out.println(agent.toString());
        Assert.assertTrue(agent.getConnection(0) == sessionTest1);
        Assert.assertTrue(profile.getConnection() == agent.getSelfSession());

    }
}

