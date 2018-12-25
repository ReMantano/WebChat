package main.java.Server;

import java.io.IOException;

import javax.websocket.Session;

import main.java.Until.AgentProfile;
import main.java.Until.Command;
import main.java.Until.Profile;
import main.java.Until.Status;
import org.json.simple.JSONObject;

public class SystemCommand {

    private static RegisteredNewUser commandRegister = new RegisteredNewUser();
    private static LeaveUserFromChat commandLeave = new LeaveUserFromChat();

    public boolean checkCommand(Session session, JSONObject message){
        Command status = findCommand(((String) message.get("Command")).toUpperCase());

        switch(status){
            case REGISTER:{
                if (!ServerEnpoint.contain(session))
                    commandRegister.registered(message, session);
                else {
                    message.put("Message", "Вы зарегистрированы");
                    ServerEnpoint.sendText(session, message.toJSONString());
                }
                return true;
            }
            case LEAVE:{
                Profile prof =  ServerEnpoint.getProfileFromSession(session);
                if (prof != null) {
                    if(prof.getConnection() != null)
                        commandLeave.leave(prof, -1);
                    else {
                        int index = Integer.valueOf((String) message.get("Index"));
                        if(prof.getStatus() == Status.AGENT &&((AgentProfile) prof).noEmptyConnection(index))
                            commandLeave.leave(prof, index);
                        else {
                            message.put("Message","У вас нет собеседника");
                            ServerEnpoint.sendText(session, message.toJSONString());
                        }
                    }
                }

                return true;
            }
            case EXIT:{
                Profile prof =  ServerEnpoint.getProfileFromSession(session);
                if (prof != null) {
                    if(prof.getConnection() != null)
                            commandLeave.leave(prof, -1);
                    else {
                        if(prof.getStatus() == Status.AGENT && ((AgentProfile) prof).checkActiveConnection())
                            commandLeave.leave(prof, -1);
                    }
                }
                exitUserFromChat(session);

                return true;
            }
            case DELETE:{
                Profile prof =  ServerEnpoint.getProfileFromSession(session);
                if (prof != null) {
                    if(prof.getStatus() == Status.AGENT){
                        commandLeave.leave(prof,Integer.valueOf((String) message.get("Index")));
                        ((AgentProfile) prof).addSession(prof.getSelfSession());
                        if(((AgentProfile) prof).checkArrayFill())
                            ServerEnpoint.removeAgent(session);
                    }else
                        return true;

                }

            }
            case UNKNOWN:{
                message.put("Message","Неизвестная команда");
            	ServerEnpoint.sendText(session,message.toJSONString());
                return true;
            }
            case TEXT: {
                return false;
            }
        }
        return false;
    }

    private Command findCommand(String command){
        try {
            return Command.valueOf(command);
        }catch (IllegalArgumentException e){
            return Command.UNKNOWN;
        }

    }

    private synchronized void exitUserFromChat(Session session){
    	ServerEnpoint.removeAgent(session);
    	ServerEnpoint.removeClient(session);
    	ServerEnpoint.removeConnection(session);

    	if(!session.isOpen())
    	    return;
        try {
            session.close();
        } catch (IOException e) {
            ServerEnpoint.log.error(e);
        }
    }
}
