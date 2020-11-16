package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Agent;


import java.util.List;

public class AgentsAvailbleEvent implements Event {
    List<String> agentsSerial;
    public AgentsAvailbleEvent(List<String> agentsSerial)
    {
        this.agentsSerial=agentsSerial;
    }
    public List<String> getAgents()
    {
        return this.agentsSerial;
    }


}
