package Server;

import main.java.Server.LeaveUserFromChat;
import main.java.Server.ServerEnpoint;
import main.java.Until.Profile;
import main.java.Until.Status;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.Session;

import static org.mockito.Mockito.mock;

public class testLeaveUserFromChat {

    private Session sessionTest1;
    private Session sessionTest2;
    private Profile profileTest1;
    private Profile profileTest2;

    @Before
    public void init(){
        sessionTest1 = mock(Session.class);
        sessionTest2 = mock(Session.class);
        profileTest1 = new Profile(sessionTest1);
        profileTest2 = new Profile(sessionTest1);

        profileTest1.setConnection(sessionTest2);
        profileTest2.setConnection(sessionTest1);

        profileTest1.setStatus(Status.CLIENT);
        profileTest2.setStatus(Status.AGENT);

        ServerEnpoint.setNewConnection(sessionTest1,profileTest1);
        ServerEnpoint.setNewConnection(sessionTest2,profileTest2);
    }

    @After
    public void clear(){
        ServerEnpoint.removeConnection(sessionTest1);
        ServerEnpoint.removeConnection(sessionTest2);
        ServerEnpoint.removeClient(sessionTest1);
        ServerEnpoint.removeAgent(sessionTest2);
    }

    @Test
    public void testClientLeave(){
        LeaveUserFromChat leaveUser = new LeaveUserFromChat();

        leaveUser.leave(sessionTest1);

        Assert.assertTrue(ServerEnpoint.getProfileFromSession(sessionTest1).getConnection() == null);
        Assert.assertTrue(ServerEnpoint.getProfileFromSession(sessionTest2).getConnection() == null);


    }

    @Test
    public void testAgentLeave(){
        LeaveUserFromChat leaveUser = new LeaveUserFromChat();

        leaveUser.leave(sessionTest2);

        Assert.assertTrue(ServerEnpoint.getProfileFromSession(sessionTest1).getConnection() == null);
        Assert.assertTrue(ServerEnpoint.getProfileFromSession(sessionTest2).getConnection() == null);


    }


}
