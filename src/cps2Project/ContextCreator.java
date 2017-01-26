package cps2Project;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;


public class ContextCreator implements ContextBuilder<Agent> {
	
	float drillingAngle;
	double drillingSpeed;
	float surfaceTemp;
	float geothermalGradient;
	double coolDown = 0; //the cooling down taking place, starts at no cooling down 
	int depthGoal;

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
		geothermalGradient =  RunEnvironment.getInstance().getParameters().getFloat("geothermalGradient");
		double actuatorEffectiveness = RunEnvironment.getInstance().getParameters().getDouble("actuatorEffectiveness");
		depthGoal = RunEnvironment.getInstance().getParameters().getInteger("depthGoal");
		boolean voteEnabled = RunEnvironment.getInstance().getParameters().getBoolean("voteEnabled");
		int nextID = 1;
		
		//the system is created bottom-up :
		//1) the downhole actuator
		//2) the sensor agent list, ending with the upper most SA
		//3) the field agent
		//4) the uphole actuators
		
		/*--------------CREATING THE DOWNHOLE ACTUATOR-----------------*/
		ActuatorAgent aa = new ActuatorAgent(nextID,true,nextID+1,this,1);
		nextID++;
		context.add(aa);
		
		/*--------------CREATING THE SENSOR AGENTS-----------------*/
		for (int i = 0;i<nbSensor-1;i++)
		{
			double startingMeasuredDepth = (nbSensor*10) - (10*i);
			double dangerTemp = 100 + (int)(Math.random() * ((125 - 100) + 1)); //random value from 100 to 125
			double criticalTemp = 125 + (int)(Math.random() * ((150 - 125) + 1)); //random value from 125 to 150
			double shutdownTemp = 150; //shutdown at 150°C for everyone
			SensorAgent sa = new SensorAgent(nextID,nextID+1,nextID-1,nbSensor,this,startingMeasuredDepth,dangerTemp,criticalTemp,shutdownTemp,voteEnabled);
			context.add(sa);
			nextID++;
		}
		
		double startingMeasuredDepth = 0;
		double dangerTemp = 100 + (int)(Math.random() * ((125 - 100) + 1)); //random value from 100 to 125
		double criticalTemp = 125 + (int)(Math.random() * ((150 - 125) + 1)); //random value from 125 to 150
		double shutdownTemp = 150; //shutdown at 150°C for everyone
		UpperSensorAgent sa =  new UpperSensorAgent(nextID,nextID+1,nextID-1,nbSensor,this,startingMeasuredDepth,dangerTemp,criticalTemp,shutdownTemp,voteEnabled);
		context.add(sa);
		nextID++;
		
		/*--------------CREATING THE FIELD AGENT-----------------*/
		FieldAgent fa = new FieldAgent(nextID,nextID-1);
		nextID++;
		context.add(fa);
		
		/*--------------CREATING THE UPHOLE ACTUATORS-----------------*/
		for (int i = 0;i<nbActuator;i++)
		{
			int IDFA = fa.getIDFieldAgent(); //get the ID of the Field Agent
			aa = new ActuatorAgent(nextID,false,IDFA,this,actuatorEffectiveness);
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
		checkDepthReached(trueDepth);
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
		temperature = surfaceTemp + (geothermalGradient * trueDepth);
		temperature = temperature - coolDown;
		return temperature;
	}
	
	public void lowerDrillingSpeed()
	{
		double previousDrillingSpeed = drillingSpeed;
	
		drillingSpeed = (drillingSpeed * 0.90);
		//System.out.println("New drilling speed is " + drillingSpeed + "m/min");
		if (drillingSpeed <= .01)
				drillingSpeed = .01;
		
		System.out.println("Speed changed from "+previousDrillingSpeed+" m/min to "+ drillingSpeed +"m/min.");
		//RunEnvironment.getInstance().pauseRun();
	}
	
	public void increaseDrillingSpeed()
	{
//		System.out.println("Taking up speed!");
		drillingSpeed = (drillingSpeed * 1.111111111); //the opposite of slowing down
//		RunEnvironment.getInstance().pauseRun();
	}
	
	public void coolDown(double cooling)
	{
		coolDown += cooling;
	}
	
	public void checkDepthReached (double depth)
	{
		if (depth>=depthGoal)
		{
			double ticks = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
			System.out.println("Depth goal reached in " + ticks + " minutes");
			RunEnvironment.getInstance().endRun();
		}
	}

}
