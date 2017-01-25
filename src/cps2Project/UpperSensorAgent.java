package cps2Project;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class UpperSensorAgent extends SensorAgent{
	
	//variables used to send a message to the FA up-hole
	protected int nextTooHotMessage = -1;
	protected boolean messageFATooHot = false;
	
	
	

	public UpperSensorAgent(int IDSensorAgent, int neighborUp, int neighborDown, int nbSensor, ContextCreator context,
			double measuredDepth, double dangerTemp, double criticalTemp, double shutdownTemp) {
		super(IDSensorAgent, neighborUp, neighborDown, nbSensor, context, measuredDepth, dangerTemp, criticalTemp,
				shutdownTemp);
		// TODO Auto-generated constructor stub
		System.out.println("Upper Sensor agent created");
	}
	
	
	@Override
	public void compute() {
		actionAlreadyTaken = false; //this boolean is a quickfix to avoid having several action taken from the same vote. With that, we can only have one vote/tick (1 vote/min)
		if (nextVoteCountdown > 0)
			nextVoteCountdown--;
		double speed = context.getDrillingSpeed();
		measuredDepth += speed;
		trueDepth = context.getTrueDepth(measuredDepth);
		temperature = context.getTemperatureFromTVD(trueDepth);
		
		if (temperature >= shutdownTemp)
		{
			//end the simulation
			System.out.println("Failure! Agent #"+IDSensorAgent+" overheated.");
			RunEnvironment.getInstance().endRun();
		}
		else if (temperature >= criticalTemp)
		{
			if (nextVoteCountdown == 0)
			{
//				System.out.println("Agent #" + IDSensorAgent+ " is at "+ temperature+"°C of "+criticalTemp+" and is starting a vote!");
				startVote();				
				nextVoteCountdown = 20; //the agent cannot vote again before 20 ticks
			}
		}
		else if (temperature >= dangerTemp)
		{
			//send a message to the FA
			messageFATooHot(this);
		}
		
		if (nextTooHotMessage != -1)
		{
			if (nextTooHotMessage == 0)
				messageFATooHot = !messageFATooHot;
			nextTooHotMessage --; //if we reached , back to -1
		}
		
	}
	
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "messageFATooHot", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void messageFATooHot(SensorAgent sensorAgent)
	{
		if ((sensorAgent.getIDSensorAgent() == (IDSensorAgent-1) ||sensorAgent.getIDSensorAgent() == IDSensorAgent) && nextTooHotMessage == -1) //if we receive a message from the SA or self under and we don't already have a message in the queue
		{
//			messageFATooHot = !messageFATooHot;
			nextTooHotMessage = 60;
		}
	}

}
