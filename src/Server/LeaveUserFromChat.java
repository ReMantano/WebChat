package Server;

import java.io.IOException;

import javax.websocket.Session;

import Until.Profile;
import Until.Status;

public class LeaveUserFromChat {

    public synchronized void leave(Session session) throws IOException{
        Profile prof = ServerEnpoint.connectionMap.get(session);
        Session temp = prof.getConnection();

        String name = prof.getName();

        prof.setConnection(null);
        ServerEnpoint.connectionMap.get(temp).setConnection(null);

        temp.getBasicRemote().sendText(name + " вышел из чата");
        session.getBasicRemote().sendText("Вы вышли из чата");

        if (prof.getStatus() == Status.AGENT)
        	ServerEnpoint.agentList.add(session);
        else
        	ServerEnpoint.agentList.add(temp);

    }

}
