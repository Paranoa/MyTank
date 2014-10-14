package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import entity.bullet.*;
import entity.obstacle.*;
import entity.tank.*;
import entity.tank.ai.TankAI_0;
import entity.explode.*;
import entity.item.BasicItem;
import entity.item.ItemBomb;
import entity.item.ItemFreeze;
import entity.item.ItemLifeBall;
import entity.item.ItemSpeedUp;
import entity.item.ItemSuperBullet;
import entity.item.ItemSupreMode;

import listener.*;
import util.*;

public class TankClient extends Frame implements TankListener,BulletListener,ExplodeListener{ /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

// 结构：  component--container--window--frame--jframe
	
	private BasicTank playerTank;
	private MainBase mainBase;

	private ArrayList<BasicTank> tanks = new ArrayList<BasicTank>();
	private ArrayList<BasicTank> removeTankList = new ArrayList<BasicTank>();

	private ArrayList<BasicBullet> bullets=new ArrayList<BasicBullet>();
	private ArrayList<BasicBullet> removeBulletList=new ArrayList<BasicBullet>();
	
	private ArrayList<BasicObstacle> obstacles = new ArrayList<BasicObstacle>();
	private ArrayList<BasicObstacle> removeObstacleList = new ArrayList<BasicObstacle>();

	private ArrayList<Explode> explodes = new ArrayList<Explode>();
	private ArrayList<Explode> removeExplodeList = new ArrayList<Explode>();
	
	private ArrayList<BasicItem> items = new ArrayList<BasicItem>();
	private ArrayList<BasicItem> removeItemList = new ArrayList<BasicItem>();
	
	private Image offScreenImage;
	private int score;
	private ArrayList<String> imagesPath  = new ArrayList<String>();
	
	private boolean playing = true;
	private boolean gameOver = false;
	private int gameSpeed = Global.GAMESPEED;
	
	public TankClient(){
		initImagesPath();//初始化的同时初始化游戏开始前需加载的图片路径
	}

	
	
	//-----------------------------------------------
	public ArrayList<BasicTank> getTanks() {
		return tanks;
	}
	public ArrayList<BasicBullet> getBullets() {
		return bullets;
	}
	public ArrayList<Explode> getExplodes() {
		return explodes;
	}
	public BasicTank getPlayerTank() {
		return playerTank;
	}
	//------------------------------------------------


