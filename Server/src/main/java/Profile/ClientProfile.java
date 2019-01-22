package main.java.Profile;

import main.java.Until.Status;

import javax.websocket.Session;
import java.util.ArrayList;

public class ClientProfile extends Profile {

    private ArrayList<String> notSentMessagesList = new ArrayList<>();

    public ClientProfile(Session session){
        super(session);
        super.setStatus(Status.CLIENT);
    }

    public void addMessageInVoid(String message){
        notSentMessagesList.add(message + "\n");
    }

    public ArrayList<String> getNotSentMessagesList(){
        return notSentMessagesList;
    }

    public void clearNotSentMessagesList(){
        notSentMessagesList.clear();
    }


}
