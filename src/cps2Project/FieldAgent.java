package cps2Project;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class FieldAgent extends Agent{
	
	protected int IDFieldAgent;
	protected int IDUpperSensorAgent;
	
	public int getIDFieldAgent() {
		return IDFieldAgent;
	}

	public FieldAgent(int IDFieldAgent, int IDUpperSensorAgent) {
		this.IDFieldAgent = IDFieldAgent;
		this.IDUpperSensorAgent = IDUpperSensorAgent;
	}
	
	@Override
	public void compute() {
		// TODO Auto-generated method stub
//		System.out.println("FA #" + IDFieldAgent + " is on and waiting");
	}

}
