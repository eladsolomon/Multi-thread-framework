package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.GadgetAvailbleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TickEndBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {
	Inventory i;
	int currentTime = 0;
	private CountDownLatch countDownLatch;

	public Q(CountDownLatch countDownLatch) {
		super("Q");
		this.countDownLatch=countDownLatch;
	}

	@Override
	protected void initialize() {
		i = Inventory.getInstance();
			subscribeBroadcast(TickBroadcast.class, c -> {
				currentTime = c.getTime();
			});
			subscribeBroadcast(TickEndBroadcast.class, c -> terminate());
			subscribeEvent(GadgetAvailbleEvent.class,c-> {
															c.setTimeCharge(this.currentTime);
															Boolean ans = i.getItem(c.getGadget());
															if(ans) {
															System.out.println(this.getName()+ " got the gadget " + c.getGadget());
																complete(c, true);
															}
															else
																complete(c,false);
			});
		countDownLatch.countDown();
		}
	}

