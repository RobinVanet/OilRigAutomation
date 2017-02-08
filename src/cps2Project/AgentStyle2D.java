package cps2Project;

import java.awt.Color;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

/**
 * Class used to give a color and a shape to the tiles of the graphic.
 * 
 * @author Robin Vanet
 *
 */
public class AgentStyle2D extends DefaultStyleOGL2D {
	
	@Override
	public Color getColor(Object o) {
		if (o instanceof BackgroundAgent)
		{
			if (((BackgroundAgent) o).getContent()==true)
			{
				Color customColor = new Color(128,96,22);
				return customColor;
			}
			else
				return Color.CYAN;
		}
		else
			return Color.BLACK;
	}
	
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
	    if (spatial == null) {
	      spatial = shapeFactory.createRectangle(15, 15);
	    }
	    return spatial;
	  }


}
