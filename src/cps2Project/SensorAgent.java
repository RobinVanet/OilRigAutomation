package cps2Project;

public class SensorAgent extends Agent{
	
	double temperature;
	int IDSensorAgent;
	
	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public int getIDSensorAgent() {
		return IDSensorAgent;
	}

	public void setIDSensorAgent(int iDSensorAgent) {
		IDSensorAgent = iDSensorAgent;
	}

	public SensorAgent(int IDSensorAgent) {
		this.IDSensorAgent = IDSensorAgent;
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub
		System.out.println("Agent "+ getIDSensorAgent()+" is breathing!");
		if (temperature<100)
		{
			//do nothing
		}
		else if(temperature<125)
		{
			//inform the field Agent
		}
		else
		{
			//take direct action, inform field agent
		}
		
	}

}
