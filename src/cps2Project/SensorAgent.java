package cps2Project;

import java.util.ArrayList;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class SensorAgent extends Agent{
	
	//informations on the sensor itself
	protected int IDSensorAgent;
	protected int neighborUp;
	protected int neighborDown;
	protected ContextCreator context;
	protected double dangerTemp;
	protected double criticalTemp;
	protected double shutdownTemp;
	
	//voting mechanisms variables
	protected int nbSensor;
	protected int voteResult; //0 if no vote result, 1 if true, -1 if false
	protected ArrayList<Boolean> voteList;
	protected ArrayList<Integer> voterIDList;
	protected boolean voting;
	protected boolean voteFinished;
	protected int nextVoteCountdown; //will be used to avoid a vote spam
	
	//measures
	protected double measuredDepth; 
	protected double trueDepth;	
	protected double temperature;

	public int getIDSensorAgent() {
		return IDSensorAgent;
	}

	public ArrayList<Boolean> getVoteList() {
		return voteList;
	}

	public ArrayList<Integer> getVoterIDList() {
		return voterIDList;
	}

	public int getVoteResult() {
		return voteResult;
	}

	public SensorAgent(int IDSensorAgent, int neighborUp, int neighborDown, int nbSensor, ContextCreator context, double measuredDepth, double dangerTemp, double criticalTemp, double shutdownTemp) {
		this.IDSensorAgent = IDSensorAgent;
		this.neighborUp = neighborUp;
		this.neighborDown = neighborDown;
		this.voting = false;
		this.voteResult = 0;
		this.nbSensor = nbSensor;
		this.context = context;
		this.measuredDepth = measuredDepth;
		this.dangerTemp = dangerTemp;
		this.criticalTemp = criticalTemp;
		this.shutdownTemp = shutdownTemp;
		this.nextVoteCountdown = 0;
	}

	@Override
	public void compute() {
		
		if (nextVoteCountdown > 0)
			nextVoteCountdown--;
		float speed = context.getDrillingSpeed();
		measuredDepth += speed;
		trueDepth = context.getTrueDepth(measuredDepth);
		temperature = context.getTemperatureFromTVD(trueDepth);
//		System.out.println("measuredDepth of Agent #" + IDSensorAgent + " is " + measuredDepth + " meters downhole");
//		System.out.println("trueDepth of Agent #" + IDSensorAgent + " is " + trueDepth + " meters downhole");
		System.out.println("temperature of Agent #" + IDSensorAgent + " is " + temperature + "�C");
		
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
				System.out.println("Agent #" + IDSensorAgent+ " is at "+ temperature+"�C of "+criticalTemp+" and is starting a vote!");
				startVote();				
				nextVoteCountdown = 20; //the agent cannot vote again before 20 ticks
			}
		}
		else if (temperature >= dangerTemp)
		{
			//TODO: send a message to the FA
		}
		
		
	}
	
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "voting", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void vote(SensorAgent sensorAgent) {
		if (validSender(sensorAgent.getIDSensorAgent()))
		{ //if we receive a voting request
			voteResult=0; //we forget the previous vote result
			ArrayList<Boolean> receivedVoteList = sensorAgent.getVoteList(); //we get the list
			ArrayList<Integer> receivedVoterIDList = sensorAgent.getVoterIDList(); //we get the list
			voteList = receivedVoteList;
			voterIDList = receivedVoterIDList;
			//we check if it is filled
			if (voteList.size()!=nbSensor && !voterIDList.contains(IDSensorAgent))//if it isn't finished and we haven't already voted, we update the vote
			{
				//TODO: decide the vote of the agent
				boolean vote;
				if (temperature>=criticalTemp)
					vote = true;
				else
					vote = false;
				voteList.add(vote);
				voterIDList.add(IDSensorAgent);
				//we pass the vote to the others
				System.out.println(voteList.toString());
				System.out.println(voterIDList.toString());
				voting = !voting;
			}
			else if (voteList.size()==nbSensor)//if everybody voted, we extract the result
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
					System.out.println("Vote result is true");
					voteResult = 1;
					voteFinished = !voteFinished;
				}
				else
				{
					System.out.println("Vote result is false");
					voteResult = -1;
					voteFinished = !voteFinished;
				}//we tell the results to the neighbors
			}
			else //else some people still need to vote, so we pass the list along
			{
				voting = !voting;
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
		voterIDList = new ArrayList<Integer>();
		voting = !voting;
	}

}
