package cps2Project;

public class ActuatorAgent extends Agent{
	
	double power; //for now, maybe we can represent the actuator power by a variable (e.g. from 0 to 100)
	int IDActuatorAgent;
	boolean downhole; //we can represent the place of the actuator with this boolean (true = downhole, false = uphole)
	
	public ActuatorAgent(int IDActuatorAgent,boolean downhole) {
		this.IDActuatorAgent = IDActuatorAgent;
		this.downhole = downhole;
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub
		
	}

}
