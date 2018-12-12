package main.java.Server;

import main.java.Until.Command;
import main.java.Until.Profile;
import main.java.Until.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.websocket.Session;

public class RegisteredNewUser {

    public synchronized void registered(String message, Session session) {
        int length = Command.REGISTER.name().length();
        String temp = message.substring(2 + length);
        ArrayList<String> commandList = new ArrayList<String>(Arrays.asList(temp.split(" ")));
        String status = commandList.remove(0).toUpperCase();
        String name = arrayListToString(commandList);

        Profile prof = new Profile(session);
        prof.setName(name);
        

        try{
            addNewUser(prof, Status.valueOf(status));
        }catch (IllegalArgumentException e){
        	ServerEnpoint.sendText(session,"Вы указали неправильный статус");
            return;
        }

        ServerEnpoint.setNewConnection(session,prof);
        ServerEnpoint.sendText(session,name + "вы зарегистрированы как " + prof.getStatus().toString().toLowerCase());
        ServerEnpoint.log.info(name + "зарегистрировался как " + prof.getStatus().toString());
    }

    private void addNewUser(Profile prof, Status status){
        if(status == Status.CLIENT)
            prof.setStatus(Status.CLIENT);
        else
        {
            prof.setStatus(Status.AGENT);
            ServerEnpoint.addAgent(prof.getSelfWriter());
        }
    }

    private String arrayListToString(List<String> list){
        StringBuilder st = new StringBuilder();
        for(String s : list){
            st.append(s + " ");
        }
        return st.toString();
    }
}
