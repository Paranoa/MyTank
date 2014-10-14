package entity.tank.ai;

import util.Direction;
import entity.bullet.BasicBullet;
import entity.tank.BasicTank;

public interface TankAIController {
	public void control(BasicTank tank);
	public void hitedByBullet(BasicTank tank,BasicBullet bullet);
	public void collisionDirection(Direction direction);
}
