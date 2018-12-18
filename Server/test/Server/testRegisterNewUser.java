package Server;

import main.java.Server.RegisteredNewUser;
import main.java.Server.ServerEnpoint;
import main.java.Until.Profile;
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
/*

    Session session;

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
    public void testOnStatusError() throws IOException {
        String text = "\\register unknown unknown";
        Session session = mock(Session.class);
        RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);
        when(session.getBasicRemote()).thenReturn(basic);
        when(session.isOpen()).thenReturn(true).thenReturn(false);

        RegisteredNewUser register = new RegisteredNewUser();

        register.registered(text, session);

        Mockito.verify(session.getBasicRemote()).sendText("Вы указали неправильный статус");
    }

    public void reg(String status, String name, Session session){

        String text = "\\register " + status + " " + name;
        RegisteredNewUser register = new RegisteredNewUser();

        register.registered(text, session);

        Profile prof = ServerEnpoint.getProfileFromSession(session);

        Assert.assertTrue(prof != null);
        Assert.assertTrue(prof.getName().equals(name + " "));
        Assert.assertTrue(prof.getStatus().name().equals(status.toUpperCase()));

    }
*/

}