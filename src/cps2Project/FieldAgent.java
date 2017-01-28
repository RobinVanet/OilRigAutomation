package cps2Project;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

/**
 * The class used to represent the Field Agent of the simulation (one per well).
 * Their goal is to help reaching the goal depth the fastest way possible by cooling down the equipments
 * when it is threatened and increasing the speed when it is safe to do so.
 * 
 * @author Robin Vanet
 *
 */
public class FieldAgent extends Agent{
	
	/*--------------VARIABLES-----------------*/
	protected int IDFieldAgent;
	protected int IDUpperSensorAgent;
	protected boolean coolingEnabled;
	
	protected int accelerateCounter = 0;
	protected boolean speedDrill = false;
	protected int nextSpeedDrillMessage = -1;
	
	//messages to the AA
	protected boolean coolDown = false;
	
	/*--------------GETTERS AND SETTERS-----------------*/
	public int getIDFieldAgent() {
		return IDFieldAgent;
	}

	/*--------------CONSTRUCTOR-----------------*/
	public FieldAgent(int IDFieldAgent, int IDUpperSensorAgent, boolean coolingEnabled) {
		this.IDFieldAgent = IDFieldAgent;
		this.IDUpperSensorAgent = IDUpperSensorAgent;
		this.coolingEnabled = coolingEnabled;
	}
	
	/*--------------FUNCTIONS-----------------*/
	/**
	 * Method used at each tick (as defined in the Agent class)
	 */
	@Override
	public void compute() {
		//if no agent complained of being to hot in the last 120 ticks (2 hours), speed the drill up
		accelerateCounter++;
		if (accelerateCounter == 120)
		{
			speedDrill();
			accelerateCounter =0;
		}
		if (nextSpeedDrillMessage != -1)      //if a message is queued
		{
			if (nextSpeedDrillMessage == 0)   //if the message reached the end of the queue
					speedDrill = !speedDrill; //it is launched 
			nextSpeedDrillMessage--;
		}
	}
	
	/**
	 * Watch the UpperSensorAgent to get the message "Agent too hot" from the Sensor Array
	 * Cools down the system when this function is activated.
	 * 
	 * @param sensorAgent : the UpperSensorAgent sending the message
	 */
	@Watch(watcheeClassName = "cps2Project.UpperSensorAgent", watcheeFieldNames = "messageFATooHot", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void messageFATooHot(UpperSensorAgent sensorAgent)	
	{
//		System.out.println("FA detected that the temperature is too hot!");
		coolDown();
		accelerateCounter = 0; //we note that the sensor agents were too hot 0 ticks ago
	}
	
	/**
	 * Watch the UpperSensorAgent to get the message "Drill slowed down" from the Sensor Array
	 * 
	 * @param sensorAgent : the UpperSensorAgent sending the message
	 */
	@Watch(watcheeClassName = "cps2Project.UpperSensorAgent", watcheeFieldNames = "messageFASlowDown", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void messageFASlowDown(UpperSensorAgent sensorAgent)
	{
//		System.out.println("FA learned that the drill was slowed down!");
//		coolDown();
	}
	
	/**
	 * If the cooling is enabled, sends a message to the actuators to cool the system down.
	 */
	public void coolDown()
	{
		if (coolingEnabled)
		{
			coolDown = !coolDown;
		}	
	}
	
	/**
	 * Prepare the message to speed the drill up.
	 * When the countdown reach 0 (the message traveled the whole way down) the message is sent to the UpperSensorAgent
	 * (see compute () ).
	 */
	public void speedDrill()
	{
		nextSpeedDrillMessage = 60;
	}

}
