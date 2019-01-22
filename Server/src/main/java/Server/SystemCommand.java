package main.java.Server;

import java.io.IOException;

import javax.websocket.Session;

import main.java.Profile.AgentProfile;
import main.java.Profile.Profile;
import main.java.Until.*;

public class SystemCommand {

    private static RegisteredNewUser commandRegister = new RegisteredNewUser();
    private static LeaveUserFromChat commandLeave = new LeaveUserFromChat();

    public boolean checkCommand(Session session, Message message){
        Command status = message.getCommand();

        switch(status){
            case REGISTER:{
                if (!ServerEnpoint.contain(session))
                    commandRegister.registered(message, session);
                else {
                    message.setText("Вы зарегистрированы");
                    message.setName("Сервер");
                    ServerEnpoint.sendText(session, message.toJsonString());
                }
                return true;
            }
            case LEAVE:{
                Profile prof =  ServerEnpoint.getProfileFromSession(session);
                if (prof != null) {
                    if(prof.getConnection() != null)
                        commandLeave.leave(prof, -1);
                    else {
                        int index = message.getIndex();
                        if(prof.getStatus() == Status.AGENT &&((AgentProfile) prof).noEmptyConnection(index))
                            commandLeave.leave(prof, index);
                        else {
                            message.setName("Сервер");
                            message.setText("У вас нет собеседника");
                            ServerEnpoint.sendText(session, message.toJsonString());
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
                if (!message.getText().equals("Close"))
                    exitUserFromChat(session);

                return true;
            }
            case DELETE:{
                Profile prof =  ServerEnpoint.getProfileFromSession(session);
                if (prof != null) {
                    if(prof.getStatus() == Status.AGENT){
                        commandLeave.leave(prof,message.getIndex());
                        ((AgentProfile) prof).addSession(prof.getSelfSession());
                        if(((AgentProfile) prof).checkArrayFill())
                            ServerEnpoint.removeAgent(session);
                    }else
                        return true;

                }

            }
            case UNKNOWN:{
                message.setName("Сервер");
                message.setText("Неизвестная команда");
            	ServerEnpoint.sendText(session,message.toJsonString());
                return true;
            }
            case TEXT: {
                return false;
            }
        }
        return false;
    }


    private synchronized void exitUserFromChat(Session session){
    	ServerEnpoint.removeAgent(session);
    	ServerEnpoint.removeClient(session);
    	ServerEnpoint.removeConnection(session);

        try {
            if (session.isOpen())
                session.close();

        } catch (IOException | IllegalStateException e) {
            ServerEnpoint.log.error(e);
        }
    }
}
