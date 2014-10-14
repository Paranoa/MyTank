package entity.tank;

import java.util.ArrayList;

import listener.BulletListener;
import util.Direction;
import util.Global;
import entity.bullet.BasicBullet;

public class BaseBulletCreater {
	
	protected BasicBullet bullet;   //这个工厂类每次将产生一些这个成员的新的实例
	protected BulletListener bulletListener ;	//对所有产生的子弹类都会给予同一个client
	
	protected int coolDownTime;
	protected boolean coolDown = true; //记录子弹是否已经冷却完毕可以再次发射

	//----------------------------------------------------------
	public BaseBulletCreater(){}
	public BaseBulletCreater(BasicBullet bullet,BulletListener bulletListener){
		this.bullet = bullet;
		this.bulletListener = bulletListener;
	}
	
	
	//----------------------------------------------------------
	public BasicBullet getBullet() {
		return bullet;
	}

	public void setBullet(BasicBullet bullet) {
		this.bullet = bullet;
	}
	
	public BulletListener getBulletListener() {
		return bulletListener;
	}

	public void setBulletListener(BulletListener bulletListener) {
		this.bulletListener = bulletListener;
	}
	
	public boolean isCoolDown() {
		return coolDown;
	}
	public void setCoolDown(boolean coolDown) {
		this.coolDown = coolDown;
	}
	
	public int getCoolDownTime() {
		return coolDownTime;
	}
	public void setCoolDownTime(int coolDownTime) {
		this.coolDownTime = coolDownTime;
	}
	
	
	
	
	
	
	
	//-----------------------------------------------------------
	
	


	public void timeTrigger(){
		
		
		if(!coolDown){
			coolDownTime-=Global.GAMESPEED;
		}
	
		if(coolDownTime<=0){
			coolDown = true;
		}
		
	}




	public ArrayList<BasicBullet>  getNewBullets(BasicTank tank){
		
		
		if(isCoolDown()){
			ArrayList<BasicBullet> bulletList = new ArrayList<BasicBullet>();

			bulletList.add(createNewBullet(tank,Direction.U)); //第二个参数direction表示子弹发射方向与坦克方向的相对方向。U表示与炮筒方向相同
				
				
			coolDownTime = bullet.getCoolDown();
			coolDown=false;
			return bulletList;		
		}
		return null;
	}
	
	
	private int getBulletLeft(BasicTank tank){
		return tank.getLeft()+tank.getTankWidth()/2-bullet.getBulletWidth()/2;
	}
	
	private int getBulletTop(BasicTank tank){
		return tank.getTop()+tank.getTankHeight()/2-bullet.getBulletHeight()/2;
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
	
	
	
	

}
