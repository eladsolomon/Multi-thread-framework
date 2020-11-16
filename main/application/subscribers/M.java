package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Report;
import sun.net.www.protocol.file.FileURLConnection;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {
    private int currentTIme;
    private int id;
    private CountDownLatch countDownLatch;

    public M(int id,CountDownLatch countDownLatch) {
        super("M-" + id);
        currentTIme = 1;
        this.id = id;
        this.countDownLatch =countDownLatch;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickEndBroadcast.class, c -> terminate());
        subscribeBroadcast(TickBroadcast.class, c -> {currentTIme = c.getTime();});

        subscribeEvent(MissionReceivedEvent.class, c -> {
            Diary.getInstance().incrementTotal();
            Future<Boolean> f_agents = this.getSimplePublisher().sendEvent(new AgentsAvailbleEvent(c.getAgentNum()));
            if (f_agents != null) {
                if (f_agents.get() != null && f_agents.get() == true) {
                    if (this.currentTIme > c.getTime_expired()) {
                        this.getSimplePublisher().sendEvent(new ReleasAgentsEvent(c.getAgentNum()));
                        System.out.println("Mission aborted - after the time: " + c.getMissionName());
                    } else {
                        GadgetAvailbleEvent gadgetEvent = new GadgetAvailbleEvent(c.getGadgetname());
                        Future<Boolean> f_gadget = this.getSimplePublisher().sendEvent(gadgetEvent);
                        if ((f_gadget == null) || (f_gadget.get() == false)) {
                            System.out.println("Mission can not accomplish - Gadget " + c.getGadgetname() + " not avalibale");
                            this.getSimplePublisher().sendEvent(new ReleasAgentsEvent(c.getAgentNum()));
                        } else {
                            SendAgentsEvent sag = new SendAgentsEvent(c.getAgentNum(), c.getDuration());
                            this.getSimplePublisher().sendEvent(sag);
                            Report repo = new Report();
                            repo.setMissionName(c.getMissionName());
                            repo.setM(this.id);
                            repo.setMoneypenny(sag.getIdCharge());
                            repo.setAgentsSerialNumbersNumber(c.getAgentNum());
                            repo.setAgentsNames(sag.getList_agents_names());
                            repo.setGadgetName(c.getGadgetname());
                            repo.setTimeIssued(c.gettime_issued());
                            repo.setQTime(gadgetEvent.getTimeCharge());
                            repo.setTimeCreated(currentTIme);
                            Diary.getInstance().addReport(repo);
                        }
                    }
                }
            }
        });
        countDownLatch.countDown();
    }

}
