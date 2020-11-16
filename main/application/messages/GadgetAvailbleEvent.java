package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class GadgetAvailbleEvent implements Event {
        private String gadget;
        private int TimeCharge;

        public GadgetAvailbleEvent(String gadget){
            this.gadget = gadget;
        }
        public String getGadget(){
            return this.gadget;
        }

    public void setTimeCharge(int timeCharge) {
        TimeCharge = timeCharge;
    }

    public int getTimeCharge() {
        return TimeCharge;
    }
}
