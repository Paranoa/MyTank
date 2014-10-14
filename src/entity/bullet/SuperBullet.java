package entity.bullet;
import java.awt.Graphics;
import java.awt.Image;

import util.Global;


public class SuperBullet extends BasicBullet{
	
	{
		xSpeed = 22;
		ySpeed = 22;
		pow = 20;
		coolDown = 300;
		
		bulletWidth = 15;
		bulletHeight = 15;
		
		
		ClassLoader loader = Global.loader;
		bulletImages = new Image[8];
		bulletImages[0] = Global.toolKit.getImage(loader.getResource("images/bullet/bullet0.png"));
		bulletImages[1] = Global.toolKit.getImage(loader.getResource("images/bullet/bullet1.png"));
		bulletImages[2] = Global.toolKit.getImage(loader.getResource("images/bullet/bullet2.png"));
		bulletImages[3] = Global.toolKit.getImage(loader.getResource("images/bullet/bullet3.png"));
		bulletImages[4] = Global.toolKit.getImage(loader.getResource("images/bullet/bullet4.png"));
		bulletImages[5] = Global.toolKit.getImage(loader.getResource("images/bullet/bullet5.png"));
		bulletImages[6] = Global.toolKit.getImage(loader.getResource("images/bullet/bullet6.png"));
		bulletImages[7] = Global.toolKit.getImage(loader.getResource("images/bullet/bullet7.png"));
		
		
	}
	
	
	//--------------------------------------------------------------------------------------
	

	@Override
	protected void drawBullet(Graphics g) { 
		
		switch(direction){
		case U:
			g.drawImage(bulletImages[0],left,top,null);
			break;
		case D:
			g.drawImage(bulletImages[1],left,top,null);
			break;
		case L:
			g.drawImage(bulletImages[2],left,top,null);
			break;
		case R:
			g.drawImage(bulletImages[3],left,top,null);
			break;
		case UL:
			g.drawImage(bulletImages[4],left,top,null);
			break;
		case UR:
			g.drawImage(bulletImages[5],left,top,null);
			break;
		case DL:
			g.drawImage(bulletImages[6],left,top,null);
			break;
		case DR:
			g.drawImage(bulletImages[7],left,top,null);
			break;	
		}
		
		
	}
	


}
