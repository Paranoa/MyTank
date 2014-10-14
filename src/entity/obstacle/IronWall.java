package entity.obstacle;

import java.awt.Graphics;

import util.Global;

public class IronWall extends BasicObstacle {
	
	{  
	    throughable = false;
	    walkable = false;
	    destroyable = false;
	    
		ClassLoader loader = Global.loader;
	    image = Global.toolKit.getImage(loader.getResource("images/obstacle/ironwall.png"));
	}
	
	
	
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(image,left,top,null);	
	}

}
