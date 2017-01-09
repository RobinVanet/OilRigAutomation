package cps2Project;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;


public class ContextCreator implements ContextBuilder<Agent> {

	protected Context<Agent> context;

	/*--------------CONSTRUCTOR-----------------*/
	@Override
	public Context<Agent> build(Context<Agent> context) {
		
		/*--------------GETTING THE VARIABLES-----------------*/
		//TODO: add all the variables mentioned in the second Meeting Report
		int nbSensor = RunEnvironment.getInstance().getParameters().getInteger("nbSensor");
		int nbActuator = RunEnvironment.getInstance().getParameters().getInteger("nbActuator");
		int nextID = 1;
		
		/*--------------CREATING THE SENSOR AGENTS-----------------*/
		for (int i = 0;i<nbSensor;i++)
		{
			SensorAgent sa = new SensorAgent(nextID);
			context.add(sa);
			nextID++;
		}
		
		/*--------------CREATING THE DOWNHOLE ACTUATOR-----------------*/
		ActuatorAgent aa = new ActuatorAgent(nextID,true);
		nextID++;
		context.add(aa);

		/*--------------CREATING THE UPHOLE ACTUATORS-----------------*/
		for (int i = 0;i<nbActuator;i++)
		{
			aa = new ActuatorAgent(nextID,false);
			context.add(aa);
			nextID++;
		}
		
		/*--------------CREATING THE FIELD AGENT-----------------*/
		FieldAgent fa = new FieldAgent(nextID);
		nextID++;
		context.add(fa);
		
		return context;
	}

}
