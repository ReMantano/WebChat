package main.java.Server;
import java.io.IOException;
import java.util.*;

import com.google.gson.GsonBuilder;
import main.java.Profile.AgentProfile;
import main.java.Profile.ClientProfile;
import main.java.Profile.Profile;
import main.java.Until.*;
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
        Message message = new Message();
        message.setName("Сервер");
        message.setText("Id: " + session.getId() + "\tСоединение установленно");
        sendText(session,message.toJsonString());
        log.info("Connection: " + session.toString());
    }

    @OnMessage
    public void message(Session session, String message)  {
        Message gMessage = new GsonBuilder().create().fromJson(message, Message.class);

        if (!systemCommand.checkCommand(session,gMessage)) {
            if (connectionMap.containsKey(session))
                send(session, gMessage);
            else {
                gMessage.setText("Вы не зарегистрированны" );
                sendText(session, gMessage.toJsonString());
            }
        }else{
            createChatWithWaitClient();
        }

    }
    
    @OnError
    public void error(Session session , Throwable throwable) {
    	log.error(session.toString(),throwable);
    }

    @OnClose
    public void closeConnection(Session session){
        if(connectionMap.containsKey(session)) {
            Message message = new Message();
            message.setCommand(Command.EXIT);
            message.setText("Close");

            systemCommand.checkCommand(session, message);

            agentList.remove(session);
            clientWaitList.remove(session);
            connectionMap.remove(session);
        }
        log.info("Disconnect:" + session.toString());

        createChatWithWaitClient();
    }
    
    private void send(Session session, Message message) {

        Profile prof = connectionMap.get(session);
        if (prof.getStatus() == Status.AGENT){
            AgentProfile agent = (AgentProfile) prof;
            int index = message.getIndex();

            if(agent.noEmptyConnection(index)){
                sendText(agent.getConnection(index),message.toJsonString());
            }else{
                message.setText("Дождитесь клиента");
                sendText(session,message.toJsonString());
            }
        }else{
            if (prof.getConnection() != null) {
                AgentProfile agent = (AgentProfile)connectionMap.get(prof.getConnection());
                int index = agent.findIndexBySession(session);
                message.setIndex(index);
                sendText(prof.getConnection(), message.toJsonString());
            }
            else{
                if (!connectionClientToAgent(prof,message)) {
                    String voidMessage =  message.getText();
                    message.setText("Нет свободных агентов");
                    sendText(session,message.toJsonString());
                    ((ClientProfile) prof).addMessageInVoid(voidMessage);
                    if (!clientWaitList.contains(session))
                        clientWaitList.add(session);
                }
            }
        }

    }

    private synchronized boolean connectionClientToAgent(Profile prof, Message message){
        if(agentList.size() > 0){
            Session agent = agentList.get(0);
            prof.setConnection(agent);
            AgentProfile agentProf = (AgentProfile) connectionMap.get(agent);

            agentList.remove(agent);
            if(!agentProf.addSession(prof.getSelfSession())){
                agentList.add(agent);
            }

            message.setIndex(agentProf.findIndexBySession(prof.getSelfSession()));
            sendText(agent,message.toJsonString());
            return true;
        }else
            return false;


    }

    private synchronized void createChatWithWaitClient() {
        if(agentList.size() > 0){
            Session agentSession =  agentList.get(0);
            createChatOnAllWindows(agentSession,(AgentProfile) connectionMap.get(agentSession));

        }
    }

    private synchronized void createChatOnAllWindows(Session session, AgentProfile agent){
            int length = agent.getLength();

            for(int i = 0; i < length; i++){
                if(clientWaitList.size() > 0) {
                    Session client = clientWaitList.remove(0);
                    ClientProfile clientProfile = (ClientProfile) connectionMap.get(client);
                    ArrayList<String> list = clientProfile.getNotSentMessagesList();
                    list.add("Вы переписываетесь с клиентом " + clientProfile.getName());
                    clientProfile.setConnection(session);
                    agent.addSession(client);

                    Message message = new Message();
                    message.setName(agent.getName());
                    message.setText("Аген " + agent.getName() + "готов к беседе\n");
                    message.setCommand(Command.TEXT);

                    sendText(client, message.toJsonString());

                    message.setName(clientProfile.getName());
                    message.setIndex(agent.findIndexBySession(client));
                    message.setText("");
                    for (String text : list){
                        String temp = message.getText() + text;
                        message.setText(temp);
                    }

                    sendText(session, message.toJsonString());

                    clientProfile.clearNotSentMessagesList();

                    agentList.remove(agent);
                    if (!agent.checkArrayFill()) {
                        agentList.add(session);
                        break;
                    }

                }else
                    break;

            }

    }

    static void sendText(Session session, String text){
        try{
            if (session.isOpen())
                session.getBasicRemote().sendText(text);
        }catch (IOException | IllegalStateException e){
            agentList.remove(session);
            clientWaitList.remove(session);
            connectionMap.remove(session);
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