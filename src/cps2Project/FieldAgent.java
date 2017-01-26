package cps2Project;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class FieldAgent extends Agent{
	
	protected int IDFieldAgent;
	protected int IDUpperSensorAgent;
	
	protected int accelerateCounter = 0;
	protected boolean speedDrill = false;
	protected int nextSpeedDrillMessage = -1;
	
	//messages to the AA
	protected boolean coolDown = false;
	
	public int getIDFieldAgent() {
		return IDFieldAgent;
	}

	public FieldAgent(int IDFieldAgent, int IDUpperSensorAgent) {
		this.IDFieldAgent = IDFieldAgent;
		this.IDUpperSensorAgent = IDUpperSensorAgent;
	}
	
	@Override
	public void compute() {
		accelerateCounter++;
		if (accelerateCounter == 120)
		{
			speedDrill();
			accelerateCounter =0;
		}
		if (nextSpeedDrillMessage != -1)
		{
			if (nextSpeedDrillMessage == 0)
					speedDrill = !speedDrill;
			nextSpeedDrillMessage--;
		}
	}
	
	@Watch(watcheeClassName = "cps2Project.UpperSensorAgent", watcheeFieldNames = "messageFATooHot", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void messageFATooHot(UpperSensorAgent sensorAgent)	
	{
//		System.out.println("FA detected that the temperature is too hot!");
		coolDown();
		accelerateCounter = 0;
	}
	
	@Watch(watcheeClassName = "cps2Project.UpperSensorAgent", watcheeFieldNames = "messageFASlowDown", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void messageFASlowDown(UpperSensorAgent sensorAgent)
	{
//		System.out.println("FA learned that the drill was slowed down!");
//		coolDown();
	}
	
	public void coolDown()
	{
		//TODO: comment/uncomment this to remove the cooling down
//		coolDown = !coolDown;
//		System.out.println("Request for cooling.");
//		RunEnvironment.getInstance().pauseRun();
	}
	
	public void speedDrill()
	{
		nextSpeedDrillMessage = 60;
	}

}
