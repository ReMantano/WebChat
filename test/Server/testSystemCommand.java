package Server;

import Until.Profile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;

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

        String message = "\\register UNKNOWN ";
        ServerEnpoint.setNewConnection(session,profile);

        boolean commandExist = sc.checkCommand(session,message);

        Assert.assertTrue(commandExist);
        Mockito.verify(session.getBasicRemote()).sendText("Вы зарегистрированы");

    }

    @Test
    public void testOnLeave() throws IOException {
        String message = "\\leave";

        boolean commandExist = sc.checkCommand(session,message);

        Assert.assertTrue(commandExist);
        Mockito.verify(session.getBasicRemote()).sendText("У вас нет собеседника");

    }

    @Test
    public void testOnExit() throws IOException {
        String message = "\\exit";
        ServerEnpoint.setNewConnection(session,profile);
        ServerEnpoint.addAgent(session);
        ServerEnpoint.addClient(session);

        boolean commandExist = sc.checkCommand(session,message);

        Assert.assertTrue(commandExist);
        Assert.assertFalse(ServerEnpoint.contain(session));

        Mockito.verify(session).close();

    }

    @Test
    public void testOnSendMessage(){
        String message = "Hello";

        boolean commandExist = sc.checkCommand(session,message);

        Assert.assertFalse(commandExist);
    }

    @Test
    public void testOnUnknownCommand() throws IOException {
        String message = "\\NONE";

        boolean commandExist = sc.checkCommand(session,message);

        Assert.assertTrue(commandExist);
        Mockito.verify(session.getBasicRemote()).sendText("Неизвестная команда");
    }
}
