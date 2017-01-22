package cps2Project;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;


public class ContextCreator implements ContextBuilder<Agent> {
	
	float drillingAngle;

	protected Context<Agent> context;

	/*--------------CONSTRUCTOR-----------------*/
	@Override
	public Context<Agent> build(Context<Agent> context) {
		
		/*--------------GETTING THE VARIABLES-----------------*/
		//TODO: add all the variables mentioned in the second Meeting Report
		int nbSensor = RunEnvironment.getInstance().getParameters().getInteger("nbSensor");
		int nbActuator = RunEnvironment.getInstance().getParameters().getInteger("nbActuator");
		drillingAngle = RunEnvironment.getInstance().getParameters().getFloat("drillingAngle");
		int nextID = 1;
		
		//the system is created bottom-up :
		//1) the downhole actuator
		//2) the sensor agent list, ending with the upper most SA
		//3) the field agent
		//4) the uphole actuators
		
		/*--------------CREATING THE DOWNHOLE ACTUATOR-----------------*/
		ActuatorAgent aa = new ActuatorAgent(nextID,true,nextID+1);
		nextID++;
		context.add(aa);
		
		/*--------------CREATING THE SENSOR AGENTS-----------------*/
		for (int i = 0;i<nbSensor;i++)
		{
			double startingMeasuredDepth = 70 - (10*i);
			SensorAgent sa = new SensorAgent(nextID,nextID+1,nextID-1,nbSensor,this,startingMeasuredDepth);
			context.add(sa);
			nextID++;
		}
		
		/*--------------CREATING THE FIELD AGENT-----------------*/
		FieldAgent fa = new FieldAgent(nextID,nextID-1);
		nextID++;
		context.add(fa);
		
		/*--------------CREATING THE UPHOLE ACTUATORS-----------------*/
		for (int i = 0;i<nbActuator;i++)
		{
			int IDFA = fa.getIDFieldAgent(); //get the ID of the Field Agent
			aa = new ActuatorAgent(nextID,false,IDFA);
			context.add(aa);
			nextID++;
		}
		
		return context;
	}
	
	public double getTrueDepth(double measuredDepth)
	{
		double trueDepth = 0;
		double angle = ((this.drillingAngle)/180)*3.14159;
		trueDepth = measuredDepth * Math.cos(angle) ;//* (57.3);
		System.out.println(Math.cos(angle));
		return trueDepth;
	}

}
