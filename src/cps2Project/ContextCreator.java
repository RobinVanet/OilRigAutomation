package cps2Project;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;


public class ContextCreator implements ContextBuilder<Agent> {
	
	float drillingAngle;
	double drillingSpeed;
	float surfaceTemp;
	float geotermalGradient;

	protected Context<Agent> context;

	/*--------------CONSTRUCTOR-----------------*/
	@Override
	public Context<Agent> build(Context<Agent> context) {
		
		/*--------------GETTING THE VARIABLES-----------------*/
		//TODO: add all the variables mentioned in the second Meeting Report
		int nbSensor = RunEnvironment.getInstance().getParameters().getInteger("nbSensor");
		int nbActuator = RunEnvironment.getInstance().getParameters().getInteger("nbActuator");
		drillingAngle = RunEnvironment.getInstance().getParameters().getFloat("drillingAngle");
		drillingSpeed = RunEnvironment.getInstance().getParameters().getFloat("drillingSpeed");
		surfaceTemp = RunEnvironment.getInstance().getParameters().getFloat("surfaceTemp");
		geotermalGradient =  RunEnvironment.getInstance().getParameters().getFloat("geotermalGradient");
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
			double dangerTemp = 100 + (int)(Math.random() * ((125 - 100) + 1)); //random value from 100 to 125
			double criticalTemp = 125 + (int)(Math.random() * ((150 - 125) + 1)); //random value from 125 to 150
			double shutdownTemp = 150; //shutdown at 150°C for everyone
			SensorAgent sa = new SensorAgent(nextID,nextID+1,nextID-1,nbSensor,this,startingMeasuredDepth,dangerTemp,criticalTemp,shutdownTemp);
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
		trueDepth = measuredDepth * Math.cos(angle) ;
		return trueDepth;
	}

	public double getDrillingSpeed() {
		return drillingSpeed;
	}

	public void setDrillingSpeed(double drillingSpeed) {
		this.drillingSpeed = drillingSpeed;
	}
	
	public double getTemperatureFromTVD(double trueDepth)
	{
		double temperature = 0;
		temperature = surfaceTemp + (geotermalGradient * trueDepth);
		return temperature;
	}
	
	public void lowerDrillingSpeed()
	{
		drillingSpeed = (drillingSpeed * 0.90);
		System.out.println("New drilling speed is " + drillingSpeed + "m/min");
	}

}
