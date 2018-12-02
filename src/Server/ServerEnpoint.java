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

import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/web")
public class ServerEnpoint {
    public static Map<Session,Profile> connectionMap = Collections.synchronizedMap(new HashMap<Session,Profile>());
    private static SystemCommand systemCommand = new SystemCommand();
    public static List<Session> agentList = Collections.synchronizedList(new ArrayList<Session>());
    public static List<Session> clientWaitList = Collections.synchronizedList(new ArrayList<Session>());
    

    @OnOpen
    public void open(Session session ) throws IOException {
        Profile profiel = new Profile(session);
        connectionMap.put(session, profiel);
        System.out.println(session.getRequestURI());
        System.out.println(session.getPathParameters());
        session.getBasicRemote().sendText("Id: " + session.getId() + "\tСоединение установленно");
    }

    @OnMessage
    public void message(Session session, String message) throws IOException {
    	
        if (!systemCommand.checkCommand(session,message)) {
            if (connectionMap.get(session).getName() != null)
                send(session, message);
            else
            	session.getBasicRemote().sendText("Вы не зарегистрированны");
        }else if (agentList.size() > 0)
                createChatWithWaitClient();
    }
    
    @OnError
    public void eror(Session session , Throwable throwable) {
    	System.out.println(session + "\n" + throwable.getMessage());
    }
    
    private void send(Session session, String message) throws IOException{

        Profile prof = connectionMap.get(session);

        if (prof.getConnection() != null)
                prof.getConnection().getBasicRemote().sendText(prof.getName() + ": " + message);
        else
            if (prof.getStatus() == Status.AGENT)
            	session.getBasicRemote().sendText("Дождитесь клиента");
        else
            if (!connectionClientToAgent(prof,message)) {
            	session.getBasicRemote().sendText("Нет свободных агентов");
                prof.addMessageInVoid(message);
                if (!clientWaitList.contains(session))
                    clientWaitList.add(session);
            }

    }

    private boolean connectionClientToAgent(Profile prof, String message) throws IOException{
        if (agentList.size() > 0) {
            Session agent = agentList.remove(0);
            prof.setConnection(agent);

            connectionMap.get(agent).setConnection(prof.getSelfWriter());
            agent.getBasicRemote().sendText(prof.getName() + ": " + message);

            return true;
        }
        else
            return false;

    }

    private void createChatWithWaitClient() throws IOException{
        if (clientWaitList.size() > 0) {
            Session client = clientWaitList.remove(0);
            Session agent = agentList.remove(0);
            Profile agentProfile = connectionMap.get(agent);

            agentProfile.setConnection(client);
            Profile clientProfile = connectionMap.get(client);
            clientProfile.setConnection(agent);

            client.getBasicRemote().sendText("Аген " + agentProfile.getName() + "готов r беседе");
            agent.getBasicRemote().sendText(clientProfile.getMessageInVoid() +
                    "\nВы переписываетесь с клиентом " +
                    clientProfile.getName());

            clientProfile.clearMessageInVoid();
        }
    }


}