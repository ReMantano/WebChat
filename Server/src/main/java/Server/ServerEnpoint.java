package main.java.Server;
import java.io.IOException;
import java.util.*;

import main.java.Until.Profile;
import main.java.Until.Status;
import org.apache.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/web")
public class ServerEnpoint {
    private static Map<Session, Profile> connectionMap = Collections.synchronizedMap(new HashMap<Session,Profile>());
    private static SystemCommand systemCommand = new SystemCommand();
    private static List<Session> agentList = Collections.synchronizedList(new ArrayList<Session>());
    private static List<Session> clientWaitList = Collections.synchronizedList(new ArrayList<Session>());
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
        }else
            createChatWithWaitClient();
    }
    
    @OnError
    public void error(Session session , Throwable throwable) {
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
        try {
            Session agent = agentList.remove(0);

            prof.setConnection(agent);

            connectionMap.get(agent).setConnection(prof.getSelfWriter());
            sendText(agent,prof.getName() + ": " + message);

            return true;
        }
        catch (IndexOutOfBoundsException e){
            return false;
        }

    }

    private boolean createChatWithWaitClient() {
        try{
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

            return true;
        }catch (IndexOutOfBoundsException e){
            return false;
        }

    }

    static void sendText(Session session, String text){
        if (!session.isOpen())
            return;

        try{
            session.getBasicRemote().sendText(text);
        }catch (IOException e){
            log.error(e);
        }
    }

    public synchronized static Profile getProfileFromSession(Session session){
        return connectionMap.get(session);
    }

    public synchronized static void setNewConnection(Session session, Profile profile){
        connectionMap.put(session,profile);
    }

    public synchronized static boolean contain(Session session){
        return connectionMap.containsKey(session);
    }

    public synchronized static void removeConnection(Session session){
        connectionMap.remove(session);
    }

    public synchronized static void addAgent(Session session){
        agentList.add(session);
    }

    public synchronized static void removeAgent(Session session){
        agentList.remove(session);
    }

    public synchronized static void addClient(Session session){
        clientWaitList.add(session);
    }

    public synchronized static void removeClient(Session session){
        clientWaitList.remove(session);
    }
}