package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

import java.util.concurrent.atomic.AtomicInteger;

public class TickBroadcast implements Broadcast {
    private int time;

    public TickBroadcast(int time)
    {
        this.time=time;
    }
    public int getTime()
    {
        return this.time;
    }
}
