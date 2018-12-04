package Server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import Until.Profile;
import Until.Status;
import org.apache.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/web")
public class ServerEnpoint {
    static Map<Session,Profile> connectionMap = Collections.synchronizedMap(new HashMap<Session,Profile>());
    private static SystemCommand systemCommand = new SystemCommand();
    static List<Session> agentList = Collections.synchronizedList(new ArrayList<Session>());
    static List<Session> clientWaitList = Collections.synchronizedList(new ArrayList<Session>());
    static Logger log = Logger.getLogger(ServerEnpoint.class);

    

    @OnOpen
    public void open(Session session )  {
        sendText(session,"Id: " + session.getId() + "\tСоединение установленно");
        log.info("Connection: " + session.toString());
    }

    @OnMessage
    public void message(Session session, String message)  {
    	
        if (!systemCommand.checkCommand(session,message)) {
            if (connectionMap.containsKey(session))
                send(session, message);
            else
            	sendText(session,"Вы не зарегистрированны");
        }else if (agentList.size() > 0)
                createChatWithWaitClient();
    }
    
    @OnError
    public void eror(Session session , Throwable throwable) {
    	log.error(session.toString(),throwable);
    }

    @OnClose
    public void closeConnection(Session session){
        systemCommand.checkCommand(session,"\\exit");
        log.info("Disconnect:" + session.toString() );
    }
    
    private void send(Session session, String message) {

        Profile prof = connectionMap.get(session);

        if (prof.getConnection() != null)
                sendText(prof.getConnection(),prof.getName() + ": " + message);
        else
            if (prof.getStatus() == Status.AGENT)
            	sendText(session,"Дождитесь клиента");
        else
            if (!connectionClientToAgent(prof,message)) {
            	sendText(session,"Нет свободных агентов");
                prof.addMessageInVoid(message);
                if (!clientWaitList.contains(session))
                    clientWaitList.add(session);
            }

    }

    private boolean connectionClientToAgent(Profile prof, String message){
        if (agentList.size() > 0) {
            Session agent = agentList.remove(0);
            prof.setConnection(agent);

            connectionMap.get(agent).setConnection(prof.getSelfWriter());
            sendText(agent,prof.getName() + ": " + message);

            return true;
        }
        else
            return false;

    }

    private void createChatWithWaitClient() {
        if (clientWaitList.size() > 0) {
            Session client = clientWaitList.remove(0);
            Session agent = agentList.remove(0);
            Profile agentProfile = connectionMap.get(agent);

            agentProfile.setConnection(client);
            Profile clientProfile = connectionMap.get(client);
            clientProfile.setConnection(agent);


            sendText(client,"Аген " + agentProfile.getName() + "готов r беседе");
            sendText(agent,clientProfile.getMessageInVoid() +
                                "\nВы переписываетесь с клиентом " +
                                clientProfile.getName());

            clientProfile.clearMessageInVoid();
        }
    }

    static void sendText(Session session, String text){
        try{
            session.getBasicRemote().sendText(text);
        }catch (IOException e){
            log.error(e);
        }
    }


}