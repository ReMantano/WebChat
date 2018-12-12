package main.java.Server;

import javax.websocket.Session;
import main.java.Until.Profile;
import main.java.Until.Status;

public class LeaveUserFromChat {

    public synchronized void leave(Session session)  {
        Profile prof = ServerEnpoint.getProfileFromSession(session);
        Session temp = prof.getConnection();

        String name = prof.getName();

        prof.setConnection(null);
        ServerEnpoint.getProfileFromSession(temp).setConnection(null);

        ServerEnpoint.sendText(temp,name + " вышел из чата");
        ServerEnpoint.sendText(session,"Вы вышли из чата");

        if (prof.getStatus() == Status.AGENT)
        	ServerEnpoint.addAgent(session);
        else
        	ServerEnpoint.addAgent(temp);

    }

}
