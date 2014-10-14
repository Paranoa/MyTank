package entity.explode;

import java.awt.Graphics;
import java.awt.Image;


import listener.ExplodeListener;

public class Explode {
	
	protected int explodeCount = 10;//��ըʱ��  ��ʱ��=ˢ����Ļ���ʱ��*count
	protected int explodeIndex ;//��ը�±꣬ÿ����һ��draw����+1�����ڸ�����ͼ
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
		
		//��ը�������Ƴ�
		if(explodeIndex>=explodeCount){
			listener.removeExplode(this);
		}
		
		
	}
	
	
	
	
	

}
