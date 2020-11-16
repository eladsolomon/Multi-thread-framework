package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {
	private int id;
	private CountDownLatch countDownLatch;

	public Moneypenny(int id, CountDownLatch countDownLatch) {
		super("MoneyPenny-" + id);
		this.id=id;
		this.countDownLatch=countDownLatch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickEndBroadcast.class, c -> terminate());
		if(this.id%2==0) {
			subscribeEvent(AgentsAvailbleEvent.class, c -> {
				System.out.println(this.getName() + " Try to get the Squad " + c.getAgents().toString());
				boolean ans = Squad.getInstance().getAgents(c.getAgents());
				System.out.println(ans);
				if (ans) {
					System.out.println(this.getName()+ " GOT the Squad : " + c.getAgents().toString());
					complete(c, true);
				}
				else
					complete(c, false);
			});
		}
		else {
			subscribeEvent(ReleasAgentsEvent.class, c -> {
				Squad.getInstance().releaseAgents(c.getList_Agents());
				complete(c,true);
			});
			subscribeEvent(SendAgentsEvent.class, c -> {
				c.setIdCharge(this.id);
				c.setList_agents_names(Squad.getInstance().getAgentsNames(c.getList_Agents()));
				System.out.println(this.getName() + " send the agents: "+ c.getList_Agents().toString() + " to the mission");
				Squad.getInstance().sendAgents(c.getList_Agents(), c.getDuration());
				complete(c , id);
				System.out.println("Agents send to the Mission");
			});
		}
		countDownLatch.countDown();
	}

}
