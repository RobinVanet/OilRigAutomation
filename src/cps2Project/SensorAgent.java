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
	protected boolean actionAlreadyTaken;
	
	//measures
	protected double measuredDepth; 
	protected double trueDepth;	
	protected double temperature;
	
	//message mechanisms variables
	protected boolean messageFATooHot = false;

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
		actionAlreadyTaken = false; //this boolean is a quickfix to avoid having several action taken from the same vote. With that, we can only have one vote/tick (1 vote/min)
		if (nextVoteCountdown > 0)
			nextVoteCountdown--;
		double speed = context.getDrillingSpeed();
		measuredDepth += speed;
		trueDepth = context.getTrueDepth(measuredDepth);
		temperature = context.getTemperatureFromTVD(trueDepth);
//		System.out.println("measuredDepth of Agent #" + IDSensorAgent + " is " + measuredDepth + " meters downhole");
//		System.out.println("trueDepth of Agent #" + IDSensorAgent + " is " + trueDepth + " meters downhole");
//		System.out.println("temperature of Agent #" + IDSensorAgent + " is " + temperature + "°C");
		
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
			messageFATooHot = !messageFATooHot;
		}
	}
	
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "voting", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void vote(SensorAgent sensorAgent) {
		if (validSender(sensorAgent.getIDSensorAgent()))
		{ //if we receive a voting request
			voteResult=0; //we forget the previous vote result
			voteList = sensorAgent.getVoteList(); //we get the list
			voterIDList = sensorAgent.getVoterIDList(); //we get the list
			//we check if it is filled
			if (voteList.size()!=nbSensor && !voterIDList.contains(IDSensorAgent))//if it isn't finished and we haven't already voted, we update the vote
			{
				boolean vote;
				if (temperature>=criticalTemp)
					vote = true;
				else
					vote = false;
				voteList.add(vote);
				voterIDList.add(IDSensorAgent);
				//we pass the vote to the others
//				System.out.println(voteList.toString());
//				System.out.println(voterIDList.toString());
				voting = !voting;
			}
			else if (voteList.size()==nbSensor)//if everybody voted, we extract the result
			{
				extractVoteResult();
			}
			else //else some people still need to vote, so we pass the list along
			{
//				voting = !voting;
			}
		}
	}
	
	public void extractVoteResult(){
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
	
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "voteFinished", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getVoteResult(SensorAgent sensorAgent) {
		if (validSender(sensorAgent.getIDSensorAgent()))
		{ //if we receive a valid answer from a neighbor
//			System.out.println("Agent " + IDSensorAgent+ " received the result "+sensorAgent.getVoteResult()+" from Agent"+sensorAgent.getIDSensorAgent());
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
	
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "voteResult", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void actionTaken(SensorAgent sensorAgent) {
		if (sensorAgent.getIDSensorAgent() == IDSensorAgent && IDSensorAgent == 2 && voteResult ==1 && actionAlreadyTaken == false)
		{ //when the most downhole SensorAgent realise the vote is a yes
			actionAlreadyTaken = true;
//			System.out.println("Vote said : action taken!");
//			RunEnvironment.getInstance().endRun();
			context.lowerDrillingSpeed();
		}
	}
	
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "messageFATooHot", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void messageFATooHot(SensorAgent sensorAgent)
	{
		if (sensorAgent.getIDSensorAgent() == (IDSensorAgent-1)) //if we receive a message from the SA under
		{
			messageFATooHot = !messageFATooHot;
		}
	}
	
//	public void messageFASlowDown()
//	{
//
//	}
}
