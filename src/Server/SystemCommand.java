package Server;

import java.io.IOException;

import javax.websocket.Session;

import Until.Command;
import Until.CommandIdentifier;
import Until.Profile;

class SystemCommand {

    private static CommandIdentifier commandIdentifier = new CommandIdentifier();
    private static RegisteredNewUser commandRegister = new RegisteredNewUser();
    private static LeaveUserFromChat commandLeave = new LeaveUserFromChat();

    boolean checkCommand(Session session, String message)  {
        Command status = commandIdentifier.checkCommand(message);
        switch(status){
            case REGISTER:{
                if (!ServerEnpoint.contain(session))
                    commandRegister.registered(message,session);
                else
                	ServerEnpoint.sendText(session,"Вы зарегистрированы");
                return true;
            }
            case LEAVE:{
                Profile prof =  ServerEnpoint.getProfileFromSession(session);
                if (prof != null && prof.getConnection() != null)
                    commandLeave.leave(session);
                else
                	ServerEnpoint.sendText(session,"У вас нет собеседника");

                return true;
            }
            case EXIT:{
                Profile prof =  ServerEnpoint.getProfileFromSession(session);
                if (prof != null && prof.getConnection() != null)
                    commandLeave.leave(session);

                exitUserFromChat(session);

                return true;
            }
            case UNKNOWN:{
            	ServerEnpoint.sendText(session,"Неизвестная команда");
                return true;
            }
        }
        return false;
    }

    private synchronized void exitUserFromChat(Session session){
    	ServerEnpoint.removeAgent(session);
    	ServerEnpoint.removeClient(session);
    	ServerEnpoint.removeConnection(session);

        try {
            session.close();
        } catch (IOException e) {
            ServerEnpoint.log.error(e);
        }
    }
}