	private void initImagesPath(){
		File file = null;
		try {
			file = new File(Global.loader.getResource("images").toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}	
		getImagesPath(imagesPath,file);
	}
	
	




	private void getImagesPath(ArrayList<String> filePath,File file){
		if(file.isDirectory()){
			for(File f:file.listFiles()){
				getImagesPath(filePath, f);
			}
		}else{
			String path = file.getPath();
			path = path.substring(path.indexOf("images")).replace("\\", "/");
			filePath.add(path);
		}
	}
	

	private class MainBase extends BasicObstacle{ //这个表示游戏的主基地
		{
			obWidth = 80;
			obHeight = 80;
			throughable = false;
		    walkable = false;
		    destroyable = true;
		    
			ClassLoader loader = Global.loader;
		    image = Global.toolKit.getImage(loader.getResource("images/mainbase.png"));
		}
		@Override
		public void draw(Graphics g) {
			g.drawImage(image, left, top, null);			
		}
		
	}
	
	//---------------------------------游戏流程控制部分------------------------------------------------
	public void newGame() {
		
		launchFrame();
		initGameEntities();
		loadImages(imagesPath);
		updateBeforePause();
		countDown();
		setComputerControl();//倒计时结束时添加坦克的电脑控制
		startGameThread();
		

	}
	

	private void restart(){
		this.dispose();
		TankClient tc = new TankClient();
		tc.newGame();	
	} 
	
	private void pause(){
		playing = false;	
		showPauseWindow();
	}
	
	//------------游戏开始前先在看不见的位置画一次图片
	private void loadImages(ArrayList<String> images){
		for(String image:images){
			this.getGraphics().drawImage(Global.toolKit.getImage(Global.loader.getResource(image)),-100,-100,null);
		}	
	}
	
	
	private void updateBeforePause(){	
		this.update(this.getGraphics());
		this.update(this.getGraphics());//第一次画时看不见
		
	}
	//-------------开始时的倒计时
	private void countDown(){	
		Image[] images = new Image[3];
		images[0]=Global.toolKit.getImage(Global.loader.getResource("images/count_3.png"));
		images[1]=Global.toolKit.getImage(Global.loader.getResource("images/count_2.png"));
		images[2]=Global.toolKit.getImage(Global.loader.getResource("images/count_1.png"));
		
		int i = 0;
		while(i <3){
			drawCountDown(images[i],i);

			try {
				Thread.sleep(1000);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
		}		
		
	}
	
	private void drawCountDown(Image image,int i){
		offScreenImage.getGraphics().drawImage(image,Global.WIDTH/2 -75,Global.HEIGHT/2-75,null);	
		this.getGraphics().drawImage(offScreenImage,0,0,null);
	}
	
	private void setComputerControl(){
		for(BasicTank tank:tanks){
			tank.setComputerControl(true);
		}
	}
	
	private void startGameThread(){
		new Thread(new AutoPaintThread()).start();
		new Thread(new AutoCreateEntityThread()).start();
		new Thread(new DetectGameOverThread()).start();
	}
	
	
	//-----开启游戏中的3个线程
	private class AutoPaintThread implements Runnable{//每隔gameSpeed秒重画
		
		public void run() {
			while(true){
				while(playing){
							//可以访问外部类的属性和方法，this的区别【 this 和 (TankClient.this)】
					repaint();		//component的repaint方法    如果此组件是轻量组件，则此方法会尽快调用此组件的 paint 方法。否则此方法会尽快调用此组件的 update 方法。 
									// 在这里会调用一次paint方法，然后调用本类 的update 方法 ,而 update 方法（这个方法中似乎‘擦除了原图’，重写该方法要注意擦除）会调用本类的 paint方法
									//注意这个方法会被(不知道什么)自动调用~即使不这样显式的调用
					try {
						Thread.sleep(TankClient.this.gameSpeed);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(500);//暂停时每做一次sleep一段时间。否则会卡死
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
 	
	private class AutoCreateEntityThread implements Runnable{//每隔5秒添加敌方坦克、道具	
		@Override
		public void run() {
			while(true){
				while(playing){
					if(tanks.size()<=12){
						createTanks(0, Global.TOPDISTANCE, Global.WIDTH, 80, new EnemyTank_0(), 3, 1,true);	
					}
					if(items.size()<=4){
						createItems(2);
					}
					try {
						Thread.sleep(5*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
		
	}
	
	private class DetectGameOverThread implements Runnable{

		@Override
		public void run() {
			while(true){
				while(playing){
					if(playerTank==null||mainBase==null){
						gameOver = true;
					}
					
					if(gameOver){
						playing = false;
						showGameOverWindow();
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
	
		}
		
		
	}
	//---------

	
	
//	private void clearGameEntities(){
//		tanks = new ArrayList<BasicTank>();
//		removeTankList = new ArrayList<BasicTank>();
//
//		bullets=new ArrayList<BasicBullet>();
//		removeBulletList=new ArrayList<BasicBullet>();
//		
//		obstacles = new ArrayList<BasicObstacle>();
//		removeObstacleList = new ArrayList<BasicObstacle>();
//
//		explodes = new ArrayList<Explode>();
//		removeExplodeList = new ArrayList<Explode>();
//		
//		items = new ArrayList<BasicItem>();
//		removeItemList = new ArrayList<BasicItem>();
//	}
//	

	
	//------------------------------------游戏元素初始化部分-----------------------------------------
	private void initGameEntities(){
		createMap();
		createItems(5);
		createPlyaerTank();
		createTanks(0,30,1000,20,new EnemyTank_0(),10,1,false);

	}
	
	private void createMap() {
		 createObstacles(50,500,2,10,new BrickWall());
		 createObstacles(160,500,2,10,new BrickWall());
		 createObstacles(270,500,2,10,new BrickWall());
		 
		 createObstacles(910,500,2,10,new BrickWall());
		 createObstacles(800,500,2,10,new BrickWall());
		 createObstacles(690,500,2,10,new BrickWall());

		 createObstacles(300,400,4,2,new IronWall());
		 createObstacles(380,400,4,2,new BrickWall());
		 createObstacles(460,400,4,2,new IronWall());
		 createObstacles(540,400,4,2,new BrickWall());
		 createObstacles(620,400,4,2,new IronWall());
		 
		 createObstacles(200,310,2,6,new IronWall());
		 createObstacles(760,310,2,6,new IronWall());

		 createObstacles(220,100,7,3,new Meadow());
		 createObstacles(320,160,13,2,new Meadow());

		 createObstacles(560,210,10,3,new BrickWall());
		 createObstacles(310,230,2,2,new IronWall());

		 
		 
		 
		 createObstacles(70,200,6,6,new BrickWall());
		 createObstacles(810,200,6,6,new BrickWall());

		 
		 
		 createObstacles(420,580,2,6,new BrickWall());//这一段是造基地边的墙
		 createObstacles(540,580,2,6,new BrickWall());
		 createObstacles(460,580,4,2,new BrickWall());

		 
		
		 
		 createMainBase();
	}
	
	private void createObstacles(int left,int top,int x,int y,BasicObstacle o) {
		for(int i=0;i<y;i++){
			for(int j=0;j<x;j++){	
				BasicObstacle obstacle = null;
				
				try {
					obstacle = o.getClass().newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				} 			
				if(obstacle!=null){			
					obstacle.setLeft(left+j*obstacle.getObWidth());
					obstacle.setTop(top+i*obstacle.getObHeight());				
					obstacles.add(obstacle);
				}
			}			
		}		
	}
	
	private void createMainBase(){
		 mainBase = new MainBase();
		 mainBase.setLeft(Global.WIDTH/2-mainBase.getObWidth()/2);
		 mainBase.setTop(Global.HEIGHT-mainBase.getObHeight());
		 obstacles.add(mainBase);
	}
	
	
	private void createItems(int num){
		ArrayList<BasicItem> allItems = initAllItemsList();
		Random r =new Random();
		int count = 0;
		while(count<num){
			BasicItem item = createItem(allItems.get((int)(Math.random()*allItems.size())), r.nextInt(Global.WIDTH), r.nextInt(Global.HEIGHT)+Global.TOPDISTANCE,this);
			if(!isInvalidItemPosition(item)){
				items.add(item);
				count++;
			}	
		}

	}

	private void createPlyaerTank(){
		BasicBullet playerTankbullet = new CommonBullet();
		PlayerBulletCreater bulletCreater=new PlayerBulletCreater(playerTankbullet,this);
		playerTank = new PlayerTank(380,Global.HEIGHT-40,this,bulletCreater,0); //left top listener bulletcreater  team
		tanks.add(playerTank);
	}
	
	private void createTanks(int left,int top,int width,int height,BasicTank tank,int num,int team,boolean computerControl){//于随机位置生成tank参数为：tank的(life 数量  队伍)
		int count =0;		//生成位置随机（指定范围 left top width heigth）、指定数量(num)、指定队伍(team)的坦克
		Random r =new Random();
		while(count<num){
	
			BasicTank t = null;
			try{
				t = tank.getClass().newInstance();
			}catch(Exception e){
				e.printStackTrace();
			}
			if(t!=null){
				t.setLeft(r.nextInt(width)+left);
				t.setTop(r.nextInt(height)+top);
				
				if(isMovable(t)){  //位置合理的情况下(有left，top和自身的width，height就能判断位置是否为合理)
					t.setTankDirection(Direction.D);
					t.setListener(this);
					BasicBullet bullet = new CommonBullet();
					BaseBulletCreater bulletCreater = new BaseBulletCreater(bullet,this);
					t.setBulletCreater(bulletCreater);
					t.setTeam(team);
					t.setComputerControl(computerControl);
					t.setTankAI(new TankAI_0());
					
					tanks.add(t);
					count++;
				}
			}else{
				return;
			}
			
		}	
	}
	
	//-------------
	public ArrayList<BasicItem> initAllItemsList(){//初始化代表全道具的list，用于确定随机获得的范围
		ArrayList<BasicItem> allItem = new ArrayList<BasicItem>();
		allItem.add(new ItemSuperBullet());
		allItem.add(new ItemSupreMode());
		allItem.add(new ItemSpeedUp());
		allItem.add(new ItemLifeBall());
		allItem.add(new ItemFreeze());
		allItem.add(new ItemBomb());

		return allItem;
	}
	
	private BasicItem createItem(BasicItem i,int left,int top,TankClient listener){//指定item下标 (在itemList中)，left，top
		BasicItem item = null; 		
		try{
			item = i.getClass().newInstance();
		}catch(Exception e){
			e.printStackTrace();
		}		
		if(item!=null){
			item.setLeft(left);
			item.setTop(top);
			item.setListener(this);
		}
		return item;
	}
	


	//-----------------------------------游戏窗体部分----------------------------------------------------
	
	public void launchFrame(){//加载游戏窗体
		setTitle("TankWar");
		setLocation(Global.LOCATION_LEFT,Global.LOCATION_TOP);
		setSize(Global.WIDTH,Global.HEIGHT);
		setResizable(false);
		setBackground(Global.BGCOLOR);

		//为frame添加各种listener
		addWindowListener(new MyWindowAdapter());//继承adapter类，重写需要用到的方法;	JFrame中才有setDefaultCloseOperation,实际也是用到了system.exit	
		addKeyListener(new MyKeyAdapter());
		setVisible(true);	
	}
	

	
	//----------为游戏窗体添加的监听器类
	private class MyWindowAdapter extends WindowAdapter {
		
		@Override
		public void windowLostFocus(WindowEvent e) {
			// TODO Auto-generated method stub
			super.windowLostFocus(e);
		}
		
		@Override
		public void windowClosing(WindowEvent e) {
			System.exit(0);//0 表示无异常退出
		}
	}
	
	private class MyKeyAdapter extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(playerTank!=null){
				playerTank.keyPressedEvent(e);
			}
			int keyCode = e.getKeyCode();
			switch(keyCode){
			
			case KeyEvent.VK_SPACE:
				pause();
				break;

			case KeyEvent.VK_R:;
				restart();
				break;
			}		
		}
		@Override
		public void keyReleased(KeyEvent e) {
			if(playerTank!=null){
				playerTank.keyReleasedEvent(e);
			}
		}	
	}
	
	
	//--------------重写游戏窗体的update方法
	@Override
	public void update(Graphics g) {//container重写了component的 update方法 ，这里再次重写 
		if(offScreenImage==null){
			offScreenImage=createImage(Global.WIDTH,Global.HEIGHT);//component的方法   createImage(int width, int height) 
		}									// 				     创建一幅用于双缓冲的、可在屏幕外绘制的图像。
			
			Graphics ig=offScreenImage.getGraphics();	//拿到缓冲图片的‘笔’	
			
			//在缓冲图片上绘图		
			cleanBackGround(ig);		//因为自第二次之后用的都是以前image的，所以需要清除屏幕	
			myPaint(ig);	//这里我调用一个自己写的Mypaint方法（原来的update会调用paint方法）
			
			
			//把缓冲图片画到屏幕上		
			g.drawImage(offScreenImage, 0, 0, null);	//最后一个参数ImageObserver 不管~	
	}
	
	//	@Override
	//	public void paint(Graphics arg0) {//container重写了component的 paint方法 ，这里再次重写 
	//		System.out.println("用不到的paint");
	//	}
	//	
	
	private void cleanBackGround(Graphics g){
		g.setColor(Global.BGCOLOR);
		g.fillRect(0, 0, Global.WIDTH, Global.HEIGHT); //rectangle 矩形  
		
	}
	
	public void myPaint(Graphics g) {//向缓冲图片画图		
		drawBullets(g);
		drawTanks(g);
		drawMap(g);
		drawItem(g);
		drawExplode(g);
		
		drawScore(g);	
	}
	
	//--------所有游戏元素的画图步骤都是：先删除无效的游戏元素，然后对每个游戏元素调用draw方法
	private void drawBullets(Graphics g){
		removeBulletByList();
		for(BasicBullet b:bullets){
			b.draw(g);			
		}
	} 
	
	private void drawTanks(Graphics g){
		removeTankByList();
		for(BasicTank t:tanks){
			t.draw(g);
		}		
	}

	private void drawMap(Graphics g){
		removeObstacleByList();
		for(BasicObstacle o:obstacles){
			o.draw(g);
		}
	}
	
	private void drawExplode(Graphics g){
		removeExplodeByList();
		for(Explode e:explodes){
			e.draw(g);
		}		
	}
	
	private void drawItem(Graphics g){
		removeItemByList();
		for(BasicItem i:items){
			i.draw(g);
		}
		
	}
	
	private void drawScore(Graphics g){
		g.setFont(new Font("Arial",Font.ITALIC,20));
		g.setColor(Color.RED);
		g.drawString("Score:"+score, 0, 50);
	}

	
	//---------------------------------下面的是实现的监听方法------------------------------------------------
	//---------------------------Tank
	@Override
	public boolean tankMove(BasicTank tank) {	
		if(tank==playerTank){ //只有玩家坦克可以吃道具
			tankCollisionItem(tank);
		}
		return isMovable(tank);
	}
	
	@Override
	public void tankFire(BasicBullet bullet) {
		bullets.add(bullet);
		System.out.println("添加一个炮弹！添加过后bullets size为"+bullets.size());
	}
	

	
	
	//---------------------------Bullet
	
	@Override
	public boolean bulletMove(BasicBullet bullet){
		boolean flag = true;  //这个值为true 表示子弹将会调用自己的draw方法
		
		if(isOverView(bullet)){//出界则移除
			removeBullet(bullet);
			flag=false;
		}
		else if(isCollisionTank(bullet)||isCollisionObstacle(bullet)){
			bulletExplode(bullet);
			flag=false;
		}
		return flag;	
	}
	
	
	//---------------------------explode
	@Override
	public void removeExplode(Explode explode) {
		removeExplodeList.add(explode);
	}
	


	
	//--------------
	public void tankExplode(BasicTank tank) { //将这个tank加入待删除列表
		explodes.add(tank.tankExplode(this));
		System.out.println("新增一个坦克爆炸--现在size为"+explodes.size());
		removeTank(tank);
		System.out.println("将一个坦克加入删除列表--removeTankList！");
	}
	
	public void bulletExplode(BasicBullet bullet){
		explodes.add(bullet.bulletExplode(this));
		System.out.println("新增一个子弹爆炸--现在size为"+explodes.size());
		removeBullet(bullet);
		System.out.println("将一个子弹加入删除列表--removeTankList！");
	}
	
	public void removeTank(BasicTank tank){
		removeTankList.add(tank);
	}
	
	public void removeBullet(BasicBullet bullet){
		removeBulletList.add(bullet);	
	}
	
	public void removeItem(BasicItem item){
		removeItemList.add(item);
	}
	
	//----------------------------------下面的是一些删除方法---------------------------------------------------
	
	

	private void removeBulletByList() { 	//将removeList中的所有元素 移除,这个方法将在每次‘画图’之前调用
		for(BasicBullet rb:removeBulletList){
			bullets.remove(rb);			
			System.out.println("删除一个炮弹！现在的bullet size为"+bullets.size());
		}		
		removeBulletList = new ArrayList<BasicBullet>();
	}
		
	//-----------------
	
	private void removeTankByList() { 	
		for(BasicTank t:removeTankList){
			tanks.remove(t);			
			System.out.println("删除一个坦克！现在的tanks size为"+bullets.size());
		}
		removeTankList = new ArrayList<BasicTank>();
	}
	//-----------------	
	private void removeObstacleByList(){
		for(BasicObstacle o:removeObstacleList){
			obstacles.remove(o);
			System.out.println("删除一个障碍物！现在的obstacles size为"+obstacles.size());
		}
		removeObstacleList = new ArrayList<BasicObstacle>();
	}
	//-------------
	private void removeExplodeByList(){
		for(Explode e:removeExplodeList){
			explodes.remove(e);
			System.out.println("删除一个爆炸！现在的explodes size为"+explodes.size());
		}
		removeExplodeList = new ArrayList<Explode>();
	}
	//---------------
	private void removeItemByList(){
		for(BasicItem i:removeItemList){
			items.remove(i);
			System.out.println("删除一个道具！现在的items size为"+items.size());
		}
		removeItemList = new ArrayList<BasicItem>();
	}
	
	
	
	//-----------------------------------------------------------------------------------
	//这些用来判断子弹的情况
	
	
	private boolean isOverView(BasicBullet bullet){//子弹是否超出边界
		return (bullet.getLeft()<=0-bullet.getBulletWidth()||bullet.getLeft()>=Global.WIDTH+bullet.getBulletWidth()||bullet.getTop()<=0-bullet.getBulletHeight()||bullet.getTop()>=Global.HEIGHT+bullet.getBulletHeight());
	}
	
	
	private boolean isCollisionTank(BasicBullet bullet){//子弹是否与坦克相撞（将参数nexttank与tanks列表中的所有tank比较,thistank也用于判断是否冲突）
		
		for(BasicTank tank:tanks){ //拿出tanks中的所有tank,看矩形是否重合
			if(tank.getRectangle().intersects(bullet.getRectangle())&&tank.getTeam()!=bullet.getTeam()){ //子弹与坦克的‘矩形’ 重合 且不属于同一个 team
				hitTank(tank,bullet);				
				return true;
			}
		}
		return false;
	}
	
	private boolean isCollisionObstacle(BasicBullet bullet){ //子弹是否与障碍物相撞
		for(BasicObstacle obstacle:obstacles){
			if(obstacle.getRectangle().intersects(bullet.getRectangle())&&!obstacle.isThroughable()){//矩形重叠且障碍物"不可穿过"							
				hitObstacle(obstacle, bullet);
				return true;}
		}
		return false;
	}
	
	private void hitTank(BasicTank tank,BasicBullet bullet){//子弹打坦克
		
		if(!tank.hitedByBullet(bullet)){
			tankExplode(tank);
			if(tank!=playerTank){
				score+=tank.getScore();
			}else{
				playerTank =null;
			}	
		}
		
	}
	
	private void hitObstacle(BasicObstacle obstacle,BasicBullet bullet){
		if(obstacle.isDestroyable()){	//障碍物是"可摧毁的"才将障碍物删除
			removeObstacleList.add(obstacle);
			if(obstacle == mainBase){
				mainBase = null;
			}
		}
	}


	
	//-----------------------------------------------------------------------------------
	//这些用来判断坦克的情况
	private BasicTank getNextTank(BasicTank tank){//根据当前tank的方向获得下一次move后的tank （注意：返回的Tank类 只有left top width height属性）
		BasicTank nextTank = null;
		try {
			nextTank = tank.getClass().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		if(nextTank!=null){
			nextTank.setLeft(tank.getLeft());
			nextTank.setTop(tank.getTop());
			nextTank.setTankWidth(tank.getTankWidth());
			nextTank.setTankHeight(tank.getTankHeight());
			
			Direction direction = tank.getTankDirection();
			switch (direction) {
				case U:
					nextTank.setTop(tank.getTop()-tank.getYSpeed()) ;break;
				case D:
					nextTank.setTop(tank.getTop()+tank.getYSpeed());break;
				case L:
					nextTank.setLeft(tank.getLeft()-tank.getXSpeed());break;
				case R:
					nextTank.setLeft(tank.getLeft()+tank.getXSpeed());break;
				case UL:
					nextTank.setLeft(tank.getLeft()-tank.getXSpeed());
					nextTank.setTop(tank.getTop()-tank.getYSpeed());break;
				case UR:
					nextTank.setLeft(tank.getLeft()+tank.getXSpeed());
					nextTank.setTop(tank.getTop()-tank.getYSpeed());break;
				case DL:
					nextTank.setLeft(tank.getLeft()-tank.getXSpeed());
					nextTank.setTop(tank.getTop()+tank.getYSpeed());break;
				case DR:
					nextTank.setLeft(tank.getLeft()+tank.getXSpeed());
					nextTank.setTop(tank.getTop()+tank.getYSpeed());break;	
			}
		}
		return nextTank;	
	}
	
	private boolean isMovable(BasicTank tank){//通过一系列方法的返回值判断坦克是否能进行下一次移动
		BasicTank nextTank = getNextTank(tank);
		return !isCollisionTank(tank,nextTank)&&!isOverView(nextTank)&&!isCollisionObstacle(nextTank);
	}	
	
	private boolean isCollisionTank(BasicTank thisTank,BasicTank nextTank){ //将参数nexttank与tanks列表中的所有tank比较,thistank也用于判断是否冲突
		for(BasicTank tank:tanks){
			if(tank.getRectangle().intersects(nextTank.getRectangle())&&tank!=thisTank){//矩形重叠且不是本身且不是已经爆炸的				
				return true;}
		}
		return false;
	}
	
	private boolean isCollisionObstacle(BasicTank nextTank){ //将参数nexttank与tanks列表中的所有tank比较,thistank也用于判断是否冲突
		for(BasicObstacle obstacle:obstacles){
			if(obstacle.getRectangle().intersects(nextTank.getRectangle())&&!obstacle.isWalkable()){//矩形重叠且障碍物是"不可行走"的				
				return true;}
		}
		return false;
	}
	
	private boolean isOverView(BasicTank tank){ //根据tank的left top width height 判断是否超出边界

		return (tank.getLeft()<0||tank.getTop()<Global.TOPDISTANCE||tank.getLeft()>Global.WIDTH-tank.getTankWidth()||tank.getTop()>Global.HEIGHT-tank.getTankHeight());
		
	}
	
	private void tankCollisionItem(BasicTank tank){
		for(BasicItem item:items){
			if(tank.getRectangle().intersects(item.getRectangle())){ //坦克撞到item 则调用item的affect，然后删除
				item.affect(tank);
				removeItem(item);		
			}
		}
	}
	//-----------------------------------------------------------------------------------
	//用来判断道具的情况
	private boolean isInvalidItemPosition(BasicItem item){
		
		if(item.getLeft()<0||item.getLeft()>Global.WIDTH-item.getItemWidth()||item.getTop()>(Global.HEIGHT-item.getItemHeight()-Global.TOPDISTANCE)||item.getTop()<Global.TOPDISTANCE){
			return true;
		}
		for(BasicItem i:items){
			if(item.getRectangle().intersects(i.getRectangle())){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	//------------------Main方法 和 显示游戏窗口(暂停窗口、游戏结束窗口)-----------------------------------------------------------------
	public static void main(String[] args) {
		launchMain();
	}
	
	//----------------显示游戏主窗口-------------------------
	public static void launchMain(){
		final JFrame frame = new JFrame();
				
		frame.setTitle("TankWar");
		frame.setLocation(Global.LOCATION_LEFT,Global.LOCATION_TOP);
		frame.setSize(Global.WIDTH,Global.HEIGHT);
		frame.setResizable(false);
		frame.setLayout(null);
		
		frame.addWindowListener(new WindowAdapter(){		
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			
		});
		
		Image tmpImage;
		ImageIcon tmpIcon;
		
		ClassLoader loader = Global.loader;
		tmpImage = Global.toolKit.getImage(loader.getResource("images/main.png"));
		tmpIcon = new ImageIcon(tmpImage);
		JLabel label = new JLabel(tmpIcon);
		label.setBounds(0, 0, Global.WIDTH, Global.HEIGHT);
		
		frame.getLayeredPane().add(label,new Integer(Integer.MIN_VALUE));
		JPanel panel = (JPanel)frame.getContentPane();
		panel.setOpaque(false);//设置透明
		
		//-------开始按钮
		tmpImage = Global.toolKit.getImage(loader.getResource("images/button_0.png"));
		tmpIcon = new ImageIcon(tmpImage);
		
		JButton button_0 = new JButton(tmpIcon);
		button_0.setBounds(750, 620, 100, 50);
		button_0.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				TankClient tc = new TankClient();				
				tc.newGame();				
			}
			
		});
		frame.add(button_0);
		
		//-------退出按钮
		tmpImage = Global.toolKit.getImage(loader.getResource("images/button_1.png"));
		tmpIcon = new ImageIcon(tmpImage);
		
		JButton button_1 = new JButton(tmpIcon);
		button_1.setBounds(880, 620, 100, 50);
		button_1.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);		
			}
			
		});
		frame.add(button_1);
		
		
		
		frame.setVisible(true);
	}
	
	
	//----------------显示暂停窗口-------------------------------
	private void showPauseWindow(){
		final JFrame frame = new JFrame();
		
		int frameWidth = 380;
		int frameHeight = 300;
		int frameLeft = Global.LOCATION_LEFT+Global.WIDTH/2-frameWidth/2;
		int frameTop = Global.LOCATION_TOP+Global.HEIGHT/2-frameHeight/2;
		
		frame.setLocation(frameLeft,frameTop);
		frame.setSize(frameWidth,frameHeight);	
		frame.setResizable(false);
		frame.setLayout(null);
		frame.addWindowListener(new WindowAdapter(){
			
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}			
		});
		
		Image tmpImage = null;
		ImageIcon tmpIcon = null;
		
		ClassLoader loader = Global.loader;
		tmpImage = Global.toolKit.getImage(loader.getResource("images/pause.png"));
		tmpIcon = new ImageIcon(tmpImage);
		JLabel label = new JLabel(tmpIcon);
		label.setBounds(0, 0, frameWidth, frameHeight);
		
		frame.getLayeredPane().add(label,new Integer(Integer.MIN_VALUE));
		JPanel panel = (JPanel)frame.getContentPane();
		panel.setOpaque(false);
		
		//------继续按钮	
		tmpImage = Global.toolKit.getImage(loader.getResource("images/button_2.png"));
		tmpIcon = new ImageIcon(tmpImage);
		
		JButton button_2 = new JButton(tmpIcon);
		button_2.setBounds(20, frameHeight-80, 100, 50);
		button_2.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				TankClient.this.setEnabled(true);
				TankClient.this.requestFocus();
				playerTank.resetKey();
				playing =true;

			}
			
		});
		frame.add(button_2);
		
		
		//------重新开始按钮
		tmpImage = Global.toolKit.getImage(loader.getResource("images/button_3.png"));
		tmpIcon = new ImageIcon(tmpImage);
		
		JButton button_3 = new JButton(tmpIcon);
		button_3.setBounds(140, frameHeight-80, 100, 50);
		button_3.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				TankClient.this.setEnabled(true);
				TankClient.this.requestFocus();
				TankClient.this.restart();
			}
			
		});
		frame.add(button_3);
		
		
		//------回到主界面按钮
		tmpImage = Global.toolKit.getImage(loader.getResource("images/button_4.png"));
		tmpIcon = new ImageIcon(tmpImage);
		
		JButton button_4 = new JButton(tmpIcon);
		button_4.setBounds(260, frameHeight-80, 100, 50);
		button_4.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				TankClient.this.dispose();
				
				launchMain();
			}
			
		});
		frame.add(button_4);

		TankClient.this.setEnabled(false);//将后面的窗口设为不可激活
		frame.setAlwaysOnTop(true);//让窗口始终在最上
		frame.setUndecorated(true);//禁用frame装饰
		frame.setVisible(true);
	}
	
