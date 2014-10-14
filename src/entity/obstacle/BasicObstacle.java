package entity.obstacle;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;


public abstract class BasicObstacle{
	
	protected int left=0;
	protected int top=200;
	protected int obWidth = 20;
	protected int obHeight = 20;
	
	protected boolean throughable;
	protected boolean walkable;
	protected boolean destroyable;
	protected Image image;

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
	public int getObWidth() {
		return obWidth;
	}
	public void setObWidth(int obWidth) {
		this.obWidth = obWidth;
	}
	public int getObHeight() {
		return obHeight;
	}
	public void setObHeight(int obHeight) {
		this.obHeight = obHeight;
	}
	public boolean isThroughable() {
		return throughable;
	}
	public void setThroughable(boolean throughable) {
		this.throughable = throughable;
	}
	public boolean isWalkable() {
		return walkable;
	}
	public void setWalkable(boolean walkable) {
		this.walkable = walkable;
	}
	public boolean isDestroyable() {
		return destroyable;
	}
	public void setDestroyable(boolean destroyable) {
		this.destroyable = destroyable;
	}
	
	
	public abstract void draw(Graphics g);
	
	public  Rectangle getRectangle(){
		return new Rectangle(left,top,obWidth,obHeight);
	}
	
	


}
