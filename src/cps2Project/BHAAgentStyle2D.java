package cps2Project;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

/**
 * Class used to give a shape to the BHA Agents
 * 
 * @author Robin Vanet
 *
 */
public class BHAAgentStyle2D extends DefaultStyleOGL2D{
	
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
	    if (spatial == null) {
	    	int depthGoal = RunEnvironment.getInstance().getParameters().getInteger("depthGoal");
	    	int BHAAgentSize = 15*(1000/depthGoal);
	    	System.out.println("agent size = "+BHAAgentSize);
	    	if (BHAAgentSize<=0)
	    		BHAAgentSize=1;
	    	spatial = shapeFactory.createRectangle(15, BHAAgentSize);
	    }
	    return spatial;
	  }


}
