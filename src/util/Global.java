package util;

import java.awt.Color;
import java.awt.Toolkit;

public class Global {
	public static final int WIDTH = 1000;			//��Ϸ���� ��
	public static final int HEIGHT = 700;			//��Ϸ���� ��
	public static final int LOCATION_LEFT = 200;	//��Ϸ����λ��left
	public static final int LOCATION_TOP = 30;   	//��Ϸ������λ��top
	public static final int GAMESPEED = 45;         //��Ϸ�ٶȣ�ֵԽ����Ϸ�ٶ�Խ��
	public static final int TOPDISTANCE = 45;		//����������
	
	public static final Color BGCOLOR = Color.WHITE;  	//����ɫ
	public static final Color TLIFEBARBC = Color.WHITE; //̹���������߿�ɫ
	public static final Color TLIFEBARC = Color.MAGENTA;//̹����������ɫ
	

	public static final Toolkit toolKit = Toolkit.getDefaultToolkit();//�õ�Ĭ�Ϲ��߰�  toolkit�ࣺһЩ���ʺ���ֱ����java���Ĳ���������Ӳ����Ϣ��������һЩ����ϵͳ�����£���װ�˲���ϵͳ��һЩ����
	public static final ClassLoader loader = ClassLoader.getSystemClassLoader();
}
