package Server;

import Until.Command;
import Until.Profile;
import Until.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.websocket.Session;

public class RegisteredNewUser {

    public synchronized void registered(String message, Session session) throws IOException{
        int length = Command.REGISTER.name().length();
        String temp = message.substring(2 + length);
        ArrayList<String> commandList = new ArrayList<String>(Arrays.asList(temp.split(" ")));
        String status = commandList.remove(0).toUpperCase();
        String name = arrayListToString(commandList);

        Profile prof = ServerEnpoint.connectionMap.get(session);
        

        try{
            addNewUser(prof, Status.valueOf(status));
        }catch (IllegalArgumentException e){
        	session.getBasicRemote().sendText("Вы указали неправильный статус");
            return;
        }
        //ServerEnpoint.connectionMap.put(session,prof);
        prof.setName(name);
        session.getBasicRemote().sendText(name + "вы зарегистрированы как " + prof.getStatus().toString().toLowerCase());
        //Server.log.info(name + "зарегистрировался как " + prof.getStatus().toString());
    }

    private void addNewUser(Profile prof, Status status){
        if(status == Status.CLIENT)
            prof.setStatus(Status.CLIENT);
        else
        {
            prof.setStatus(Status.AGENT);
            ServerEnpoint.agentList.add(prof.getSelfWriter());
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
