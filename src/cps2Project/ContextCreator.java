package cps2Project;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;

import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;

/**
 * Context creating class. Creates and places all the agents of the simulation.
 * Also used to get informations/actions on the environment on the Sensor Agents array (get the TVD and temperature, slow down the drill, cooling the equipments).
 * 
 * @author Robin Vanet
 *
 */
public class ContextCreator implements ContextBuilder<Agent> {
	
	/*--------------VARIABLES-----------------*/
	float drillingAngle;
	double rotationsPerMinute;
	double minimimumRotationsPerMinute;
	double weightOnBit;
	float holeDiameter;
	boolean hardFormation;
	float surfaceTemp;
	float geothermalGradient;
	double coolDown = 0; //the cooling down taking place, starts at no cooling down 
	int depthGoal;
	ContinuousSpaceFactory spaceFactory;
	ContinuousSpace<Agent> space;

	protected Context<Agent> context;
	
	/*--------------GETTERS AND SETTERS-----------------*/
	/**
	 * Get the true depth of an equipment given the angle of drilling and the measured depth
	 * 
	 * @param measuredDepth
	 * @return the true depth linked to the measured depth
	 */
	public double getTrueDepth(double measuredDepth)
	{
		double trueDepth = 0;
		double angle = ((this.drillingAngle)/180)*3.14159;
		trueDepth = measuredDepth * Math.cos(angle) ;
		checkDepthReached(trueDepth);
		return trueDepth;
	}

	/**
	 * Get the temperature from a TVD with the geothermal gradient, the surface temperature, and the cooling.
	 * 
	 * @param trueDepth
	 * @return the temperature associated to the TVD
	 */
	public double getTemperatureFromTVD(double trueDepth)
	{
		double temperature = 0;
		temperature = surfaceTemp + (geothermalGradient * trueDepth);
		temperature = temperature - coolDown;
		return temperature;
	}
	
	/**
	 * Lower the drilling speed 
	 */
	public void lowerDrillingSpeed()
	{
		rotationsPerMinute = (rotationsPerMinute * 0.90);
		if (rotationsPerMinute <= minimimumRotationsPerMinute)
				rotationsPerMinute = minimimumRotationsPerMinute;
	}
	
	/**
	 * Increase the drilling speed
	 */
	public void increaseDrillingSpeed()
	{
		rotationsPerMinute = (rotationsPerMinute * 1.111111111); //the opposite of slowing down
	}
	
	/**
	 * Get the speed according to all the factors
	 * @return the speed in m/min of the last minute
	 */
	public double getSpeed()
	{
		double speed = 0;
		double K = 1; //TODO: find a better factor
		speed = K * getWOBFactor(weightOnBit, holeDiameter) * getROPFactor(rotationsPerMinute,hardFormation) * getFlowFactor();
		System.out.println("Speed = "+speed);
		return speed;
	}
	
	/**
	 * Get the Weight On Bit factor in the Speed equation
	 * 
	 * @param weightOnBit : force applied to the tools from uphole
	 * @param holeDiameter
	 * @return the factor
	 */
	public double getWOBFactor(double weightOnBit, float holeDiameter){
		double factor = 1;
		factor =  (7.88 * weightOnBit)/holeDiameter;
		System.out.println("WOB factor = "+factor);
		return factor;
	}
	
	/**
	 * Get the Rotations Per Minute factor in the Speed equation
	 * 
	 * @param rotationsPerMinute : Rotations per Minute from the bit (both uphole and downhole rotation applied)
	 * @param hardFormation : whether the formation is hard or soft
	 * @return the factor
	 */
	public double getROPFactor(double rotationsPerMinute,boolean hardFormation)
	{
		double factor = 1;
		double N = rotationsPerMinute;
		double a = (-100)/(N*N);
		if (hardFormation)
		{
			factor = (Math.exp(a) * Math.pow(N,-.428))+0.2*N*(1-Math.exp(a));
		}
		else
		{
			factor = (Math.exp(a) * Math.pow(N,-.75))+0.5*N*(1-Math.exp(a));
		}
		System.out.println("RPM factor = "+factor);
		return factor;
	}

	/**
	 * Get the flow factor in the Speed equation
	 * @return
	 */
	public double getFlowFactor()
	{
		return 1; //TODO : determine the flow factor
	}
	
