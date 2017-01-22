package cps2Project;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class ActuatorAgent extends Agent{
	
	protected double power; //for now, maybe we can represent the actuator power by a variable (e.g. from 0 to 100)
	protected int IDActuatorAgent;
	protected int IDPilotAgent;
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

	public ActuatorAgent(int IDActuatorAgent,boolean downhole,int IDPilotAgent) {
		this.IDActuatorAgent = IDActuatorAgent;
		this.IDPilotAgent = IDPilotAgent;
		this.downhole = downhole;
		this.power = 0.0; //we start with the Actuator Agent switched off
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub
		//System.out.println("AA #" + IDActuatorAgent + " is at " + power + "% of full power");
	}

}
