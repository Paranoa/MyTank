package entity.bullet;

import java.awt.Graphics;
import java.awt.Image;

import util.Global;

public class PlayerBullet extends BasicBullet {
	{
		xSpeed = 15;
		ySpeed = 15;
		pow = 10;
		coolDown = 400;
		
		bulletWidth = 10;
		bulletHeight = 10;
		
		bulletImages = new Image[1];
		ClassLoader loader = Global.loader;
		bulletImages[0] = Global.toolKit.getImage(loader.getResource("images/bullet/bullet-p.png"));
		
		
	}
	@Override
	protected void drawBullet(Graphics g) {	
		g.drawImage(bulletImages[0],left,top,null);	

	}

}
