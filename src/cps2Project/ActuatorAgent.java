package cps2Project;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class ActuatorAgent extends Agent{
	
	protected double power; //for now, maybe we can represent the actuator power by a variable (e.g. from 0 to 100)
	protected int IDActuatorAgent;
	protected boolean downhole; //we can represent the place of the actuator with this boolean (true = downhole, false = uphole)
	
	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public int getIDActuatorAgent() {
		return IDActuatorAgent;
	}

	public ActuatorAgent(int IDActuatorAgent,boolean downhole) {
		this.IDActuatorAgent = IDActuatorAgent;
		this.downhole = downhole;
		this.power = 0.0; //we start with the Actuator Agent switched off
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub
		//System.out.println("AA #" + IDActuatorAgent + " is at " + power + "% of full power");
	}
	
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "increaseDownHoleActu", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void temperatureDanger(SensorAgent sensorAgent) {
		
		if (downhole) //must listen to the SensorAgent only if it is the downhole actuator agent
		{
			if (power< 95.0)
			{
				power = power + Math.random()*5;
				System.out.println("AA #" + IDActuatorAgent + " is at " + power + "% of full power");
			}
			else
			{
				System.out.println("Error : AA #" + IDActuatorAgent + " is already at " + power + "% of full power!");
				//TODO: message the Field Agent to start the uphole actuator agent to relieve the downhole actuator agent
			}
		}

	}

}
