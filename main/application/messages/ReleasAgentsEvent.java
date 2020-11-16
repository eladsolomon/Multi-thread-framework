package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class ReleasAgentsEvent implements Event {
    List<String> list_Agents;
    public ReleasAgentsEvent(List<String> list)
    {
        this.list_Agents=list;
    }
    public List<String> getList_Agents()
    {
        return this.list_Agents;
    }

}
