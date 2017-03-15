package cps2Project;

import repast.simphony.engine.environment.RunEnvironment;

/**
 * 
 * Agent used to read the variables set by the user during the program run
 * 
 * @author Robin Vanet
 *
 */
public class ReadingAgent extends Agent{

	ContextCreator context;
	
	public ReadingAgent(ContextCreator context){
		this.context = context;
	}
	
	@Override
	public void compute() {
		// TODO Auto-generated method stub
		//System.out.println("current WOB : " + RunEnvironment.getInstance().getParameters().getDouble("currentSetWOB"));
		if (context.isConstantWOBincrease()) //if the WOB is increasing constantly
		{
			double weightOnBit = context.getWeightOnBit();
			double increasePerTick = RunEnvironment.getInstance().getParameters().getDouble("increasePerTick");
			if (weightOnBit<(30-increasePerTick))
				context.setWeightOnBit(weightOnBit+increasePerTick);
			else
				context.setWeightOnBit(30);
		}
		else //else the user sets it
		{
			double futureWOB = RunEnvironment.getInstance().getParameters().getDouble("currentSetWOB");
			// we check to avoid impossible values
			if (futureWOB < 0)
				futureWOB = 0;
			if (futureWOB > 30)
				futureWOB = 30;
			context.setWeightOnBit(futureWOB);
		}		
		System.out.println("current WOB : " + context.getWeightOnBit());
	}

}
