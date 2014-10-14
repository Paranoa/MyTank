package util;

import java.awt.Color;
import java.awt.Toolkit;

public class Global {
	public static final int WIDTH = 1000;			//游戏窗体 宽
	public static final int HEIGHT = 700;			//游戏窗体 高
	public static final int LOCATION_LEFT = 200;	//游戏窗体位置left
	public static final int LOCATION_TOP = 30;   	//游戏窗口体位置top
	public static final int GAMESPEED = 45;         //游戏速度，值越大游戏速度越快
	public static final int TOPDISTANCE = 45;		//标题栏距离
	
	public static final Color BGCOLOR = Color.WHITE;  	//背景色
	public static final Color TLIFEBARBC = Color.WHITE; //坦克生命条边框色
	public static final Color TLIFEBARC = Color.MAGENTA;//坦克生命条颜色
	

	public static final Toolkit toolKit = Toolkit.getDefaultToolkit();//拿到默认工具包  toolkit类：一些不适合用直接用java做的操作（如拿硬盘信息），访问一些操作系统做的事，封装了操作系统的一些操作
	public static final ClassLoader loader = ClassLoader.getSystemClassLoader();
}
