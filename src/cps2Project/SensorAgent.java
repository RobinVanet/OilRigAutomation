package cps2Project;

import java.util.ArrayList;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.continuous.ContinuousSpace;

/**
 * The class used to represent the sensor array in each equipment (one agent /
 * equipment). Their goal is the preservation of the equipment through the
 * control of the temperature, but with the minimal cost in time to drill.
 * 
 * @author Robin Vanet
 *
 */
public class SensorAgent extends Agent {

	/*--------------VARIABLES-----------------*/
	// informations on the sensor itself
	protected int IDSensorAgent;
	protected int neighborUp;
	protected int neighborDown;
	protected ContextCreator context;
	protected double dangerTemp;
	protected double criticalTemp;
	protected double shutdownTemp;

	// voting mechanisms variables
	protected boolean voteEnabled;
	protected int nbSensor;
	protected int voteResult; // 0 if no vote result, 1 if true, -1 if false
	protected ArrayList<Boolean> voteList;
	protected ArrayList<Integer> voterIDList;
	protected boolean voting;
	protected boolean voteFinished;
	protected boolean actionAlreadyTaken;

	// measures
	protected double measuredDepth;
	protected double trueDepth;
	protected double temperature;

	// message mechanisms variables
	protected boolean messageFATooHot = false;
	protected boolean messageFASlowDown = false;
	protected boolean speedDrill = false;

	/*--------------GETTERS AND SETTERS-----------------*/
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

	/*--------------CONSTRUCTOR-----------------*/
	public SensorAgent(ContinuousSpace<Agent> space, int IDSensorAgent, int neighborUp, int neighborDown, int nbSensor,
			ContextCreator context, double measuredDepth, double dangerTemp, double criticalTemp, double shutdownTemp,
			boolean voteEnabled) {
		this.space = space;
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
		this.voteEnabled = voteEnabled;
	}

	/*--------------FUNCTIONS-----------------*/
	/**
	 * Method used at each tick (as defined in the Agent class)
	 */
	@Override
	public void compute() {
		actionAlreadyTaken = false; // this boolean is a quickfix to avoid
									// having several action taken from the same
									// vote. With that, we can only have one
									// vote/tick (1 vote/min)
		double speed = context.getSpeed();
		measuredDepth += speed;
		trueDepth = context.getTrueDepth(measuredDepth);
		temperature = context.getTemperatureFromTVD(trueDepth);

		space.moveTo(this, 5, context.getYCoordinates(trueDepth)); // moving the
																	// agent on
																	// the
																	// display

		// can be used to display what the agent knows each tick.
		// System.out.println("measuredDepth of Agent #" + IDSensorAgent + " is
		// " + measuredDepth + " meters downhole");
		// System.out.println("trueDepth of Agent #" + IDSensorAgent + " is " +
		// trueDepth + " meters downhole");
		// System.out.println("temperature of Agent #" + IDSensorAgent + " is "
		// + temperature + "°C");

		if (temperature >= shutdownTemp) {
			// end the simulation
			double ticks = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
			System.out.println("Failure! Agent #" + IDSensorAgent + " overheated at " + trueDepth + "m in " + ticks
					+ " minutes for temperature " + temperature + " and dangerTemp " + dangerTemp);
			RunEnvironment.getInstance().endRun();
		} else if (temperature >= criticalTemp) {
			// System.out.println("Agent #" + IDSensorAgent+ " is at "+
			// temperature+"°C of "+criticalTemp+" and is starting a vote!");
			if (voteEnabled) {
				startVote();
			}
		} else if (temperature >= dangerTemp) {
			// send a message to the FA
			messageFATooHot = !messageFATooHot;
		}

		// grid.moveTo((Agent)this, (int)trueDepth,0);
	}

	/**
	 * The function used for the voting mechanism. Activated every time an agent
	 * requests the others to vote (with the "voting" boolean), each other
	 * sensors check if the asking one is a neighbor, and if he is they either
	 * vote is not everyone voted, or extract the result.
	 * 
	 * @param sensorAgent
	 *            : the sensorAgent sending the message. Used to check if the
	 *            agent is a valid one (if it is a neighbor).
	 */
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "voting", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void vote(SensorAgent sensorAgent) {
		if (validSender(sensorAgent.getIDSensorAgent())) { // if we receive a
															// voting request
															// from a valid
															// neighbor
			voteResult = 0; // we forget the previous vote result
			// get the current lists in the memory of the requesting neighbor
			voteList = sensorAgent.getVoteList();
			voterIDList = sensorAgent.getVoterIDList();
			// we check if it is filled
			if (voteList.size() != nbSensor && !voterIDList.contains(IDSensorAgent))// if
																					// it
																					// isn't
																					// finished
																					// and
																					// we
																					// haven't
																					// already
																					// voted,
																					// we
																					// update
																					// the
																					// vote
			{
				boolean vote;
				if (temperature >= criticalTemp)
					vote = true;
				else
					vote = false;
				voteList.add(vote);
				voterIDList.add(IDSensorAgent);
				// we pass the vote to the others
				voting = !voting;
			} else if (voteList.size() == nbSensor)// if everybody voted, we
													// extract the result
			{
				extractVoteResult();
			}
		}
	}

	/**
	 * Extract the result of the vote once the list is full (everyone voted).
	 * The presence of a veto or not can be set here.
	 */
	public void extractVoteResult() {
		int trueVotes = 0;
		int falseVotes = 0;
		for (int i = 0; i < voteList.size(); i++) // we count all the votes
		{
			if (voteList.get(i) == true)
				trueVotes++;
			else
				falseVotes++;
		}
		if (trueVotes > falseVotes) {
			voteResult = 1;
			voteFinished = !voteFinished;
		} else {
			voteResult = -1;
			voteFinished = !voteFinished;
		} // we tell the results to the neighbors
	}

