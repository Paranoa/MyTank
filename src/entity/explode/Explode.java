package entity.explode;

import java.awt.Graphics;
import java.awt.Image;


import listener.ExplodeListener;

public class Explode {
	
	protected int explodeCount = 10;//爆炸时长  总时长=刷新屏幕间隔时间*count
	protected int explodeIndex ;//爆炸下标，每调用一次draw方法+1，用于辅助画图
	protected int left;
	protected int top;

	protected ExplodeListener listener;
	protected Image[] images;

	public Explode(){}
	public Explode(int left,int top,int explodeCount,Image[] images,ExplodeListener listener){
		this.left = left;
		this.top = top;
		this.explodeCount = explodeCount;
		this.images = images;
		this.listener = listener;
	}
	
	public int getExplodeCount() {
		return explodeCount;
	}

	public void setExplodeCount(int explodeCount) {
		this.explodeCount = explodeCount;
	}

	public int getExplodeIndex() {
		return explodeIndex;
	}

	public void setExplodeIndex(int explodeIndex) {
		this.explodeIndex = explodeIndex;
	}

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
	

	public void draw(Graphics g) {
		
		g.drawImage(images[explodeIndex], left, top,null);
		explodeIndex++;
		
		//爆炸结束后移除
		if(explodeIndex>=explodeCount){
			listener.removeExplode(this);
		}
		
		
	}
	
	
	
	
	

}
