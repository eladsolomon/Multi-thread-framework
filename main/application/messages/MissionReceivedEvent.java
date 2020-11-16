package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class MissionReceivedEvent implements Event {
    private String missionName;
    private List<String> AgentNum;
    private String gadgetname;
    private int time_expired;
    private int time_issued;
    private int duration_time;
    public MissionReceivedEvent(String m, List<String> a, String g,int time_expired,int time_issued,int duration_time){
        this.time_expired=time_expired;
        this.gadgetname = g;
        this.missionName=m;
        this.AgentNum = a;
        this.time_issued=time_issued;
        this.duration_time=duration_time;
    }
    public String getMissionName(){
        return this.missionName;
    }
    public String getGadgetname(){
        return this.gadgetname;
    }
    public List<String> getAgentNum(){
        return this.AgentNum;
    }
    public int getTime_expired()
    {
        return time_expired;
    }
    public int gettime_issued()
    {
        return this.time_issued;
    }
    public int getDuration()
    {
        return this.duration_time;
    }
}
