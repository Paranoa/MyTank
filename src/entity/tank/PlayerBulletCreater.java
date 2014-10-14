package entity.tank;

import java.util.ArrayList;

import entity.bullet.BasicBullet;
import entity.bullet.PlayerBullet;
import listener.BulletListener;
import util.Direction;
import util.Global;

public class PlayerBulletCreater extends BaseBulletCreater{//由这个类来生产子弹
	
	
	private boolean superMode = false;
	
	private int bullteTime;
	private int superModeTime;
	//----------------------------------------------------------
	public PlayerBulletCreater(){}
	public PlayerBulletCreater(BasicBullet bullet,BulletListener bulletListener){
		
		super(bullet,bulletListener);
	}
	
	
	//----------------------------------------------------------
	
	
	
	public boolean isSuperMode() {
		return superMode;
	}
	public void setSuperMode(boolean superMode) {
		this.superMode = superMode;
	}
	public int getBullteTime() {
		return bullteTime;
	}
	public void setBullteTime(int bullteTime) {
		this.bullteTime = bullteTime;
	}
	
	public int getSuperModeTime() {
		return superModeTime;
	}
	public void setSuperModeTime(int superModeTime) {
		this.superModeTime = superModeTime;
	}
	
	
	
	
	
	//-----------------------------------------------------------
	


	public void timeTrigger(){
		super.timeTrigger();

		bullteTime-=Global.GAMESPEED;
		superModeTime-=Global.GAMESPEED;
		
		if(bullteTime<=0){
			bullet = new PlayerBullet();
		}	
		if(superModeTime<=0){
			superMode = false;
		}
		
		
		
	}




	public ArrayList<BasicBullet>  getNewBullets(BasicTank tank){
		
		
		if(isCoolDown()){
			ArrayList<BasicBullet> bulletList = new ArrayList<BasicBullet>();

			bulletList.add(createNewBullet(tank,Direction.U)); //第二个参数direction表示子弹发射方向与坦克方向的相对方向。U表示与炮筒方向相同
				
			if(superMode){
				bulletList.add(createNewBullet(tank,Direction.UL));
				bulletList.add(createNewBullet(tank,Direction.UR));
			}	
			coolDownTime = bullet.getCoolDown();
			coolDown=false;
			return bulletList;		
		}
		return null;
	}
	

	
	private BasicBullet createNewBullet(BasicTank tank,Direction d){
		BasicBullet b = null;
		
		int left=getBulletLeft(tank);
		int top=getBulletTop(tank);
		int team=tank.getTeam();
		
		try {
			b=bullet.getClass().newInstance();
			b.setLeft(left);
			b.setTop(top);
			
			Direction direction = d==Direction.U? tank.getTankDirection():Direction.getRelativeDirection(tank.getTankDirection(), d);
			b.setDirection(direction);
			b.setTeam(team);
			b.addBulletListener(bulletListener);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return b;
	}
	
	
	private int getBulletLeft(BasicTank tank){
		return tank.getLeft()+tank.getTankWidth()/2-bullet.getBulletWidth()/2;
	}
	
	private int getBulletTop(BasicTank tank){
		return tank.getTop()+tank.getTankHeight()/2-bullet.getBulletHeight()/2;
	}

		

}
