package main.java.Server;

import javax.websocket.Session;

import main.java.Until.AgentProfile;
import main.java.Until.Profile;
import main.java.Until.Status;
import org.json.simple.JSONObject;

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

        JSONObject jo = new JSONObject();

        Session[] arraySession = agent.getArraySession();

        for(Session temp : arraySession){
            if(temp != null){
                ServerEnpoint.getProfileFromSession(temp).setConnection(null);
                jo.put("Message",agent.getName() + " вышел из чата");
                ServerEnpoint.sendText(temp,jo.toJSONString());
            }

        }
    }

    private synchronized void leaveClient(Profile profile){
        Session temp = profile.getConnection();
        profile.setConnection(null);
        AgentProfile agent = (AgentProfile) ServerEnpoint.getProfileFromSession(temp);
        agent.removeSession(profile.getSelfSession());

        if(!ServerEnpoint.containAgent(agent.getSelfSession()))
            ServerEnpoint.addAgent(agent.getSelfSession());

        sendLeaveMessage(profile,temp);
    }

    private void sendLeaveMessage(Profile p, Session t){
        sendLeaveMessage(p,t,0);
    }

    private void sendLeaveMessage(Profile profile, Session temp, int index){
        String name = profile.getName();
        JSONObject jo1 = new JSONObject();
        JSONObject jo2 = new JSONObject();
        jo1.put("Message",name + " вышел из чата");
        jo2.put("Message","Вы вышли из чата");

        if(profile.getStatus() == Status.AGENT){
            jo2.put("Index",index);
        }else{
            jo1.put("Index",index) ;
        }

        ServerEnpoint.sendText(temp,jo1.toJSONString());
        ServerEnpoint.sendText(profile.getSelfSession(),jo2.toJSONString());
    }




}
