package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TickEndBroadcast;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@linkTick Broadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {

	int duration;
	int time ;

	public TimeService() {
		super("TimeService");
	}

	@Override
	protected void initialize() {
		this.time = 1;
	}

	@Override
	public void run() {
		initialize();
			while (time <= this.duration) {
				try{
					Thread.sleep(100);
					TickBroadcast b = new TickBroadcast(time);
					System.out.println("Tick-"+(this.time)+"\n");
					this.getSimplePublisher().sendBroadcast(b);
					this.time++;
				}
				catch(Exception e){}
			}
			try {
				System.out.println("Send TickEndBroadcast");
				Thread.sleep(100);
				TickEndBroadcast e = new TickEndBroadcast();
				this.getSimplePublisher().sendBroadcast(e);
			}
			catch(InterruptedException e){}
	}

	public void setDuration(int i){
		this.duration = i;
	}

}
