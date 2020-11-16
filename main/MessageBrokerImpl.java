package bgu.spl.mics;
import java.util.concurrent.*;
import java.util.*;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

    private Map<Class<? extends Message>, Queue<Subscriber>> Topics;
    private Map<Subscriber,Queue<Message>> subscribersQueues;
    private Map<Event, Future> event_future;

    Object locker_Topics_Map = new Object();
	Object locker_subscribersQueues_Map= new Object();


	/**
	 * Retrieves the single instance of this class.
	 */
	private MessageBrokerImpl(){
		Topics = new ConcurrentHashMap<>();
		subscribersQueues = new ConcurrentHashMap<>();
		event_future = new ConcurrentHashMap<>();
	}
	private static class SingleInstance{
		private static MessageBrokerImpl instance = new MessageBrokerImpl();
	}
	public static MessageBroker getInstance() {
		return SingleInstance.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
		synchronized (locker_Topics_Map){
		if(Topics.containsKey((type))) {
		    Topics.get(type).add(m);
		}
		else {
			Queue <Subscriber> vec = new LinkedList<>();
			vec.add(m);
			Topics.put(type,vec);
		}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		synchronized (locker_Topics_Map) {
			if (Topics.containsKey((type))) {
				Topics.get(type).add(m);
			} else {
				Queue<Subscriber> vec = new LinkedList<>();
				vec.add(m);
				Topics.put(type, vec);
			}
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if(event_future.containsKey(e)){
			event_future.get(e).resolve(result);}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
			synchronized (locker_subscribersQueues_Map) {
				if (Topics.containsKey(b.getClass())&&!Topics.get(b.getClass()).isEmpty()) {

					Queue<Subscriber> l = Topics.get(b.getClass());

					for (Subscriber s : l) {
						subscribersQueues.get(s).add(b);
					}
					locker_subscribersQueues_Map.notifyAll();
				}
				else
				{
					System.out.println( " MessagBroker FAIL to SendBroadCast  " + b.toString().substring(34) );
				}
			}

			return;
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f ;
		synchronized (this) {
			if (Topics.containsKey(e.getClass()) && !Topics.get(e.getClass()).isEmpty()) {
				Subscriber s = Topics.get(e.getClass()).poll();
				subscribersQueues.get(s).add(e);
				Topics.get(e.getClass()).add(s);
				f = new Future<T>();
				event_future.put(e, f);
				System.out.println( " MessagBroker sendEvent  " + e.toString().substring(34,47)+" to "+s.getName() );
			}

			else {
				System.out.println(" MessagBroker FAIL sendEvent  " + e.toString().substring(34,47) );
				return null;
			}
		}
		synchronized (locker_subscribersQueues_Map)
		{
			locker_subscribersQueues_Map.notifyAll();
		}

		return f;

	}

	@Override
	public void register(Subscriber m) {
		if(!subscribersQueues.containsKey((m))) {
		 Queue <Message> vec = new LinkedList<>();
			subscribersQueues.put(m,vec);
		}
	}

	@Override
	public void unregister(Subscriber m) {
		synchronized (locker_Topics_Map) {
			if (subscribersQueues.containsKey((m))) {
				Set<Class<? extends Message>> keys = Topics.keySet();
				for (Class<? extends Message> msg : keys) {
					if (Topics.get(msg).contains(m))
						Topics.get(msg).remove(m);
				}
				for(Message me :subscribersQueues.get(m))
					if(me instanceof Event)
						complete(((Event)me),null);
				subscribersQueues.remove(m);
			}
		}
	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		if (!subscribersQueues.containsKey(m))
			throw new InterruptedException();
		try {
			synchronized (locker_subscribersQueues_Map) {
				while (subscribersQueues.get(m).isEmpty()) {
					locker_subscribersQueues_Map.wait();
				}
			}

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return subscribersQueues.get(m).poll();
	}
}
