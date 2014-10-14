package entity.item;


import java.awt.Graphics;

import util.Global;

import entity.bullet.SuperBullet;
import entity.tank.BasicTank;
import entity.tank.PlayerBulletCreater;

public class ItemSuperBullet extends BasicItem {
	
	{
		
		ClassLoader loader = Global.loader;
		image = Global.toolKit.getImage(loader.getResource("images/item/superbullet.png"));//getResource���ұ鵱ǰ���õ�classpathĿ¼

	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(image, left, top,null);

	}
	
	@Override
	public void affect(final BasicTank tank) {
		PlayerBulletCreater factory=(PlayerBulletCreater)tank.getBulletCreater();
		factory.setBullet(new SuperBullet());
		factory.setBullteTime(10*1000);
	}



		


}
