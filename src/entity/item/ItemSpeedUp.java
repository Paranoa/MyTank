package entity.item;


import java.awt.Graphics;

import util.Global;

import entity.tank.BasicTank;

public class ItemSpeedUp extends BasicItem {
	
	{
		
		ClassLoader loader = Global.loader;
		image = Global.toolKit.getImage(loader.getResource("images/item/speedup.png"));//getResource���ұ鵱ǰ���õ�classpathĿ¼

	}
	
	
	@Override
	public void affect(BasicTank tank) {
		tank.setXSpeed(tank.getXSpeed()+2);
		tank.setYSpeed(tank.getYSpeed()+2);
		
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(image, left, top,null);

	}
	
	
}
