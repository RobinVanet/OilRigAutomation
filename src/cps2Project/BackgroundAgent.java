package cps2Project;

/**
 * This class will be used to color the tiles of the simulation
 * 
 * @author Robin Vanet
 *
 */
public class BackgroundAgent extends Agent{
	
	//represents what the squarre represents:
	// 0 = air; 1 = ground
	private boolean content;
	//the coordinates
	private int x,y;

	@Override
	public void compute() {
	}

	public boolean getContent() {
		return content;
	}

	public void setContent(boolean content) {
		this.content = content;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	

}
