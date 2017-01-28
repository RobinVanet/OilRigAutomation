package cps2Project;

import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * The class which all agents classes are derived from.
 * Maily used to have a compute() function in each agent.
 * 
 * @author Robin Vanet
 *
 */
public abstract class Agent {/*
	
	/*--------------CONSTRUCTOR-----------------*/
	public Agent(){
		
	}
	
	/*--------------FUNCTIONS-----------------*/
	@ScheduledMethod(start = 1, interval = 1, priority = 2) //for every class derived from agent, each tick the compute() function will start
	public abstract void compute();

}