	/**
	 * Get the result of a vote from another sensor agent, then passing it after
	 * checking if the other agent is a neighbor.
	 * 
	 * @param sensorAgent
	 *            : the sensorAgent sending the message. Used to check if the
	 *            agent is a valid one (if it is a neighbor).
	 */
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "voteFinished", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getVoteResult(SensorAgent sensorAgent) {
		if (validSender(sensorAgent.getIDSensorAgent())) { // if we receive a
															// valid answer from
															// a neighbor
			// System.out.println("Agent " + IDSensorAgent+ " received the
			// result "+sensorAgent.getVoteResult()+" from
			// Agent"+sensorAgent.getIDSensorAgent());
			int vote = sensorAgent.getVoteResult();
			if (vote != voteResult) // if we didn't already have the vote result
			{
				voteResult = vote; // we take the result and pass it along
				voteFinished = !voteFinished;
			}
			// else we do nothing because the information has already spread
		}
	}

	/**
	 * Check if the sender of a message is a neighbor.
	 * 
	 * @param IDSender
	 *            : the ID of the sensor agent transmiting the message.
	 * @return true if the agent is a neighbor, else if it is self or a distant
	 *         one.
	 */
	public boolean validSender(int IDSender) // know if the sender is a neighbor
	{
		if (IDSender == neighborUp || IDSender == neighborDown)
			return true;
		else // if the sender is the receiver itself or a distant Agent
			return false;
	}

	/**
	 * Clean the internal memory of previous vote to avoid results from another
	 * vote to spill in the new one, then requesting the others to vote.
	 */
	public void startVote() // when we start a vote, we create a new ArrayList
							// of boolean that we fill with nulls
	{
		voteList = new ArrayList<Boolean>();
		voterIDList = new ArrayList<Integer>();
		voting = !voting;
	}

	/**
	 * Every time the field "voteResult" of one Sensor Agent changes, this
	 * function activates. The if only works if the Sensor Agent is the most
	 * downhole (id = 2) in that case, it will detect its own change and will
	 * lower the drilling speed.
	 * 
	 * @param sensorAgent
	 *            : the sensorAgent sending the message. Used to check if the
	 *            agent is a valid one (if it is a neighbor).
	 */
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "voteResult", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void actionTaken(SensorAgent sensorAgent) {
		if (sensorAgent.getIDSensorAgent() == IDSensorAgent && IDSensorAgent == 2 && voteResult == 1
				&& actionAlreadyTaken == false) { // when the most downhole
													// SensorAgent realise the
													// vote is a yes
			actionAlreadyTaken = true;
			context.lowerDrillingSpeed();// lower sensor agent is working as the
											// downhole actuator
			messageFASlowDown = !messageFASlowDown; // we send a message up-hole
													// to notify that the drill
													// was slowed down
		}
	}

	/**
	 * Send the message "temperature is getting too hot for one equipment" to
	 * everyone, to reach the FA. Only the direct upper level will listen and
	 * relay the call.
	 * 
	 * @param sensorAgent
	 *            : the sensorAgent sending the message. Used to check if the
	 *            agent is a valid one (if it is a neighbor).
	 */
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "messageFATooHot", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void messageFATooHot(SensorAgent sensorAgent) {
		if (sensorAgent.getIDSensorAgent() == (IDSensorAgent - 1)) // if we
																	// receive a
																	// message
																	// from the
																	// SA under
		{
			messageFATooHot = !messageFATooHot;
		}
	}

	/**
	 * Send the message "the drill was slowed down" to everyone, to reach the
	 * FA. Only the direct upper level will listen and relay the call.
	 * 
	 * @param sensorAgent
	 *            : the sensorAgent sending the message. Used to check if the
	 *            agent is a valid one (if it is a neighbor).
	 */
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "messageFASlowDown", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void messageFASlowDown(SensorAgent sensorAgent) {
		if (sensorAgent.getIDSensorAgent() == (IDSensorAgent - 1)) // if we
																	// receive a
																	// message
																	// from the
																	// SA under
		{
			messageFASlowDown = !messageFASlowDown;
		}
	}

	// No need for this function as the the field agent should be able to
	// communicate with context directly
	/**
	 * Watch UpperSensorAgent for the order to increase the drilling speed. Only
	 * the next sensor in the array will transmit the message, the others will
	 * ignore it.
	 * 
	 * @param upperSensorAgent
	 */
	@Watch(watcheeClassName = "cps2Project.UpperSensorAgent", watcheeFieldNames = "speedDrill", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void messageSASpeedDrill(UpperSensorAgent upperSensorAgent) {
		if (upperSensorAgent.getIDSensorAgent() == (IDSensorAgent + 1)) // if
																		// the
																		// sender
																		// is
																		// valid
		{
			if (IDSensorAgent == 2) // if we are the most down-hole
			{
				context.increaseDrillingSpeed();
			} else {
				speedDrill = !speedDrill;
			}
		}
	}

	// No need for this function as the the field agent should be able to
	// communicate with context directly
	/**
	 * Watch other SensorAgents for the order to increase the drilling speed.
	 * Only the next sensor in the array will transmit/use the message, the
	 * others will ignore it.
	 * 
	 * @param upperSensorAgent
	 */
	@Watch(watcheeClassName = "cps2Project.SensorAgent", watcheeFieldNames = "speedDrill", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void messageSASpeedDrill(SensorAgent sensorAgent) {
		if (sensorAgent.getIDSensorAgent() == (IDSensorAgent + 1)) {
			if (IDSensorAgent == 2) // if we are at the most down-hole agent
			{
				context.increaseDrillingSpeed();
			} else {
				speedDrill = !speedDrill;
			}
		}
	}

}
