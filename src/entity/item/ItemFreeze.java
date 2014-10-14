package entity.item;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import listener.ExplodeListener;

import util.Global;

import entity.explode.Explode;
import entity.tank.BasicTank;

public class ItemFreeze extends BasicItem {
	
	
	{
		ClassLoader loader = Global.loader;
		image = Global.toolKit.getImage(loader.getResource("images/item/freeze.png"));
	}
	private class Frozen extends Explode{ //这个冻结的效果做成一个继承了explode的内部类
		private BasicTank frozenTank;
		
		{
			ClassLoader loader = Global.loader;
			images = new Image[10];
			images[0] = Global.toolKit.getImage(loader.getResource("images/explode/frozen0.png"));
			images[1] = Global.toolKit.getImage(loader.getResource("images/explode/frozen1.png"));
			images[2] = Global.toolKit.getImage(loader.getResource("images/explode/frozen2.png"));
			images[3] = Global.toolKit.getImage(loader.getResource("images/explode/frozen3.png"));
			images[4] = Global.toolKit.getImage(loader.getResource("images/explode/frozen4.png"));
			images[5] = Global.toolKit.getImage(loader.getResource("images/explode/frozen5.png"));
			images[6] = Global.toolKit.getImage(loader.getResource("images/explode/frozen6.png"));
			images[7] = Global.toolKit.getImage(loader.getResource("images/explode/frozen7.png"));
			images[8] = Global.toolKit.getImage(loader.getResource("images/explode/frozen8.png"));
			images[9] = Global.toolKit.getImage(loader.getResource("images/explode/frozen9.png"));
		}
		
		Frozen(int left,int top,int seconds,ExplodeListener listener,BasicTank tank){
			this.left = left;
			this.top = top;
			explodeCount = 1000*seconds/Global.GAMESPEED;
			this.listener = listener;
			this.frozenTank = tank;

		}
		
		@Override
		public void draw(Graphics g) {
			
			g.drawImage(images[explodeIndex<=9?explodeIndex:9], left, top,null);//冻结达到最大就一直显示最后一张图片
			explodeIndex++;
			
			//爆炸结束后移除
			if(!ItemFreeze.this.listener.getTanks().contains(frozenTank)||explodeIndex>explodeCount){
				//如果被冻住的坦克已经不存在或者冻结时间结束
				listener.removeExplode(this);
			}
		}
		
		
	}
	
	
	
	@Override
	public void affect(BasicTank tank) {
		final ArrayList<BasicTank> tanks = listener.getTanks();
		
		for(BasicTank t:tanks){
			if(t!=listener.getPlayerTank()){
				listener.getExplodes().add(new Frozen(t.getLeft(),t.getTop(),5,listener,t));
				t.setComputerControl(false);
			}
		}
		

		new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for(BasicTank t:tanks){
					t.setComputerControl(true);
				}				
			}	
		}).start();
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(image, left, top,null);
	}

}
