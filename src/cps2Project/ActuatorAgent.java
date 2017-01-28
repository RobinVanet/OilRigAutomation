package cps2Project;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

/**
 * The class used to represent the actuators in the system.
 * Their goal is to stay within the 0-100% use range through tests when ordered.
 * 
 * @author Robin Vanet
 *
 */
public class ActuatorAgent extends Agent{
	
	/*--------------VARIABLES-----------------*/
	//informations on the actuator itself
	protected double power; //for now, maybe we can represent the actuator power by a variable (e.g. from 0 to 100)
	protected int IDActuatorAgent;
	protected int IDPilotAgent;
	protected boolean downhole; //we can represent the place of the actuator with this boolean (true = downhole, false = uphole)
	protected ContextCreator context;
	protected double effectiveness;
	
	/*--------------GETTERS AND SETTERS-----------------*/
	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public int getIDActuatorAgent() {
		return IDActuatorAgent;
	}

	/*--------------CONSTRUCTOR-----------------*/
	public ActuatorAgent(int IDActuatorAgent,boolean downhole,int IDPilotAgent, ContextCreator context, double effectiveness) {
		this.IDActuatorAgent = IDActuatorAgent;
		this.IDPilotAgent = IDPilotAgent;
		this.downhole = downhole;
		this.power = 0.0; //we start with the Actuator Agent switched off
		this.context = context;
		this.effectiveness = effectiveness;
	}

	/*--------------FUNCTIONS-----------------*/
	/**
	 * Method used at each tick (as defined in the Agent class)
	 */
	@Override
	public void compute() {
	}
	
	/**
	 * Watch the FieldAgent class and check if they are a neighbor giving an order
	 * 
	 * @param fieldAgent : the Field Agent giving the order
	 */
	@Watch(watcheeClassName = "cps2Project.FieldAgent", watcheeFieldNames = "coolDown", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void coolDown(FieldAgent fieldAgent)
	{
		if (fieldAgent.getIDFieldAgent() == (IDActuatorAgent-1))
		{
//			System.out.println("Actuator " + IDActuatorAgent +" Cooling the equipment down");
			if (power<= 90)
			{
				context.coolDown(10*effectiveness);
				power = power+10;
			}
			else
			{
				//System.out.println("Actuator already at full power!");
			}
		}
	}

}
