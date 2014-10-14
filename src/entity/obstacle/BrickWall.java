package entity.obstacle;

import java.awt.Graphics;
import util.Global;


public class BrickWall extends BasicObstacle {

	
	{		 
	    throughable = false;
	    walkable = false;
	    destroyable = true;
	    
		ClassLoader loader = Global.loader;
	    image = Global.toolKit.getImage(loader.getResource("images/obstacle/brickwall.png"));
	}
	
	
	public void draw(Graphics g){
		g.drawImage(image,left, top, null);	
	}
	
	
	

	
	
	
}
