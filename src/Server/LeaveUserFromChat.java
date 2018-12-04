package Server;

import java.io.IOException;

import javax.websocket.Session;

import Until.Profile;
import Until.Status;

public class LeaveUserFromChat {

    public synchronized void leave(Session session)  {
        Profile prof = ServerEnpoint.connectionMap.get(session);
        Session temp = prof.getConnection();

        String name = prof.getName();

        prof.setConnection(null);
        ServerEnpoint.connectionMap.get(temp).setConnection(null);

        ServerEnpoint.sendText(temp,name + " вышел из чата");
        ServerEnpoint.sendText(session,"Вы вышли из чата");

        if (prof.getStatus() == Status.AGENT)
        	ServerEnpoint.agentList.add(session);
        else
        	ServerEnpoint.agentList.add(temp);

    }

}
