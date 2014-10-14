package entity.obstacle;

import java.awt.Graphics;

import util.Global;

public class Meadow extends BasicObstacle {
	
	{   
	    throughable = true;
	    walkable = true;
	    destroyable = false;	

	    ClassLoader loader = Global.loader;
	    image = Global.toolKit.getImage(loader.getResource("images/obstacle/meadow.png"));
	}
	
	
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(image, left, top, null);
		
	}

}
