package entity.item;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import client.TankClient;



import entity.tank.BasicTank;

public abstract class BasicItem {
	
	protected int left;
	protected int top;
	protected int itemWidth = 60;
	protected int itemHeight= 60;
	protected Image image;
	protected TankClient listener;
	
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
	public int getItemWidth() {
		return itemWidth;
	}
	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}
	public int getItemHeight() {
		return itemHeight;
	}
	public void setItemHeight(int itemHeight) {
		this.itemHeight = itemHeight;
	}
	public TankClient getListener() {
		return listener;
	}
	public void setListener(TankClient listener) {
		this.listener = listener;
	}
	
	public Rectangle getRectangle(){
		return new Rectangle(left,top,itemWidth,itemHeight);
	}
	
	
	
	
	public abstract void draw(Graphics g);
	public abstract void affect(BasicTank tank);
	
	
}
