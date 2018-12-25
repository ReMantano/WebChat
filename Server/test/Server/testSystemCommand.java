package Server;

import main.java.Server.ServerEnpoint;
import main.java.Server.SystemCommand;
import main.java.Until.AgentProfile;
import main.java.Until.ClientProfile;
import main.java.Until.Profile;
import org.json.simple.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class testSystemCommand {

    Session session;
    Profile profile;
    SystemCommand sc;

    @Before
    public void init(){
        session = mock(Session.class);
        sc = new SystemCommand();
        profile = new Profile(session);
        RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);

        when(session.isOpen()).thenReturn(true).thenReturn(false);
        when(session.getBasicRemote()).thenReturn(basic);
    }

    @After
    public void clear(){
        ServerEnpoint.removeClient(session);
        ServerEnpoint.removeAgent(session);
        ServerEnpoint.removeConnection(session);
    }

    @Test
    public void testOnRegister() throws IOException {

        String message = "register";
        JSONObject object = createJSONObject(message);
        object.put("Status", "Unknown");
        ServerEnpoint.setNewConnection(session,profile);

        boolean commandExist = sc.checkCommand(session,object);
        object.put("Message","Вы зарегистрированы");

        Assert.assertTrue(commandExist);
        Mockito.verify(session.getBasicRemote()).sendText(object.toJSONString());

    }

    @Test
    public void testOnLeave() throws IOException {
        ServerEnpoint.setNewConnection(session,profile);
        String message = "leave";
        JSONObject object = createJSONObject(message);
        object.put("Index", "0");

        boolean commandExist = sc.checkCommand(session,object);
        object.put("Message", "У вас нет собеседника");

        Assert.assertTrue(commandExist);
        Mockito.verify(session.getBasicRemote()).sendText(object.toJSONString());

    }

    @Test
    public void testExit() throws IOException {
        String message = "exit";
        JSONObject object = createJSONObject(message);
        ServerEnpoint.setNewConnection(session,profile);
        ServerEnpoint.addAgent(session);
        ServerEnpoint.addClient(session);

        boolean commandExist = sc.checkCommand(session,object);

        Assert.assertTrue(commandExist);
        Assert.assertFalse(ServerEnpoint.contain(session));

        Mockito.verify(session).close();

    }

    @Test
    public void testSendMessage(){
        String message = "text";

        boolean commandExist = sc.checkCommand(session,createJSONObject(message));

        Assert.assertFalse(commandExist);
    }

    @Test
    public  void testDeleteCommand(){
        AgentProfile agent = new AgentProfile(session, 1);
        Session temp = mock(Session.class);
        agent.addSession(temp);
        ServerEnpoint.setNewConnection(session,agent);
        ServerEnpoint.setNewConnection(temp,new ClientProfile(temp));

        String message = "delete";
        JSONObject object = createJSONObject(message);
        object.put("Index", "0");

        boolean commandExist = sc.checkCommand(session,object);

        Assert.assertTrue(commandExist);
        Assert.assertEquals(((AgentProfile) ServerEnpoint.getProfileFromSession(session))
                .getConnection(0), session);
    }

    @Test
    public void testUnknownCommand() throws IOException {
        String message = "NONE";
        JSONObject object = createJSONObject(message);

        boolean commandExist = sc.checkCommand(session,object);
        object.put("Message","Неизвестная команда");

        Assert.assertTrue(commandExist);
        Mockito.verify(session.getBasicRemote()).sendText(object.toJSONString());
    }

    private JSONObject createJSONObject(String command){
        JSONObject object = new JSONObject();
        object.put("Command", command.toUpperCase());
        return object;
    }
}
