package bgu.spl.mics.application.passiveObjects;
import bgu.spl.mics.MessageBrokerImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private Map<String, Agent> agents = new ConcurrentHashMap<>();
	private final Object locker_Map = new Object();

	/**
	 * Retrieves the single instance of this class.
	 */
	private static class SingleInstance{
		private static Squad s = new Squad();
	}
	public static Squad getInstance() {
		return SingleInstance.s;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		for (Agent a : agents)
			this.agents.put(a.getSerialNumber(),a);
	}

	/**
	 * Releases agents.
	 */
	public void releaseAgents(List<String> serials){
		synchronized (locker_Map) {
			for (String s : serials)
				agents.get(s).release();
			locker_Map.notifyAll();
		}
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   milliseconds to sleep
	 */
	public void sendAgents(List<String> serials, int time){
         try{Thread.sleep(time*100);}
         catch(Exception e){

		 }
		releaseAgents(serials);
		System.out.println(serials.toString() +" Came Back and now Free");
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){

			synchronized (locker_Map){
				for(String s : serials)
					if(!agents.containsKey(s))
						return false;
				try {
						while (!allReady(serials))
							locker_Map.wait();
						for(String s : serials)
							agents.get(s).acquire();
					}
				catch(InterruptedException e){
					Thread.currentThread().interrupt();
					return false;
				}
			}
			return true;
	}

	private boolean allReady(List<String > list)
	{
		for(String s : list)
			if(!agents.get(s).isAvailable())
				return false;
			return true;
	}
    /**
     * gets the agents names
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */
    public List<String> getAgentsNames(List<String> serials){
    	List <String> names = new LinkedList<>();
    	for (String s : serials){
    		names.add(agents.get(s).getName());
		}
    	return names;
    }

}
