package main.java.Server;

import javax.websocket.Session;

import main.java.Profile.AgentProfile;
import main.java.Until.Message;
import main.java.Profile.Profile;
import main.java.Until.Status;

public class LeaveUserFromChat {



    public void leave(Profile profile, int index){
        if(profile.getStatus() == Status.CLIENT){
            leaveClient(profile);
        }else{
            if(index != -1)
                leaveAgent((AgentProfile) profile,index);
            else
                leaveAgent((AgentProfile) profile);
        }
    }

    private synchronized void leaveAgent(AgentProfile agent, int index){
        Session temp = agent.getConnection(index);
        ServerEnpoint.getProfileFromSession(temp).setConnection(null);
        agent.removeSession(index);

        if(!ServerEnpoint.containAgent(agent.getSelfSession()))
            ServerEnpoint.addAgent(agent.getSelfSession());

        sendLeaveMessage(agent,temp,index);
    }

    private synchronized void leaveAgent(AgentProfile agent){

        Message message = new Message();

        Session[] arraySession = agent.getArraySession();

        for(Session temp : arraySession){
            if(temp != null){
                ServerEnpoint.getProfileFromSession(temp).setConnection(null);
                message.setName("Сервер");
                message.setText(agent.getName() + " вышел из чата");
                ServerEnpoint.sendText(temp,message.toJsonString());
            }

        }
    }

    private synchronized void leaveClient(Profile profile){
        Session temp = profile.getConnection();
        profile.setConnection(null);
        AgentProfile agent = (AgentProfile) ServerEnpoint.getProfileFromSession(temp);
        int index = agent.findIndexBySession(profile.getSelfSession());
        agent.removeSession(profile.getSelfSession());

        if(!ServerEnpoint.containAgent(agent.getSelfSession()))
            ServerEnpoint.addAgent(agent.getSelfSession());

        sendLeaveMessage(profile,temp, index);
    }

    private void sendLeaveMessage(Profile profile, Session temp, int index){
        String name = profile.getName();
        Message message1 = new Message();
        Message message2 = new Message();
        message1.setName("Сервер");
        message1.setText(name + " вышел из чата");
        message2.setName("Сервер");
        message2.setText("Вы вышли из чата");

        if(profile.getStatus() == Status.AGENT){
            message2.setIndex(index);
        }else{
            message1.setIndex(index);
        }

        ServerEnpoint.sendText(temp,message1.toJsonString());
        ServerEnpoint.sendText(profile.getSelfSession(),message2.toJsonString());
    }




}
