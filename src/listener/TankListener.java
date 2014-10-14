package listener;

import entity.bullet.BasicBullet;
import entity.tank.BasicTank;

public interface TankListener {
	boolean tankMove(BasicTank t);
	void tankFire(BasicBullet b);
}
