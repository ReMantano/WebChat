package Server;

import java.io.IOException;

import javax.websocket.Session;

import Until.Command;
import Until.CommandIdentifier;
import Until.Profile;

public class SystemCommand {

    private static CommandIdentifier commandIdentifier = new CommandIdentifier();
    private static RegisteredNewUser commandRegister = new RegisteredNewUser();
    private static LeaveUserFromChat commandLeave = new LeaveUserFromChat();

    public boolean checkCommand(Session session, String message) throws IOException{
        Command status = commandIdentifier.checkCommand(message);
        switch(status){
            case REGISTER:{
                if (ServerEnpoint.connectionMap.get(session).getName() == null)
                    commandRegister.registered(message,session);
                else
                	session.getBasicRemote().sendText("�� �����������������");
                return true;
            }
            case LEAVE:{
                Profile prof =  ServerEnpoint.connectionMap.get(session);
                if (prof != null && prof.getConnection() != null)
                    commandLeave.leave(session);
                else
                	session.getBasicRemote().sendText("� ��� ��� �����������");

                return true;
            }
            case EXIT:{
                Profile prof =  ServerEnpoint.connectionMap.get(session);
                if (prof != null && prof.getConnection() != null)
                    commandLeave.leave(session);

                exitUserFromCaht(session);

                return true;
            }
            case UNKNOWN:{
            	session.getBasicRemote().sendText("����������� �������");
                return true;
            }
        }
        return false;
    }

    private synchronized void exitUserFromCaht(Session session){
    	ServerEnpoint.agentList.remove(session);
    	ServerEnpoint.clientWaitList.remove(session);
    	ServerEnpoint.connectionMap.remove(session);
        //mWriter.close();
    }
}