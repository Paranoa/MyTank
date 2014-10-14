package entity.tank;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import entity.bullet.BasicBullet;
import entity.explode.Explode;
import entity.tank.ai.TankAIController;
import listener.ExplodeListener;
import listener.TankListener;
import util.Direction;

public abstract class BasicTank{
	
	protected int tankWidth;
	protected int tankHeight;
	protected int life; //记录坦克的生命，降为0的坦克将被消灭
	protected int xSpeed;//坦克的xy速度
	protected int ySpeed;
	protected Image[] tankImages;
	protected Image[] explodeImages;
	
	protected int left;
	protected int top;
	protected int currentLife;
	protected boolean kU=false,kD=false,kL=false,kR=false; //判断4个方向键的按下情况
	protected Direction direction = Direction.STOP;
	protected Direction tankDirection = Direction.U;
	protected int score;
	
	protected BaseBulletCreater bulletCreater;
	protected TankListener listener;
	
	protected int team; //记录坦克所属team ，不同队伍之间的炮弹会相互击中并造成伤害
	
	protected boolean computerControl = false;
	protected int controlCount =0;	
	protected TankAIController tankAI;
	
	//----------------------------------------------------------------------------
	public int getTankWidth() {
		return tankWidth;
	}
	public void setTankWidth(int tankWidth) {
		this.tankWidth = tankWidth;
	}
	public int getTankHeight() {
		return tankHeight;
	}
	public void setTankHeight(int tankHeight) {
		this.tankHeight = tankHeight;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getXSpeed() {
		return xSpeed;
	}
	public void setXSpeed(int speed) {
		xSpeed = speed>15?15:speed;//上限控制应在这里做
	}
	public int getYSpeed() {
		return ySpeed;
	}
	public void setYSpeed(int speed) {
		ySpeed = speed>15?15:speed;
	}
	public Direction getTankDirection() {
		return tankDirection;
	}
	public void setTankDirection(Direction tankDirection) {
		this.tankDirection = tankDirection;
	}
	
	public void addTankListener(TankListener listener){
		this.listener=listener;		
	}		
	public int getTeam() {
		return team;
	}
	public void setTeam(int team) {
		this.team = team;
	}
	public int getLife() {
		return life;
	}
	public void setLife(int life) {
		this.life = life;
	}
	public boolean isComputerControl() {
		return computerControl;
	}
	public void setComputerControl(boolean computerControl) {
		this.computerControl = computerControl;
	}	
	public int getCurrentLife() {
		return currentLife;
	}
	public void setCurrentLife(int currentLife) {
		this.currentLife = currentLife>life?life:currentLife;//生命上限控制
	}	
	public BaseBulletCreater getBulletCreater() {
		return bulletCreater;
	}
	public int getScore() {
		return score;
	}	
	public void setScore(int score) {
		this.score = score;
	}	
	public Direction getDirection() {
		return direction;
	}	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}	
	public TankListener getListener() {
		return listener;
	}	
	public void setListener(TankListener listener) {
		this.listener = listener;
	}	
	public void setBulletCreater(BaseBulletCreater bulletCreater) {
		this.bulletCreater = bulletCreater;
	}	
	public TankAIController getTankAI() {
		return tankAI;
	}
	public void setTankAI(TankAIController tankAI) {
		this.tankAI = tankAI;
	}	
	public int getControlCount() {
		return controlCount;
	}	
	public void setControlCount(int controlCount) {
		this.controlCount = controlCount;
	}
	

	
	//---------------------------------------------------------------------------------



	protected void move(){
		
			
		if(direction!=Direction.STOP){//tank方向不为stop
			if(listener.tankMove(this)){//tank可以移动
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
					left-=xSpeed;	
					top-=ySpeed;
					break;
				case UR:
					left+=xSpeed;
					top-=ySpeed;
					break;
				case DL:
					left-=xSpeed;
					top+=ySpeed;
					break;
				case DR:
					left+=xSpeed;
					top+=ySpeed;
					break;
				}
			}else if(tankAI!=null){//向ai类告知"不可移动"
				tankAI.collisionDirection(direction);
			}
			
		}
		locateDirection();

	}

	public void fire(){
		
		ArrayList<BasicBullet> bulletList=bulletCreater.getNewBullets(this);
		if(bulletList!=null){
			for(BasicBullet b:bulletList){				
					listener.tankFire(b);			
			}
		}
		
	}
	
	
	//--------------------------------------------------------------------------------
	public void draw(Graphics g){
			bulletCreater.timeTrigger();
			if(computerControl){					
				computerControl(); 
			}
				move();					//每次draw之前move，即 速度/s = 每秒draw的次数 * 速度
				drawTank(g);
	}
	
	
	//-----------------------------------------------------------------------------------
	public void keyPressedEvent(KeyEvent e){
		int keyCode=e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_W:
			kU=true;
			break;
		case KeyEvent.VK_S:
			kD=true;		
			break;
		case KeyEvent.VK_A:
			kL=true;
			break;
		case KeyEvent.VK_D:
			kR=true;
			break;
		case KeyEvent.VK_J:
			fire();
			break;
		}	
		locateDirection();	
	}
	
	public void keyReleasedEvent(KeyEvent e){
		int keyCode=e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_W:
			kU=false;
			break;
		case KeyEvent.VK_S:
			kD=false;		
			break;
		case KeyEvent.VK_A:
			kL=false;
			break;
		case KeyEvent.VK_D:
			kR=false;
			break;
		case KeyEvent.VK_J:
			break;
		}
		locateDirection();
	}
	//-----------------------------------------------------------------------------------

	public void resetKey(){
		kU = kD = kL = kR = false;
		locateDirection();
		
	}
	
	public boolean hitedByBullet(BasicBullet bullet){
		bullet.hitTank(this);
		if(tankAI!=null){
			tankAI.hitedByBullet(this,bullet);
		}
		if(currentLife<=0){
			return false;
		}	
		return true;
	}
	
	
	public Rectangle getRectangle(){
		return new Rectangle(left,top,tankWidth,tankHeight);
	}
	
	
	public Explode tankExplode(ExplodeListener listener){
		return new Explode(left,top,10,explodeImages,listener);
	}

	protected void locateDirection(){
		if(!computerControl){
			if (kU && !kD && !kL && !kR)direction=Direction.U;
			else if (!kU && kD && !kL && !kR)direction=Direction.D;
			else if (!kU && !kD && kL && !kR)direction=Direction.L;
			else if (!kU && !kD && !kL && kR)direction=Direction.R;
			else if (kU && !kD && kL && !kR)direction=Direction.UL;
			else if (kU && !kD && !kL && kR)direction=Direction.UR;
			else if (!kU && kD && kL && !kR)direction=Direction.DL;
			else if (!kU && kD && !kL && kR)direction=Direction.DR;
			else if (!kU && !kD && !kL && !kR)direction=Direction.STOP;
		}
		if(direction!=Direction.STOP){
			tankDirection=direction;  //坦克不是 停止的 才将‘坦克方向’ 改为‘这次的按键方向 ’
		}

	}
	
	protected void computerControl(){
		
		if(tankAI!=null){
			tankAI.control(this);
		}
		controlCount++;	
	}
	
	protected abstract void drawTank(Graphics g);

}
