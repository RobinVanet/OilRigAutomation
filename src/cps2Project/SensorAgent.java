package cps2Project;

public class SensorAgent extends Agent{
	
	protected double temperature;
	protected int IDSensorAgent;
	protected boolean danger; //will convey the message that the sensor feels a high temperature
	protected boolean actionTaken; //will convey the message that the temperature was so high, the sensor agent did something.
	protected boolean increaseDownHoleActu; //will convey the command for the downhole actuator agent to increase power
	
	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public int getIDSensorAgent() {
		return IDSensorAgent;
	}

	public SensorAgent(int IDSensorAgent) {
		this.IDSensorAgent = IDSensorAgent;
		this.temperature = 0;
		this.danger = false;
		this.actionTaken = false;
		this.increaseDownHoleActu = false;
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub
		//System.out.println("Agent "+ getIDSensorAgent()+" is breathing!");
		temperature = Math.random()*130;
		
		if (temperature<100)
		{
			//do nothing
		}
		else if(temperature<125)
		{
			//inform the field Agent
			//maybe use a @watch?
			System.out.println("SA #" + IDSensorAgent + " has a temp of : " + temperature + "°C");
			danger = !danger;
		}
		else
		{
			//take direct action, inform field agent
			//TODO:in the case the downhole actuator is already at full power, someone has to send the message to the field agent : but SensorAgent or ActuatorAgent? AA seems more logic (no tracking from SA, if in the AA)
			System.out.println("SA #" + IDSensorAgent + " has a temp of : " + temperature + "°C");
			increaseDownHoleActu = !increaseDownHoleActu;
			actionTaken = !actionTaken;
		}
		
	}

}
