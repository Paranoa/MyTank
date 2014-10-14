package entity.item;

import java.awt.Graphics;
import java.util.ArrayList;

import util.Global;

import entity.tank.BasicTank;

public class ItemBomb extends BasicItem{
	{	
		ClassLoader loader = Global.loader;
		image = Global.toolKit.getImage(loader.getResource("images/item/bomb.png"));
	}
	
	@Override
	public void affect(BasicTank tank) {
		ArrayList<BasicTank> tanks = listener.getTanks();
		
		for(BasicTank t:tanks){
			if(t!=listener.getPlayerTank()){
				listener.getExplodes().add(t.tankExplode(listener));
				listener.removeTank(t);
			}
		}
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(image, left, top,null);
	}

}
