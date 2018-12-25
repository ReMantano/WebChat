package Server;

import main.java.Server.ServerEnpoint;
import main.java.Server.ServerEnpoint;
import main.java.Until.AgentProfile;
import main.java.Until.ClientProfile;
import main.java.Until.Profile;
import main.java.Until.Status;
import org.json.simple.JSONObject;
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
        JSONObject object = createJSONObject("text");
        server.message(sessionTest1,object.toJSONString());

        object.put("Message","Вы не зарегистрированны");

        verify(sessionTest1.getBasicRemote()).sendText(object.toJSONString());

    }

    @Test
    public void testSendMessage() throws IOException {

        ClientProfile profile = new ClientProfile(sessionTest1);
        AgentProfile agent = new AgentProfile(sessionTest2,1);
        agent.addSession(sessionTest1);
        profile.setConnection(sessionTest2);
        server.setNewConnection(sessionTest1,profile);
        server.setNewConnection(sessionTest2,agent);

        JSONObject object = createJSONObject("text");
        object.put("Index","0");

        server.message(sessionTest1,object.toJSONString());
        object.put("Index",0);

        verify(sessionTest2.getBasicRemote()).sendText(object.toJSONString());

    }

    @Test
    public void testAgentWantSendMessage() throws IOException {

        AgentProfile agent = new AgentProfile(sessionTest2,1);
        server.setNewConnection(sessionTest2,agent);

        JSONObject object = createJSONObject("text");
        object.put("Index","0");

        server.message(sessionTest2,object.toJSONString());
        object.put("Message","Дождитесь клиента");

        verify(sessionTest2.getBasicRemote()).sendText(object.toJSONString());

    }

    @Test
    public void testClientWantSendMessageButNotAgent() throws IOException {

        ClientProfile profile = new ClientProfile(sessionTest1);
        server.setNewConnection(sessionTest1,profile);
        JSONObject object = createJSONObject("text");
        object.put("Message",message);

        server.message(sessionTest1,object.toJSONString());
        object.put("Message","Нет свободных агентов");
        ArrayList<String> list = new ArrayList<>();
        list.add(message+"\n");

        verify(sessionTest1.getBasicRemote()).sendText(object.toJSONString());
        Assert.assertTrue(profile.getMessageInVoid().equals(list));

    }

    @Test
    public void testConnectionClientToAgent() throws IOException {

        ClientProfile profile = new ClientProfile(sessionTest1);
        server.setNewConnection(sessionTest1,profile);

        JSONObject object1 = createJSONObject("register");
        object1.put("Status","AGENT");
        object1.put("Name","A");
        object1.put("Size","1");
        server.message(sessionTest2,object1.toJSONString());
        object1.put("Message","A вы зарегистрированы как AGENT");

        JSONObject object = createJSONObject("text");
        server.message(sessionTest1,object.toJSONString());

        verify(sessionTest2.getBasicRemote()).sendText(object1.toJSONString());
        Assert.assertTrue(profile.getConnection() == sessionTest2);
        Assert.assertTrue(((AgentProfile) server.getProfileFromSession(sessionTest2))
                .getConnection(0) == sessionTest1);

    }

    @Test
    public void testWhenAgentConnectToWaitClient()  {

        ClientProfile profile = new ClientProfile(sessionTest1);

        server.setNewConnection(sessionTest1,profile);
        JSONObject object = createJSONObject("text");
        server.message(sessionTest1,object.toJSONString());

        JSONObject object1 = createJSONObject("register");
        object1.put("Status","AGENT");
        object1.put("Name","A");
        object1.put("Size","1");
        server.message(sessionTest2,object1.toJSONString());

        AgentProfile agent = (AgentProfile) server.getProfileFromSession(profile.getConnection());

        Assert.assertTrue(agent.getConnection(0) == sessionTest1);
        Assert.assertTrue(profile.getConnection() == agent.getSelfSession());

    }

    private JSONObject createJSONObject(String command){
        JSONObject object = new JSONObject();
        object.put("Command", command.toUpperCase());
        return object;
    }
}

