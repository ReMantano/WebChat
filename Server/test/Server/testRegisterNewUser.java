package Server;

import main.java.Server.RegisteredNewUser;
import main.java.Server.ServerEnpoint;
import main.java.Profile.Profile;
import main.java.Until.Command;
import main.java.Until.Message;
import main.java.Until.Status;
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

public class testRegisterNewUser {


    private Session session;

    @Before
    public void init(){
        session = mock(Session.class);
    }

    @After
    public void clear(){
        ServerEnpoint.removeClient(session);
        ServerEnpoint.removeAgent(session);
        ServerEnpoint.removeConnection(session);
    }

    @Test
    public void testRegisterClient(){
        reg("client","SAME",session);
    }

    @Test
    public void testRegisterAgent(){
        reg("agent","SAME",session);
    }

    @Test
    public void testStatusError() throws IOException {
        Session session = mock(Session.class);
        RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);
        when(session.getBasicRemote()).thenReturn(basic);
        when(session.isOpen()).thenReturn(true).thenReturn(false);

        RegisteredNewUser register = new RegisteredNewUser();

        Message message = new Message();
        message.setProfile(null);

        register.registered(message, session);

        Mockito.verify(session.getBasicRemote()).sendText(message.toJsonString());

        ServerEnpoint.removeConnection(session);
        ServerEnpoint.removeAgent(session);
        ServerEnpoint.removeClient(session);
    }

    private void reg(String status, String name, Session session){

        RegisteredNewUser register = new RegisteredNewUser();

        Message message = new Message();
        message.setCommand(Command.REGISTER);
        message.setName(name);
        message.setProfile(Status.valueOf(status.toUpperCase()));

        register.registered(message, session);

        Profile prof = ServerEnpoint.getProfileFromSession(session);

        Assert.assertNotNull(prof);
        Assert.assertEquals(prof.getName(),name);
        Assert.assertEquals(prof.getStatus().name(),status.toUpperCase());

    }


}