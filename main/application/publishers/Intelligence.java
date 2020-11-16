package bgu.spl.mics.application.publishers;

import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Publisher;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TickEndBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Publisher only.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
		private List<MissionInfo> list_Info = new LinkedList<>();
		AtomicInteger time ;
		int id;
		private CountDownLatch countDownLatch;

	public Intelligence(int id, MissionInfo[] missions,CountDownLatch countDownLatch) {
		super("Intelligence-"+id);
		load(missions);
		this.id = id;
		this.countDownLatch=countDownLatch;
	}

	@Override
	protected void initialize() {
		this.time = new AtomicInteger(1);
		subscribeBroadcast(TickEndBroadcast.class, c -> terminate());
		subscribeBroadcast(TickBroadcast.class,c->{System.out.println(" intel got TICK -"+c.getTime());
													for(MissionInfo m : list_Info)
													if(m.getTimeIssued() == this.time.intValue()){
														System.out.println(this.getName() +" send "+m.getMissionName());
													getSimplePublisher().sendEvent(new MissionReceivedEvent(m.getMissionName(),m.getSerialAgentsNumbers(),
																					m.getGadget(),m.getTimeExpired(), m.getTimeIssued(),
																					m.getDuration()));}
													time.incrementAndGet();});
	countDownLatch.countDown();
	}


	public void load(MissionInfo[] s){
		for (MissionInfo m: s)
			this.list_Info.add(m);
	}
}
