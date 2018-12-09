package Server;

import javax.websocket.Session;

import Until.Profile;
import Until.Status;

class LeaveUserFromChat {

    synchronized void leave(Session session)  {
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
