package entity.item;


import java.awt.Graphics;

import util.Global;

import entity.tank.BasicTank;
import entity.tank.PlayerBulletCreater;

public class ItemSupreMode extends BasicItem {

	{
		
		ClassLoader loader = Global.loader;
		image = Global.toolKit.getImage(loader.getResource("images/item/supermode.png"));//getResource会找遍当前设置的classpath目录

	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(image, left, top,null);

	}
	
	@Override
	public void affect(final BasicTank tank) {
		PlayerBulletCreater creater=(PlayerBulletCreater)tank.getBulletCreater();
		creater.setSuperMode(true);
		creater.setSuperModeTime(10*1000);
		
	}
	
	
	
	
	
}