	//----------------显示游戏结束窗口-----------------------
	private void showGameOverWindow(){
		final JFrame frame = new JFrame();
		
		int frameWidth = 380;
		int frameHeight = 300;
		int frameLeft = Global.LOCATION_LEFT+Global.WIDTH/2-frameWidth/2;
		int frameTop = Global.LOCATION_TOP+Global.HEIGHT/2-frameHeight/2;
		
		frame.setLocation(frameLeft,frameTop);
		frame.setSize(frameWidth,frameHeight);		
		frame.setResizable(false);
		frame.setLayout(null);
		frame.addWindowListener(new WindowAdapter(){
			
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}			
		});
		
		Image tmpImage = null;
		ImageIcon tmpIcon = null;
		

		ClassLoader loader = Global.loader;
		tmpImage = Global.toolKit.getImage(loader.getResource("images/gameover.png"));
		tmpIcon = new ImageIcon(tmpImage);
		JLabel label = new JLabel(tmpIcon);
		label.setBounds(0, 0, frameWidth, frameHeight);
		
		frame.getLayeredPane().add(label,new Integer(Integer.MIN_VALUE));
		JPanel panel = (JPanel)frame.getContentPane();
		panel.setOpaque(false);//设置透明
				
		//-----重新开始按钮
		tmpImage = Global.toolKit.getImage(loader.getResource("images/button_5.png"));
		tmpIcon = new ImageIcon(tmpImage);
		
		JButton button_5 = new JButton(tmpIcon);
		button_5.setBounds(20, frameHeight-80, 160, 50);
		button_5.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				TankClient.this.setEnabled(true);
				TankClient.this.requestFocus();
				TankClient.this.restart();
			}
			
		});
		frame.add(button_5);
		
		
		//-----回到主界面按钮
		tmpImage = Global.toolKit.getImage(loader.getResource("images/button_6.png"));
		tmpIcon = new ImageIcon(tmpImage);
		
		JButton button_6 = new JButton(tmpIcon);
		button_6.setBounds(200, frameHeight-80, 160, 50);
		button_6.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				TankClient.this.dispose();
				
				launchMain();
			}
			
		});
		frame.add(button_6);

		TankClient.this.setEnabled(false);//将后面的窗口设为不可激活
		frame.setAlwaysOnTop(true);//让窗口始终在最上
		frame.setUndecorated(true);//禁用frame装饰
		frame.setVisible(true);
	}


	


}
