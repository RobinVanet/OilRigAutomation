package cps2Project;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.continuous.ContinuousSpace;

/**
 * The class used to represent the actuators in the system. Their goal is to
 * stay within the 0-100% use range through tests when ordered.
 * 
 * @author Robin Vanet
 *
 */
public class ActuatorAgent extends Agent {

	/*--------------VARIABLES-----------------*/
	// informations on the actuator itself
	protected double power; // for now, maybe we can represent the actuator
							// power by a variable (e.g. from 0 to 100)
	protected int IDActuatorAgent;
	protected int IDPilotAgent;// the upper agent in the list of agents
	protected boolean downhole; // we can represent the place of the actuator
								// with this boolean (true = downhole, false =
								// uphole), currently we have only an uphole
								// actuator agent
	protected ContextCreator context;
	protected double effectiveness;// ?? 1 for now

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
	public ActuatorAgent(ContinuousSpace<Agent> space, int IDActuatorAgent, boolean downhole, int IDPilotAgent,
			ContextCreator context, double effectiveness) {
		this.space = space;
		this.IDActuatorAgent = IDActuatorAgent;
		this.IDPilotAgent = IDPilotAgent;
		this.downhole = downhole;
		this.power = 0.0; // we start with the Actuator Agent switched off
		this.context = context;
		this.effectiveness = effectiveness;
	}

	/*--------------FUNCTIONS-----------------*/
	/**
	 * Method used at each tick (as defined in the Agent class)
	 */
	@Override
	public void compute() {
/*		double speed = context.getSpeed();
		measuredDepth += speed;
		trueDepth = context.getTrueDepth(measuredDepth);
		temperature = context.getTemperatureFromTVD(trueDepth);

		space.moveTo(this, 5, context.getYCoordinates(trueDepth)); // moving the
																	// agent on
																	// the
																	// display
*/	}

	/**
	 * Watch the FieldAgent class and check if they are a neighbor giving an
	 * order
	 * 
	 * @param fieldAgent
	 *            : the Field Agent giving the order
	 */
	@Watch(watcheeClassName = "cps2Project.FieldAgent", watcheeFieldNames = "coolDown", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void coolDown(FieldAgent fieldAgent) {
		if (fieldAgent.getIDFieldAgent() == (IDActuatorAgent - 1)) {
			// This line is not used properly because it does not use the IDFA
			// (ID of the field agent), so if we have more than one uphole
			// actuator agent then only the one that has the consequent ID to
			// field agent will work
			// it should be if (fieldAgent.getIDFieldAgent() ==
			// this.IDPilotAgent)
			// System.out.println("Actuator " + IDActuatorAgent +" Cooling the
			// equipment down");
			if (power <= 90) {
				context.coolDown(effectiveness);
				power = power + 10;
				System.out.println ("Cooling power increased to " + power + "%");
			} else {
				// System.out.println("Actuator already at full power!");
			}
		}
	}
	
	@Watch(watcheeClassName = "cps2Project.FieldAgent", watcheeFieldNames = "reduceCooling", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void reduceCooling(FieldAgent fieldAgent) {
		if (fieldAgent.getIDFieldAgent() == (IDActuatorAgent - 1)) {
			// This line is not used properly because it does not use the IDFA
			// (ID of the field agent), so if we have more than one uphole
			// actuator agent then only the one that has the consequent ID to
			// field agent will work
			// it should be if (fieldAgent.getIDFieldAgent() ==
			// this.IDPilotAgent)
			// System.out.println("Actuator " + IDActuatorAgent +" Cooling the
			// equipment down");
			if (power >= 10) {
				context.reduceCooling(effectiveness);
				power = power - 10;
				System.out.println ("Cooling power reduced to " + power + "%");
			} else {
				// System.out.println("Actuator already at full power!");
			}
		}
	}

}
