package main.java.Server;

import main.java.Until.AgentProfile;
import main.java.Until.ClientProfile;
import main.java.Until.Profile;
import main.java.Until.Status;
import org.json.simple.JSONObject;

import javax.websocket.Session;

public class RegisteredNewUser {

    public synchronized void registered(JSONObject message, Session session) {
        Profile prof = new Profile(session);
        String name = (String) message.get("Name");
        String status = (String) message.get("Status");
        int size = Integer.valueOf((String) message.getOrDefault("Size","1"));

        try{
            addNewUser(prof, Status.valueOf(status.toUpperCase()), size);
        }catch (IllegalArgumentException e){
            message.put("Message","Вы указали неправильный статус");
        	ServerEnpoint.sendText(session,message.toJSONString());
            return;
        }

        ServerEnpoint.getProfileFromSession(session).setName((String) message.get("Name"));
        message.put("Message", name + " вы зарегистрированы как " + status);
        ServerEnpoint.sendText(session,message.toJSONString());
        ServerEnpoint.log.info(name + " зарегистрировался как " + status);
    }

    private void addNewUser(Profile prof, Status status, int size){
        if(status == Status.CLIENT) {
            ServerEnpoint.setNewConnection(prof.getSelfSession(),new ClientProfile(prof.getSelfSession()));
        }
        else
        {
            ServerEnpoint.addAgent(prof.getSelfSession());
            ServerEnpoint.setNewConnection(prof.getSelfSession(),new AgentProfile(prof.getSelfSession(),size));
        }
    }

}
