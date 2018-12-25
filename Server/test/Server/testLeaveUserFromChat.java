package Server;

import main.java.Server.LeaveUserFromChat;
import main.java.Server.ServerEnpoint;
import main.java.Until.AgentProfile;
import main.java.Until.ClientProfile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.Session;

import static org.mockito.Mockito.mock;


public class testLeaveUserFromChat {


    private Session agentSession;
    private Session clientSession;
    private AgentProfile agentProfile;
    private ClientProfile clientProfile;

    @Before
    public void init(){
        agentSession = mock(Session.class);
        clientSession = mock(Session.class);
        agentProfile = new AgentProfile(agentSession,1);
        clientProfile = new ClientProfile(clientSession);

        agentProfile.addSession(clientSession);
        clientProfile.setConnection(agentSession);

        ServerEnpoint.setNewConnection(agentSession, agentProfile);
        ServerEnpoint.setNewConnection(clientSession, clientProfile);
    }

    @After
    public void clear(){
        ServerEnpoint.removeConnection(agentSession);
        ServerEnpoint.removeConnection(clientSession);
        ServerEnpoint.removeConnection(agentSession);
        ServerEnpoint.removeConnection(clientSession);
        ServerEnpoint.removeClient(agentSession);
        ServerEnpoint.removeClient(clientSession);
        ServerEnpoint.removeAgent(clientSession);
        ServerEnpoint.removeAgent(agentSession);
    }


    @Test
    public void testClientLeave(){
        LeaveUserFromChat leaveUser = new LeaveUserFromChat();

        leaveUser.leave(clientProfile,0);

        Assert.assertFalse(((AgentProfile) ServerEnpoint.getProfileFromSession(agentSession)).noEmptyConnection(0));
        Assert.assertTrue(ServerEnpoint.getProfileFromSession(clientSession).getConnection() == null);


    }

    @Test
    public void testAgentLeave(){
        LeaveUserFromChat leaveUser = new LeaveUserFromChat();

        leaveUser.leave(agentProfile,0);

        Assert.assertFalse(((AgentProfile) ServerEnpoint.getProfileFromSession(agentSession)).noEmptyConnection(0));
        Assert.assertTrue(ServerEnpoint.getProfileFromSession(clientSession).getConnection() == null);


    }



}
