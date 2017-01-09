package cps2Project;

public class SensorAgent extends Agent{
	
	protected double temperature;
	protected int IDSensorAgent;
	protected boolean danger; //will convey the message that the sensor feels a high temperature
	protected boolean actionTaken; //will convey the message that the temperature was so high, the sensor agent did something.
	
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
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub
		//System.out.println("Agent "+ getIDSensorAgent()+" is breathing!");
		temperature = Math.random()*130;
		System.out.println("SA #" + IDSensorAgent + " has a temp of : " + temperature + "°C");
		if (temperature<100)
		{
			//do nothing
		}
		else if(temperature<125)
		{
			//inform the field Agent
			//maybe use a @watch?
			danger = !danger;
		}
		else
		{
			//take direct action, inform field agent
			actionTaken = !actionTaken;
		}
		
	}

}
