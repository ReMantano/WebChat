package Server;

import main.java.Server.ServerEnpoint;
import main.java.Server.SystemCommand;
import main.java.Profile.AgentProfile;
import main.java.Profile.ClientProfile;
import main.java.Profile.Profile;
import main.java.Until.Command;
import main.java.Until.Message;
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
    Message message;

    @Before
    public void init(){
        session = mock(Session.class);
        sc = new SystemCommand();
        profile = new Profile(session);
        RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);
        message = new Message();

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

        message.setCommand(Command.REGISTER);
        ServerEnpoint.setNewConnection(session,profile);

        boolean commandExist = sc.checkCommand(session,message);
        message.setText("Вы зарегистрированы");

        Assert.assertTrue(commandExist);
        Mockito.verify(session.getBasicRemote()).sendText(message.toJsonString());

    }

    @Test
    public void testOnLeave() throws IOException {
        ServerEnpoint.setNewConnection(session,profile);

        message.setCommand(Command.LEAVE);
        message.setIndex(0);

        boolean commandExist = sc.checkCommand(session,message);
        message.setText("У вас нет собеседника");

        Assert.assertTrue(commandExist);
        Mockito.verify(session.getBasicRemote()).sendText(message.toJsonString());

    }

    @Test
    public void testExit() throws IOException {
        message.setCommand(Command.EXIT);
        message.setText("SomeText");
        ServerEnpoint.setNewConnection(session,profile);
        ServerEnpoint.addAgent(session);
        ServerEnpoint.addClient(session);

        boolean commandExist = sc.checkCommand(session,message);

        Assert.assertTrue(commandExist);
        Assert.assertFalse(ServerEnpoint.contain(session));

        Mockito.verify(session).close();

    }

    @Test
    public void testSendMessage(){

        message.setText("SomeText");
        message.setCommand(Command.TEXT);
        boolean commandExist = sc.checkCommand(session,message);

        Assert.assertFalse(commandExist);
    }

    @Test
    public  void testDeleteCommand(){
        AgentProfile agent = new AgentProfile(session, 1);
        Session temp = mock(Session.class);
        agent.addSession(temp);
        ServerEnpoint.setNewConnection(session,agent);
        ServerEnpoint.setNewConnection(temp,new ClientProfile(temp));

        message.setCommand(Command.DELETE);
        message.setIndex(0);

        boolean commandExist = sc.checkCommand(session,message);

        Assert.assertTrue(commandExist);
        Assert.assertEquals(((AgentProfile) ServerEnpoint.getProfileFromSession(session))
                .getConnection(0), session);
    }

    @Test
    public void testUnknownCommand() throws IOException {
        message.setCommand(Command.UNKNOWN);

        boolean commandExist = sc.checkCommand(session,message);
        message.setText("Неизвестная команда");

        Assert.assertTrue(commandExist);
        Mockito.verify(session.getBasicRemote()).sendText(message.toJsonString());
    }

}
