package main.java.Until;

import javax.websocket.Session;
import java.util.ArrayList;

public class ClientProfile extends Profile {

    private ArrayList<String> sendMessageInVoidList = new ArrayList<>();

    public ClientProfile(Session session){
        super(session);
        super.setStatus(Status.CLIENT);
    }

    public void addMessageInVoid(String message){
        sendMessageInVoidList.add(message + "\n");
    }

    public ArrayList<String> getMessageInVoid(){
        return sendMessageInVoidList;
    }

    public void clearMessageInVoid(){
        sendMessageInVoidList.clear();
    }


}
