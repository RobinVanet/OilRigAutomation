package cps2Project;

/**
 * This class is only used to represent the goal the agents must reach to end the simulation.
 * 
 * @author Robin Vanet
 *
 */
public class GoalAgent  extends Agent{
	
	//the goal to reach
	protected int goalDepth;
	
	@Override
	public void compute() {
		// TODO Auto-generated method stub
		
	}
	
	public void setGoalDepth(int goalDepth)
	{
		this.goalDepth=goalDepth;
	}

}
