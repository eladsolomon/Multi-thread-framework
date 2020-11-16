package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class SendAgentsEvent implements Event {
    private List<String> list_Agents_serial;
    private int duration ;
    private List<String> list_agents_names;
    private int IdCharge;

    public SendAgentsEvent(List<String> listSerials, int duration)
    {
        this.list_Agents_serial=listSerials;
        this.duration=duration;
    }

    public List<String> getList_Agents()
    {
        return this.list_Agents_serial;
    }

    public int getDuration() {
        return this.duration;
    }

    public List<String> getList_agents_names() {
        return list_agents_names;
    }

    public void setList_agents_names(List<String> list_agents_names) {
        this.list_agents_names = list_agents_names;
    }

    public void setIdCharge(int idCharge) {
        IdCharge = idCharge;
    }

    public int getIdCharge() {
        return IdCharge;
    }
}
