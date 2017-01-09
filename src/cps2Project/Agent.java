package cps2Project;

import repast.simphony.engine.schedule.ScheduledMethod;

public abstract class Agent {/*
	
	/*--------------CONSTRUCTOR-----------------*/
	public Agent(){
		
	}
	
	/*--------------FONCTIONS-----------------*/
	@ScheduledMethod(start = 1, interval = 1, priority = 2) //for every class derived from agent, each tick the compute() function will start
	public abstract void compute();

}
