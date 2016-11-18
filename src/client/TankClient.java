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

// �ṹ��  component--container--window--frame--jframe
	
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
		initImagesPath();//��ʼ����ͬʱ��ʼ����Ϸ��ʼǰ����ص�ͼƬ·��
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
	

	private class MainBase extends BasicObstacle{ //�����ʾ��Ϸ�������
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
	
	//---------------------------------��Ϸ���̿��Ʋ���------------------------------------------------
	public void newGame() {
		
		launchFrame();
		initGameEntities();
		loadImages(imagesPath);
		updateBeforePause();
		countDown();
		setComputerControl();//����ʱ����ʱ���̹�˵ĵ��Կ���
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
	
	//------------��Ϸ��ʼǰ���ڿ������λ�û�һ��ͼƬ
	private void loadImages(ArrayList<String> images){
		for(String image:images){
			this.getGraphics().drawImage(Global.toolKit.getImage(Global.loader.getResource(image)),-100,-100,null);
		}	
	}
	
	
	private void updateBeforePause(){	
		this.update(this.getGraphics());
		this.update(this.getGraphics());//��һ�λ�ʱ������
		
	}
	//-------------��ʼʱ�ĵ���ʱ
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
	
	
	//-----������Ϸ�е�3���߳�
	private class AutoPaintThread implements Runnable{//ÿ��gameSpeed���ػ�
		
		public void run() {
			while(true){
				while(playing){
							//���Է����ⲿ������Ժͷ�����this����� this �� (TankClient.this)��
					repaint();		//component��repaint����    ��������������������˷����ᾡ����ô������ paint ����������˷����ᾡ����ô������ update ������ 
									// ����������һ��paint������Ȼ����ñ��� ��update ���� ,�� update ����������������ƺ���������ԭͼ������д�÷���Ҫע��������ñ���� paint����
									//ע����������ᱻ(��֪��ʲô)�Զ�����~��ʹ��������ʽ�ĵ���
					try {
						Thread.sleep(TankClient.this.gameSpeed);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(500);//��ͣʱÿ��һ��sleepһ��ʱ�䡣����Ῠ��
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
 	
	private class AutoCreateEntityThread implements Runnable{//ÿ��5����ӵз�̹�ˡ�����	
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

	
	//------------------------------------��ϷԪ�س�ʼ������-----------------------------------------
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

		 
		 
		 createObstacles(420,580,2,6,new BrickWall());//��һ�������رߵ�ǽ
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
	
	private void createTanks(int left,int top,int width,int height,BasicTank tank,int num,int team,boolean computerControl){//�����λ�����tank����Ϊ��tank��(life ����  ����)
		int count =0;		//���λ�����ָ����Χ left top width heigth����ָ������(num)��ָ������(team)��̹��
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
				
				if(isMovable(t)){  //λ�ú���������(��left��top�������width��height�����ж�λ���Ƿ�Ϊ����)
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
	public ArrayList<BasicItem> initAllItemsList(){//��ʼ�����ȫ���ߵ�list������ȷ������õķ�Χ
		ArrayList<BasicItem> allItem = new ArrayList<BasicItem>();
		allItem.add(new ItemSuperBullet());
		allItem.add(new ItemSupreMode());
		allItem.add(new ItemSpeedUp());
		allItem.add(new ItemLifeBall());
		allItem.add(new ItemFreeze());
		allItem.add(new ItemBomb());

		return allItem;
	}
	
	private BasicItem createItem(BasicItem i,int left,int top,TankClient listener){//ָ��item�±� (��itemList��)��left��top
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
	


	//-----------------------------------��Ϸ���岿��----------------------------------------------------
	
	public void launchFrame(){//������Ϸ����
		setTitle("TankWar");
		setLocation(Global.LOCATION_LEFT,Global.LOCATION_TOP);
		setSize(Global.WIDTH,Global.HEIGHT);
		setResizable(false);
		setBackground(Global.BGCOLOR);

		//Ϊframe��Ӹ���listener
		addWindowListener(new MyWindowAdapter());//�̳�adapter�࣬��д��Ҫ�õ��ķ���;	JFrame�в���setDefaultCloseOperation,ʵ��Ҳ���õ���system.exit	
		addKeyListener(new MyKeyAdapter());
		setVisible(true);	
	}
	

	
	//----------Ϊ��Ϸ������ӵļ�������
	private class MyWindowAdapter extends WindowAdapter {
		
		@Override
		public void windowLostFocus(WindowEvent e) {
			// TODO Auto-generated method stub
			super.windowLostFocus(e);
		}
		
		@Override
		public void windowClosing(WindowEvent e) {
			System.exit(0);//0 ��ʾ���쳣�˳�
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
	
	
	//--------------��д��Ϸ�����update����
	@Override
	public void update(Graphics g) {//container��д��component�� update���� �������ٴ���д 
		if(offScreenImage==null){
			offScreenImage=createImage(Global.WIDTH,Global.HEIGHT);//component�ķ���   createImage(int width, int height) 
		}									// 				     ����һ������˫����ġ�������Ļ����Ƶ�ͼ��
			
			Graphics ig=offScreenImage.getGraphics();	//�õ�����ͼƬ�ġ��ʡ�	
			
			//�ڻ���ͼƬ�ϻ�ͼ		
			cleanBackGround(ig);		//��Ϊ�Եڶ���֮���õĶ�����ǰimage�ģ�������Ҫ�����Ļ	
			myPaint(ig);	//�����ҵ���һ���Լ�д��Mypaint������ԭ����update�����paint������
			
			
			//�ѻ���ͼƬ������Ļ��		
			g.drawImage(offScreenImage, 0, 0, null);	//���һ������ImageObserver ����~	
	}
	
	//	@Override
	//	public void paint(Graphics arg0) {//container��д��component�� paint���� �������ٴ���д 
	//		System.out.println("�ò�����paint");
	//	}
	//	
	
	private void cleanBackGround(Graphics g){
		g.setColor(Global.BGCOLOR);
		g.fillRect(0, 0, Global.WIDTH, Global.HEIGHT); //rectangle ����  
		
	}
	
	public void myPaint(Graphics g) {//�򻺳�ͼƬ��ͼ		
		drawBullets(g);
		drawTanks(g);
		drawMap(g);
		drawItem(g);
		drawExplode(g);
		
		drawScore(g);	
	}
	
	//--------������ϷԪ�صĻ�ͼ���趼�ǣ���ɾ����Ч����ϷԪ�أ�Ȼ���ÿ����ϷԪ�ص���draw����
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

	
	//---------------------------------�������ʵ�ֵļ���------------------------------------------------
	//---------------------------Tank
	@Override
	public boolean tankMove(BasicTank tank) {	
		if(tank==playerTank){ //ֻ�����̹�˿��ԳԵ���
			tankCollisionItem(tank);
		}
		return isMovable(tank);
	}
	
	@Override
	public void tankFire(BasicBullet bullet) {
		bullets.add(bullet);
		System.out.println("���һ���ڵ�����ӹ��bullets sizeΪ"+bullets.size());
	}
	

	
	
	//---------------------------Bullet
	
	@Override
	public boolean bulletMove(BasicBullet bullet){
		boolean flag = true;  //���ֵΪtrue ��ʾ�ӵ���������Լ���draw����
		
		if(isOverView(bullet)){//�������Ƴ�
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
	public void tankExplode(BasicTank tank) { //�����tank�����ɾ���б�
		explodes.add(tank.tankExplode(this));
		System.out.println("����һ��̹�˱�ը--����sizeΪ"+explodes.size());
		removeTank(tank);
		System.out.println("��һ��̹�˼���ɾ���б�--removeTankList��");
	}
	
	public void bulletExplode(BasicBullet bullet){
		explodes.add(bullet.bulletExplode(this));
		System.out.println("����һ���ӵ���ը--����sizeΪ"+explodes.size());
		removeBullet(bullet);
		System.out.println("��һ���ӵ�����ɾ���б�--removeTankList��");
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
	
	//----------------------------------�������һЩɾ��---------------------------------------------------
	
	

	private void removeBulletByList() { 	//��removeList�е�����Ԫ�� �Ƴ�,�����������ÿ�Ρ���ͼ��֮ǰ����
		for(BasicBullet rb:removeBulletList){
			bullets.remove(rb);			
			System.out.println("ɾ��һ���ڵ������ڵ�bullet sizeΪ"+bullets.size());
		}		
		removeBulletList = new ArrayList<BasicBullet>();
	}
		
	//-----------------
	
	private void removeTankByList() { 	
		for(BasicTank t:removeTankList){
			tanks.remove(t);			
			System.out.println("ɾ��һ��̹�ˣ����ڵ�tanks sizeΪ"+bullets.size());
		}
		removeTankList = new ArrayList<BasicTank>();
	}
	//-----------------	
	private void removeObstacleByList(){
		for(BasicObstacle o:removeObstacleList){
			obstacles.remove(o);
			System.out.println("ɾ��һ���ϰ�����ڵ�obstacles sizeΪ"+obstacles.size());
		}
		removeObstacleList = new ArrayList<BasicObstacle>();
	}
	//-------------
	private void removeExplodeByList(){
		for(Explode e:removeExplodeList){
			explodes.remove(e);
			System.out.println("ɾ��һ����ը�����ڵ�explodes sizeΪ"+explodes.size());
		}
		removeExplodeList = new ArrayList<Explode>();
	}
	//---------------
	private void removeItemByList(){
		for(BasicItem i:removeItemList){
			items.remove(i);
			System.out.println("ɾ��һ�����ߣ����ڵ�items sizeΪ"+items.size());
		}
		removeItemList = new ArrayList<BasicItem>();
	}
	
	
	
	//-----------------------------------------------------------------------------------
	//��Щ�����ж��ӵ������
	
	
	private boolean isOverView(BasicBullet bullet){//�ӵ��Ƿ񳬳��߽�
		return (bullet.getLeft()<=0-bullet.getBulletWidth()||bullet.getLeft()>=Global.WIDTH+bullet.getBulletWidth()||bullet.getTop()<=0-bullet.getBulletHeight()||bullet.getTop()>=Global.HEIGHT+bullet.getBulletHeight());
	}
	
	
	private boolean isCollisionTank(BasicBullet bullet){//�ӵ��Ƿ���̹����ײ��������nexttank��tanks�б��е�����tank�Ƚ�,thistankҲ�����ж��Ƿ��ͻ��
		
		for(BasicTank tank:tanks){ //�ó�tanks�е�����tank,�������Ƿ��غ�
			if(tank.getRectangle().intersects(bullet.getRectangle())&&tank.getTeam()!=bullet.getTeam()){ //�ӵ���̹�˵ġ����Ρ� �غ� �Ҳ�����ͬһ�� team
				hitTank(tank,bullet);				
				return true;
			}
		}
		return false;
	}
	
	private boolean isCollisionObstacle(BasicBullet bullet){ //�ӵ��Ƿ����ϰ�����ײ
		for(BasicObstacle obstacle:obstacles){
			if(obstacle.getRectangle().intersects(bullet.getRectangle())&&!obstacle.isThroughable()){//�����ص����ϰ���"���ɴ���"							
				hitObstacle(obstacle, bullet);
				return true;}
		}
		return false;
	}
	
	private void hitTank(BasicTank tank,BasicBullet bullet){//�ӵ���̹��
		
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
		if(obstacle.isDestroyable()){	//�ϰ�����"�ɴݻٵ�"�Ž��ϰ���ɾ��
			removeObstacleList.add(obstacle);
			if(obstacle == mainBase){
				mainBase = null;
			}
		}
	}


	
	//-----------------------------------------------------------------------------------
	//��Щ�����ж�̹�˵����
	private BasicTank getNextTank(BasicTank tank){//��ݵ�ǰtank�ķ�������һ��move���tank ��ע�⣺���ص�Tank�� ֻ��left top width height���ԣ�
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
				case STOP:break;
			}
		}
		return nextTank;	
	}
	
	private boolean isMovable(BasicTank tank){//ͨ��һϵ�з����ķ���ֵ�ж�̹���Ƿ��ܽ�����һ���ƶ�
		BasicTank nextTank = getNextTank(tank);
		return !isCollisionTank(tank,nextTank)&&!isOverView(nextTank)&&!isCollisionObstacle(nextTank);
	}	
	
	private boolean isCollisionTank(BasicTank thisTank,BasicTank nextTank){ //������nexttank��tanks�б��е�����tank�Ƚ�,thistankҲ�����ж��Ƿ��ͻ
		for(BasicTank tank:tanks){
			if(tank.getRectangle().intersects(nextTank.getRectangle())&&tank!=thisTank){//�����ص��Ҳ��Ǳ����Ҳ����Ѿ���ը��				
				return true;}
		}
		return false;
	}
	
	private boolean isCollisionObstacle(BasicTank nextTank){ //������nexttank��tanks�б��е�����tank�Ƚ�,thistankҲ�����ж��Ƿ��ͻ
		for(BasicObstacle obstacle:obstacles){
			if(obstacle.getRectangle().intersects(nextTank.getRectangle())&&!obstacle.isWalkable()){//�����ص����ϰ�����"��������"��				
				return true;}
		}
		return false;
	}
	
	private boolean isOverView(BasicTank tank){ //���tank��left top width height �ж��Ƿ񳬳��߽�

		return (tank.getLeft()<0||tank.getTop()<Global.TOPDISTANCE||tank.getLeft()>Global.WIDTH-tank.getTankWidth()||tank.getTop()>Global.HEIGHT-tank.getTankHeight());
		
	}
	
	private void tankCollisionItem(BasicTank tank){
		for(BasicItem item:items){
			if(tank.getRectangle().intersects(item.getRectangle())){ //̹��ײ��item �����item��affect��Ȼ��ɾ��
				item.affect(tank);
				removeItem(item);		
			}
		}
	}
	//-----------------------------------------------------------------------------------
	//�����жϵ��ߵ����
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
	
	
	
	
	//------------------Main���� �� ��ʾ��Ϸ����(��ͣ���ڡ���Ϸ�����)-----------------------------------------------------------------
	public static void main(String[] args) {
		launchMain();
	}
	
	//----------------��ʾ��Ϸ������-------------------------
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
		panel.setOpaque(false);//����͸��
		
		//-------��ʼ��ť
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
		
		//-------�˳���ť
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
	
	
	//----------------��ʾ��ͣ����-------------------------------
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
		
		//------����ť	
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
		
		
		//------���¿�ʼ��ť
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
		
		
		//------�ص������水ť
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

		TankClient.this.setEnabled(false);//������Ĵ�����Ϊ���ɼ���
		frame.setAlwaysOnTop(true);//�ô���ʼ��������
		frame.setUndecorated(true);//����frameװ��
		frame.setVisible(true);
	}
	
	//----------------��ʾ��Ϸ�����-----------------------
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
		panel.setOpaque(false);//����͸��
				
		//-----���¿�ʼ��ť
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
		
		
		//-----�ص������水ť
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

		TankClient.this.setEnabled(false);//������Ĵ�����Ϊ���ɼ���
		frame.setAlwaysOnTop(true);//�ô���ʼ��������
		frame.setUndecorated(true);//����frameװ��
		frame.setVisible(true);
	}


	


}
