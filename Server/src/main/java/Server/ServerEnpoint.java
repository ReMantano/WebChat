package main.java.Server;
import java.io.IOException;
import java.util.*;

import main.java.Until.AgentProfile;
import main.java.Until.ClientProfile;
import main.java.Until.Profile;
import main.java.Until.Status;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        JSONObject o = new JSONObject();
        o.put("Message","Id: " + session.getId() + "\tСоединение установленно");
        sendText(session,o.toJSONString());
        log.info("Connection: " + session.toString());
    }

    @OnMessage
    public void message(Session session, String message)  {
        JSONObject jMessage = getJSONFromString(message);

        if (!systemCommand.checkCommand(session,jMessage)) {
            if (connectionMap.containsKey(session))
                send(session, jMessage);
            else {
                jMessage.put("Message","Вы не зарегистрированны" );
                sendText(session, jMessage.toJSONString());
            }
        }else{
            createChatWithWaitClient(session);
        }

    }
    
    @OnError
    public void error(Session session , Throwable throwable) {
    	log.error(session.toString(),throwable);
    }

    @OnClose
    public void closeConnection(Session session){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Command", "EXIT");
            if(connectionMap.containsKey(session))
                systemCommand.checkCommand(session, jsonObject);
            log.info("Disconnect:" + session.toString());
        }catch(IllegalStateException e){

        }
    }
    
    private void send(Session session, JSONObject message) {

        Profile prof = connectionMap.get(session);

        if (prof.getStatus() == Status.AGENT){
            AgentProfile agent = (AgentProfile) prof;
            String temp = (String) message.get("Index");
            int index = Integer.valueOf(temp);

            if(agent.noEmptyConnection(index)){
                sendText(agent.getConnection(index),message.toJSONString());
            }else{
                message.put("Message", "Дождитесь клиента");
                sendText(session,message.toJSONString());
            }
        }else{
            if (prof.getConnection() != null) {
                AgentProfile agent = (AgentProfile)connectionMap.get(prof.getConnection());
                int index = agent.findIndexBySession(session);
                message.put("Index",index);
                sendText(prof.getConnection(), message.toJSONString());
            }
            else{
                if (!connectionClientToAgent(prof,message)) {
                    String voidMessage = (String) message.get("Message");
                    message.put("Message", "Нет свободных агентов");
                    sendText(session,message.toJSONString());
                    ((ClientProfile) prof).addMessageInVoid(voidMessage);
                    if (!clientWaitList.contains(session))
                        clientWaitList.add(session);
                }
            }
        }

    }

    private boolean connectionClientToAgent(Profile prof, JSONObject message){
        try {

            Session agent = agentList.get(0);
            prof.setConnection(agent);
            AgentProfile agentProf = (AgentProfile) connectionMap.get(agent);
            if(agentProf.addSession(prof.getSelfSession())){
                agentList.remove(agent);
            }

            message.put("Index",agentProf.findIndexBySession(prof.getSelfSession()));
            sendText(agent,message.toJSONString());
            return true;
        }
        catch (IndexOutOfBoundsException e){
            return false;
        }

    }

    private void createChatWithWaitClient(Session session) {

        try {
            Session agentSession =  agentList.get(0);
            createChatOnAllWindows(agentSession,(AgentProfile) connectionMap.get(agentSession));

        }catch (IndexOutOfBoundsException e){
            return;
        }

    }

    private void createChatOnAllWindows(Session session, AgentProfile agent){
            int length = agent.getLength();

            for(int i = 0; i < length; i++){
                try{
                    Session client = clientWaitList.remove(0);
                    ClientProfile clientProfile = (ClientProfile) connectionMap.get(client);
                    ArrayList<String> list = clientProfile.getMessageInVoid();
                    list.add("Вы переписываетесь с клиентом " + clientProfile.getName());
                    clientProfile.setConnection(session);
                    agent.addSession(client);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Name", agent.getName());
                    jsonObject.put("Message", "Аген " + agent.getName() + "готов к беседе\n");

                    sendText(client,jsonObject.toJSONString());

                    jsonObject = new JSONObject();
                    jsonObject.put("Name", clientProfile.getName());
                    jsonObject.put("Message", list);
                    jsonObject.put("Index",agent.findIndexBySession(client));

                    sendText(session,jsonObject.toJSONString());

                    if(agent.checkArrayFill())
                        {
                            agentList.remove(session);
                            break;
                        }

                    clientProfile.clearMessageInVoid();
                }catch (IndexOutOfBoundsException e){
                    break;
                }
            }

    }

    static void sendText(Session session, String text){
        if (!session.isOpen()) {
            return;
        }
        try{
            session.getBasicRemote().sendText(text);
        }catch (IOException e){
            log.error(e);
        }
    }

    private int getChatIndex(String command){
        int start = command.indexOf('%');
        String sub = command.substring(0,start);

        return Integer.valueOf(sub);
    }

    private JSONObject getJSONFromString(String jString){
        try {
            Object o = new JSONParser().parse(jString);
            return (JSONObject) o;
        } catch (ParseException e) {
            log.error(e);
            return null;
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

    public synchronized static boolean containAgent(Session session){
        return agentList.contains(session);
    }
    public synchronized static void addClient(Session session){
        clientWaitList.add(session);
    }

    public synchronized static void removeClient(Session session){
        clientWaitList.remove(session);
    }
}