	/*--------------CONSTRUCTOR-----------------*/
	/**
	 * Function used to create and place the agents in the simulation.
	 */
	@Override
	public Context<Agent> build(Context<Agent> context) {
		
		/*--------------GETTING THE VARIABLES-----------------*/
		int nbSensor = RunEnvironment.getInstance().getParameters().getInteger("nbSensor");
		int nbActuator = RunEnvironment.getInstance().getParameters().getInteger("nbActuator");
		drillingAngle = RunEnvironment.getInstance().getParameters().getFloat("drillingAngle");
		rotationsPerMinute = RunEnvironment.getInstance().getParameters().getDouble("initialRPM");
		float downHoleRPMPercentage =  RunEnvironment.getInstance().getParameters().getFloat("downHoleRPMPercentage");
		minimimumRotationsPerMinute = rotationsPerMinute * (100 - downHoleRPMPercentage);
		weightOnBit = RunEnvironment.getInstance().getParameters().getDouble("weightOnBit");
		holeDiameter = RunEnvironment.getInstance().getParameters().getFloat("holeDiameter");
		hardFormation = RunEnvironment.getInstance().getParameters().getBoolean("hardFormation");
		surfaceTemp = RunEnvironment.getInstance().getParameters().getFloat("surfaceTemp");
		geothermalGradient =  RunEnvironment.getInstance().getParameters().getFloat("geothermalGradient");
		double actuatorEffectiveness = RunEnvironment.getInstance().getParameters().getDouble("actuatorEffectiveness");
		depthGoal = RunEnvironment.getInstance().getParameters().getInteger("depthGoal");
		boolean voteEnabled = RunEnvironment.getInstance().getParameters().getBoolean("voteEnabled");
		boolean coolingEnabled = RunEnvironment.getInstance().getParameters().getBoolean("coolingEnabled");
		int nextID = 1;
		
		/*-------------- CREATING THE CONTINUOUS SPACE-----------------*/
		spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		space = spaceFactory.createContinuousSpace("space", context,new RandomCartesianAdder<Agent>(), new repast.simphony.space.continuous.WrapAroundBorders(), 11,120);

		//the system is created bottom-up :
		//1) the downhole actuator
		//2) the sensor agent list, ending with the upper most SA
		//3) the field agent
		//4) the uphole actuators
		
		/*--------------CREATING THE DOWNHOLE ACTUATOR-----------------*/
		ActuatorAgent aa = new ActuatorAgent(space,nextID,true,nextID+1,this,1);
		nextID++;
		context.add(aa);
		
		/*--------------CREATING THE SENSOR AGENTS-----------------*/
		for (int i = 0;i<nbSensor-1;i++)
		{
			double startingMeasuredDepth = (nbSensor*10) - (10*(i+1));
			double dangerTemp = 100 + (int)(Math.random() * ((125 - 100) + 1)); //random value from 100 to 125
			double criticalTemp = 125 + (int)(Math.random() * ((150 - 125) + 1)); //random value from 125 to 150
			double shutdownTemp = 150; //shutdown at 150°C for everyone
			SensorAgent sa = new SensorAgent(space,nextID,nextID+1,nextID-1,nbSensor,this,startingMeasuredDepth,dangerTemp,criticalTemp,shutdownTemp,voteEnabled);
			context.add(sa);
			space.moveTo(sa, 5,getYCoordinates(startingMeasuredDepth));
			nextID++;
		}
		
		double startingMeasuredDepth = 0;
		double dangerTemp = 100 + (int)(Math.random() * ((125 - 100) + 1)); //random value from 100 to 125
		double criticalTemp = 125 + (int)(Math.random() * ((150 - 125) + 1)); //random value from 125 to 150
		double shutdownTemp = 150; //shutdown at 150°C for everyone
		UpperSensorAgent sa =  new UpperSensorAgent(space,nextID,nextID+1,nextID-1,nbSensor,this,startingMeasuredDepth,dangerTemp,criticalTemp,shutdownTemp,voteEnabled);
		context.add(sa);
		space.moveTo(sa, 5,getYCoordinates(startingMeasuredDepth));
		nextID++;
		
		/*--------------CREATING THE FIELD AGENT-----------------*/
		FieldAgent fa = new FieldAgent(space,nextID,nextID-1,coolingEnabled);
		nextID++;
		context.add(fa);
		space.moveTo(fa, 5,-10); //we place it at ground level
		
		/*--------------CREATING THE UPHOLE ACTUATORS-----------------*/
		for (int i = 0;i<nbActuator;i++)
		{
			int IDFA = fa.getIDFieldAgent(); //get the ID of the Field Agent
			aa = new ActuatorAgent(space,nextID,false,IDFA,this,actuatorEffectiveness);
			context.add(aa);
			nextID++;
		}
		
		/*--------------CREATING THE AGENT REPRESENTING THE GOAL-----------------*/
		GoalAgent ga = new GoalAgent();
		ga.setGoalDepth(depthGoal);
		context.add(ga);
		space.moveTo(ga, 5,getYCoordinates(depthGoal));
		
		/*--------------CREATING THE BACKGROUND-----------------*/
		for (int i = 0;i<120;i++)
		{
			for (int j=0;j<11;j++)
			{
				BackgroundAgent bga = new BackgroundAgent();
				bga.setX(j);
				bga.setY(i);
				//we define the air tiles
				if (i<=10 && i!=0)
					bga.setContent(false);
				else //all others are ground tiles
					bga.setContent(true);
				context.add(bga);
				space.moveTo(bga, j,-i);
			}
		}
		
		
		return context;
	}
	
	/*--------------FUNCTIONS-----------------*/
	/**
	 * Increase the cooling of the equipments
	 * @param cooling : the number of °C the equipments are cooled down.
	 */
	public void coolDown(double cooling)
	{
		coolDown += cooling;
	}
	
	/**
	 * Check if we reached the depth goal.
	 * If it is reached, ends the simulation with a message.
	 * 
	 * @param depth : the depth compared to the goal
	 */
	public void checkDepthReached (double depth)
	{
		if (depth>=depthGoal)
		{
			double ticks = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
			System.out.println("Depth goal reached in " + ticks + " minutes");
			RunEnvironment.getInstance().endRun();
		}
	}
	
	/**
	 * returns the Y coordinates of the TVD input
	 * 
	 * @param TVD : the TVD of the agent going down
	 * @return a number in the scope [10-110]
	 */
	public double getYCoordinates(double TVD)
	{
		double yCoord = 10 + ((TVD/depthGoal)*100);
		return -yCoord;
	}
	
}
