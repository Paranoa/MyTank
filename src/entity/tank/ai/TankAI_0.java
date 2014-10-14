package entity.tank.ai;

import java.util.ArrayList;
import java.util.Random;

import util.Direction;
import entity.bullet.BasicBullet;
import entity.tank.BasicTank;

public class TankAI_0 implements TankAIController {
	
	private Direction aiDirection;
	private boolean aiFire;
	private ArrayList<Direction> obstructedDirection = new ArrayList<Direction>();

	@Override
	public void control(BasicTank tank){	
		int controlCount = tank.getControlCount();
		
		controlTankDirection(controlCount,tank);	
		controlTankFire(controlCount,tank);
		
		if(tank.getControlCount()>20){ //每隔一段时间重置一下control的状态
			tank.setControlCount(-1);
			resetAI();
		}	
		
	}
	
	@Override
	public void hitedByBullet(BasicTank tank,BasicBullet bullet) {
		aiDirection = Direction.getOppositeDirection(bullet.getDirection());
		aiFire = true;
		tank.setControlCount(0);
		
	}
	
	@Override
	public void collisionDirection(Direction direction) {
		if(!obstructedDirection.contains(direction)){
			obstructedDirection.add(direction);
		}
	}
	
	
	private void controlTankDirection(int controlCount,BasicTank tank){	
		getTankDirection(controlCount,tank);
	}
	
	
	private void controlTankFire(int controlCount,BasicTank tank){
		if(aiFire){
			tank.fire();
		}else{
			if(Math.random()>0.95){
				tank.fire();
			}
		}	
	}
	
	private void getTankDirection(int controlCount,BasicTank tank){
		if(aiDirection!=null){
			tank.setDirection(aiDirection);
		}
		
		else if(controlCount==0){
			
			ArrayList<Direction> allDirection = Direction.getAllDirection();
			
			for(Direction d:obstructedDirection){
				allDirection.remove(d);
			}
			if(allDirection.size()>1){
				tank.setDirection(allDirection.get(new Random().nextInt(allDirection.size())));

			}else{
				tank.setDirection(Direction.randomDirection());
				obstructedDirection = new ArrayList<Direction>();
			}
		}

	}
	
	
	
	private void resetAI(){
		
		aiDirection = null;
		aiFire = false;	
	}

	


}
