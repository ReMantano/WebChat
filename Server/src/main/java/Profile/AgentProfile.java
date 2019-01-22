package main.java.Profile;

import main.java.Until.Status;

import javax.websocket.Session;
import java.util.Arrays;

public class AgentProfile extends Profile {

    private final Session[] arraySession ;
    private int counter = 0;

    public AgentProfile(Session session, int size){
        super(session);
        super.setStatus(Status.AGENT);
        arraySession = new Session[size];
    }

    public Session getConnection(int index){
        return arraySession[index];
    }

    public boolean addSession(Session session){
        if (counter < arraySession.length) {
            addInEmptyIndex(session);
        }

        return checkArrayFill();
    }

    public boolean checkArrayFill(){
        return counter == arraySession.length;
    }

    public void removeSession(int index){
        arraySession[index] = null;
        counter--;
    }



    public void removeSession(Session removeSession){
        for(int i = 0; i < arraySession.length; i++){
            if (arraySession[i].equals(removeSession)) {
                arraySession[i] = null;
                counter--;
                break;
            }
        }

    }


    public Session[] getArraySession(){
        return arraySession;
    }

    public int getLength(){
        return arraySession.length;
    }

    public boolean noEmptyConnection(int index){
        return arraySession[index] != null && arraySession[index] != getSelfSession();
    }

    public int findIndexBySession(Session session){
        for (int i = 0; i < arraySession.length; i++){
            if(session.equals(arraySession[i])) {
                return i;
            }
        }
        return -1;
    }

    public boolean checkActiveConnection(){
        for(int i = 0; i < arraySession.length; i++){
            if(noEmptyConnection(i))
                return true;
        }
        return false;
    }

    private void addInEmptyIndex(Session session){
        for (int i = 0; i < arraySession.length ; i++){
            if (arraySession[i] == null) {
                arraySession[i] = session;
                counter++;
                break;
            }
        }
    }

}
