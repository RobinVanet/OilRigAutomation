package cps2Project;

import java.util.ArrayList;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class SensorAgent extends Agent{
	
	protected double temperature;
	protected int IDSensorAgent;
	protected int neighborUp;
	protected int neighborDown;
	protected int nbSensor;
	protected int voteResult; //0 if no vote result, 1 if true, -1 if false
	protected ArrayList<Boolean> voteList;
	protected boolean increaseDownHoleActu; //will convey the command for the downhole actuator agent to increase power
	protected boolean voting;
	protected boolean voteFinished;
	
	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public int getIDSensorAgent() {
		return IDSensorAgent;
	}

	public ArrayList<Boolean> getVoteList() {
		return voteList;
	}

	public int getVoteResult() {
		return voteResult;
	}

	public SensorAgent(int IDSensorAgent, int neighborUp, int neighborDown, int nbSensor) {
		this.IDSensorAgent = IDSensorAgent;
		this.neighborUp = neighborUp;
		this.neighborDown = neighborDown;
		this.temperature = 0;
		this.increaseDownHoleActu = false;
		this.voting = false;
		this.voteResult = 0;
		this.nbSensor = nbSensor;
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub
		//System.out.println("Agent "+ getIDSensorAgent()+" is breathing!");
		//temperature = Math.random()*130;
		temperature++;
		if (IDSensorAgent==2 && temperature%10==0) //every 10 ticks, new vote
		{
			System.out.println("Starting a vote");
			startVote();			
		}
			
		if (IDSensorAgent ==2 && voteResult!=0)
			System.out.println("Vote result = "+voteResult);
		if (temperature<100)
		{
			//do nothing
		}
		else if(temperature<125)
		{
			//inform the field Agent
			//maybe use a @watch?
		}
		else
		{
			//take direct action, inform field agent
			//TODO:in the case the downhole actuator is already at full power, someone has to send the message to the field agent : but SensorAgent or ActuatorAgent? AA seems more logic (no tracking from SA, if in the AA)
		}
		
	}
	
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "voting", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void vote(SensorAgent sensorAgent) {
		if (validSender(sensorAgent.getIDSensorAgent()))
		{ //if we receive a voting request
			voteResult=0; //we forget the previous vote result
			ArrayList<Boolean> receivedVoteList = sensorAgent.getVoteList(); //we get the list
			voteList = receivedVoteList;
			//we check if it is filled
			if (voteList.size()!=nbSensor)//if it isn't, we update the vote
			{
				//TODO: decide the vote of the agent
//				boolean vote = true; //for now it will be true
				boolean vote = (Math.random() < 0.5); //random vote
				System.out.println(voteList.toString());
				voteList.add(vote);
				//we pass the vote to the others
				voting = !voting;
			}
			else //else, we extract the result
			{
				int trueVotes = 0;
				int falseVotes = 0;
				for (int i = 0; i< voteList.size();i++) //we count all the votes
				{
					if (voteList.get(i)==true)
						trueVotes++;
					else
						falseVotes++;
				}
				if (trueVotes>falseVotes)
				{
					voteResult = 1;
					voteFinished = !voteFinished;
				}
				else
				{
					voteResult = -1;
					voteFinished = !voteFinished;
				}//we tell the results to the neighbors
			}					
		}
	}
	
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "voteFinished", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getVoteResult(SensorAgent sensorAgent) {
		if (validSender(sensorAgent.getIDSensorAgent()))
		{ //if we receive a valid answer from a neighbor
			int vote = sensorAgent.getVoteResult();
			if (vote != voteResult) //if we didn't already have the vote result
			{
				voteResult = vote; //we take the result and pass it along
				voteFinished = !voteFinished;
			}
			//else we do nothing because the information has already spread
		}
	}
	
	public boolean validSender(int IDSender) //know if the sender is a neighbor
	{
		if (IDSender == neighborUp || IDSender == neighborDown)
			return true;
		else //if the sender is the receiver itself or a distant Agent
			return false;
	}
	
	public void startVote() //when we start a vote, we create a new ArrayList of boolean that we fill with nulls
	{
		voteList = new ArrayList<Boolean>();
		voting = !voting;
	}

}
