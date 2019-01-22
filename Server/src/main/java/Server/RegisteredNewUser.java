package main.java.Server;

import main.java.Profile.Profile;
import main.java.Until.Status;
import main.java.Until.Message;
import main.java.Profile.AgentProfile;
import main.java.Profile.ClientProfile;

import javax.websocket.Session;

public class RegisteredNewUser {

    public synchronized void registered(Message message, Session session) {
        Profile prof = new Profile(session);
        String name = message.getName();
        Status status = message.getProfile();
        int size = message.getSize();

        try{
            addNewUser(prof, status, size);
        }catch (IllegalArgumentException e){
            message.setName("Сервер");
            message.setText("Вы указали неправильный статус");
        	ServerEnpoint.sendText(session,message.toJsonString());
            return;
        }

        ServerEnpoint.getProfileFromSession(session).setName(name);
        message.setName("Сервер");
        message.setText(name + " вы зарегистрированы как " + status);
        ServerEnpoint.sendText(session,message.toJsonString());
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
