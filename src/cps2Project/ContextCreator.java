package cps2Project;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;


public class ContextCreator implements ContextBuilder<Agent> {

	protected Context<Agent> context;

	/*--------------CONSTRUCTEUR-----------------*/
	@Override
	public Context<Agent> build(Context<Agent> context) {
		for (int i = 0;i<4;i++)
		{
			SensorAgent sa = new SensorAgent(i);
			context.add(sa);
		}
		return context;
	}

}
