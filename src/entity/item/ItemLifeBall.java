package entity.item;


import java.awt.Graphics;

import util.Global;

import entity.tank.BasicTank;

public class ItemLifeBall extends BasicItem {

	{
		
		ClassLoader loader = Global.loader;
		image = Global.toolKit.getImage(loader.getResource("images/item/lifeball.png"));//getResource会找遍当前设置的classpath目录
	
	}
	
	@Override
	public void affect(BasicTank tank) {
		 tank.setCurrentLife(tank.getCurrentLife()+40);
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(image, left, top,null);
		
	}

}
