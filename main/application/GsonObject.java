package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

public class GsonObject {
    String[] inventory;
    Service services;
    Agent[]squad;

    public class Service{
        int M;
        int Moneypenny;
        GsonIntelligence[] intelligence;
        int time;

        public class GsonIntelligence{
            MissionInfo[] missions;
        }
    }




}
