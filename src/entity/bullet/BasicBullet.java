package entity.bullet;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import entity.explode.Explode;
import entity.tank.BasicTank;

import listener.BulletListener;
import listener.ExplodeListener;

import util.Direction;
import util.Global;

public abstract class BasicBullet{
	
	protected int left;
	protected int top;
	protected Direction direction ;
	protected Image[] bulletImages;
	protected Image[] explodeImages;
	
	protected int xSpeed;
	protected int ySpeed;
	protected int pow;

	protected int bulletWidth;
	protected int bulletHeight;
	protected int coolDown ;//相当于子弹重复发射的间隔 每秒最多发射 1000/cooldown次
	
	
	protected BulletListener listener;
	protected int team;   //子弹的team 会伤害不同team的坦克或穿过
	
	
	{
		ClassLoader loader = Global.loader;

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
	//-----------------------------------------------------------------------------

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	

	
	public int getXSpeed() {
		return xSpeed;
	}

	public void setXSpeed(int speed) {
		xSpeed = speed;
	}

	public int getYSpeed() {
		return ySpeed;
	}

	public void setYSpeed(int speed) {
		ySpeed = speed;
	}
	
	public int getPow() {
		return pow;
	}
	
	public void setPow(int p){
		pow=p;
	}
	 
	
	public int getBulletWidth() {
		return bulletWidth;
	}

	public int getBulletHeight() {
		return bulletHeight;
	}
	
	
	
	public void addBulletListener(BulletListener listener){
		this.listener = listener;
		
	}
	
	
	
	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public int getCoolDown() {
		return coolDown;
	}

	public Image[] getBulletImages() {
		return bulletImages;
	}

	public void setBulletImages(Image[] bulletImages) {
		this.bulletImages = bulletImages;
	}

	//---------------------------------------------------------------------------
	public void move(){
		switch (direction) {
		case U:
			top-=ySpeed;			
			break;
		case D:
			top+=ySpeed;
			break;
		case L:
			left-=xSpeed;
			break;
		case R:
			left+=xSpeed;
			break;
		case UL:
			top-=ySpeed;
			left-=xSpeed;			
			break;
		case UR:
			top-=ySpeed;
			left+=xSpeed;
			break;
		case DL:
			top+=ySpeed;
			left-=xSpeed;
			break;
		case DR:
			top+=ySpeed;
			left+=xSpeed;
			break;

		}
	}
		
	public void draw(Graphics g){				
			move();
			if(listener.bulletMove(this)){//----子弹‘活着’ 才调用drawBullet方法	
				drawBullet(g);
			}						
	}
	


	public Rectangle getRectangle(){
		
		return new Rectangle(left,top,bulletWidth,bulletHeight);
	}
	
	public void hitTank(BasicTank tank){
		tank.setCurrentLife(tank.getCurrentLife()-pow); //将坦克的生命减去子弹的pow
	}
	
	public Explode bulletExplode(ExplodeListener listener){
		return new Explode(left,top,10,explodeImages,listener);
	}
	
	
	protected abstract void drawBullet(Graphics g);

}
