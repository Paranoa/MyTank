package entity.tank;

import java.awt.Graphics;
import java.awt.Image;


import listener.TankListener;

import util.Global;

public class PlayerTank extends BasicTank {
	
	{
		tankWidth = 40;
		tankHeight = 40;
		
		life = 80;
		
		xSpeed = 7;
		ySpeed = 7;	
		
		ClassLoader loader = Global.loader;

		tankImages = new Image[8];
		tankImages[0] = Global.toolKit.getImage(loader.getResource("images/playerTank/tank0.png"));
		tankImages[1] = Global.toolKit.getImage(loader.getResource("images/playerTank/tank1.png"));
		tankImages[2] = Global.toolKit.getImage(loader.getResource("images/playerTank/tank2.png"));
		tankImages[3] = Global.toolKit.getImage(loader.getResource("images/playerTank/tank3.png"));
		tankImages[4] = Global.toolKit.getImage(loader.getResource("images/playerTank/tank4.png"));
		tankImages[5] = Global.toolKit.getImage(loader.getResource("images/playerTank/tank5.png"));
		tankImages[6] = Global.toolKit.getImage(loader.getResource("images/playerTank/tank6.png"));
		tankImages[7] = Global.toolKit.getImage(loader.getResource("images/playerTank/tank7.png"));

		explodeImages = new Image[10];
		explodeImages[0] = Global.toolKit.getImage(loader.getResource("images/explode/explode0.png"));
		explodeImages[1] = Global.toolKit.getImage(loader.getResource("images/explode/explode1.png"));
		explodeImages[2] = Global.toolKit.getImage(loader.getResource("images/explode/explode2.png"));
		explodeImages[3] = Global.toolKit.getImage(loader.getResource("images/explode/explode3.png"));
		explodeImages[4] = Global.toolKit.getImage(loader.getResource("images/explode/explode4.png"));
		explodeImages[5] = Global.toolKit.getImage(loader.getResource("images/explode/explode5.png"));
		explodeImages[6] = Global.toolKit.getImage(loader.getResource("images/explode/explode6.png"));
		explodeImages[7] = Global.toolKit.getImage(loader.getResource("images/explode/explode7.png"));
		explodeImages[8] = Global.toolKit.getImage(loader.getResource("images/explode/explode8.png"));
		explodeImages[9] = Global.toolKit.getImage(loader.getResource("images/explode/explode9.png"));
		
	}
	@Override
	public void setComputerControl(boolean computerControl) {
		
	}
	
	public PlayerTank(){
		currentLife=life;
	}
	
	public PlayerTank(int left, int top, TankListener listener,PlayerBulletCreater bulletCreater,int team) {
		this.left = left;
		this.top = top;
		this.listener = listener;
		this.bulletCreater = bulletCreater;
		this.team=team;
		
		currentLife = life;
	}
	

	
	@Override
	public void drawTank(Graphics g) {	
		
		switch(tankDirection){
		case U:
			g.drawImage(tankImages[0],left,top,null);
			break;
		case D:
			g.drawImage(tankImages[1],left,top,null);
			break;
		case L:
			g.drawImage(tankImages[2],left,top,null);
			break;
		case R:
			g.drawImage(tankImages[3],left,top,null);
			break;
		case UL:
			g.drawImage(tankImages[4],left,top,null);
			break;
		case UR:
			g.drawImage(tankImages[5],left,top,null);
			break;
		case DL:
			g.drawImage(tankImages[6],left,top,null);
			break;
		case DR:
			g.drawImage(tankImages[7],left,top,null);
			break;	
		}
		drawLifeBar(g);
	}
	
	
	
	
	private void drawLifeBar(Graphics g){
		int liftBarBorder = 15;	//Ѫ����̹�˵�top�������
		
		int lifeBarWidth = 40;
		int lifeBarHeight = 10;
		
		g.setColor(Global.TLIFEBARBC);//���������
		g.drawRect(left, top-liftBarBorder, lifeBarWidth, lifeBarHeight);
		g.setColor(Global.TLIFEBARC);//��������ɫ
		g.fillRect(left, top-liftBarBorder, (int)(lifeBarWidth*(double)currentLife/(double)life), lifeBarHeight);
		
	}
	


}
