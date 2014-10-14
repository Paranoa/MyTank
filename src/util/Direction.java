package util;

import java.util.ArrayList;


public enum Direction {
	U,D,L,R,UL,UR,DL,DR,STOP;
	
	public static Direction randomDirection(){
		Direction d=STOP;
		int random = (int)(Math.random()*9);
		switch (random){
			
			case 0:d=U;break;
			case 1:d=D;break;
			case 2:d=L;break;
			case 3:d=R;break;
			case 4:d=UL;break;
			case 5:d=UR;break;
			case 6:d=DL;break;
			case 7:d=DR;break;
			case 8:d=STOP;break;
		}
		return d;
		
		
	}
	//获得相对方向
	public static Direction getRelativeDirection(Direction d1,Direction d2){
		Direction direction= null;
		switch(d1){
			case U:	
				switch (d2) {
					
				case D:		
					direction = D;
					break;
				case L:
					direction = L;
					break;
				case R:
					direction = R;
					break;
				case UL:			
					direction = UL;
					break;
				case UR:			
					direction = UR;
					break;
				case DL:			
					direction = DL;
					break;
				case DR:	
					direction = DR;
					break;
				}
				
				
				
				break;
			case D:	
				switch (d2) {
				
				case D:
					direction = U;
					break;
				case L:
					direction = R;
					break;
				case R:
					direction = L;
					break;
				case UL:			
					direction = DR;
					break;
				case UR:			
					direction = DL;
					break;
				case DL:			
					direction = UR;
					break;
				case DR:			
					direction = UL;
					break;
			}
				break;
			case L:
				
				switch (d2) {
				
				case D:		
					direction = R;
					break;
				case L:
					direction = D;
					break;
				case R:
					direction = U;
					break;
				case UL:	
					direction = DL;
					break;
				case UR:			
					direction = UL;
					break;
				case DL:			
					direction = DR;
					break;
				case DR:	
					direction = UR;
					break;
			}
				break;
			case R:
				
				switch (d2) {
				
				case D:			
					direction = L;
					break;
				case L:
					direction = U;
					break;
				case R:
					direction = D;
					break;
				case UL:			
					direction = UR;
					break;
				case UR:	
					direction = DR;
					break;
				case DL:			
					direction = UL;
					break;
				case DR:	
					direction = DL;
					break;
			}
				break;
			case UL:	
				
				switch (d2) {
				
				case D:		
					direction = DR;
					break;
				case L:
					direction = DL;
					break;
				case R:
					direction = UR;
					break;
				case UL:			
					direction = L;
					break;
				case UR:			
					direction = U;
					break;
				case DL:	
					direction = D;
					break;
				case DR:			
					direction = R;
					break;
			}
				break;
			case UR:	
				
				switch (d2) {
				
				case D:		
					direction = DL;
					break;
				case L:
					direction = UL;
					break;
				case R:
					direction = DR;
					break;
				case UL:	
					direction = U;
					break;
				case UR:	
					direction = R;
					break;
				case DL:	
					direction = L;
					break;
				case DR:	
					direction = D;
					break;
			}
				break;
			case DL:	
				
				switch (d2) {
				
				case D:
					direction = UR;
					break;
				case L:
					direction = DR;
					break;
				case R:
					direction = UL;
					break;
				case UL:	
					direction = D;
					break;
				case UR:			
					direction = L;
					break;
				case DL:			
					direction = R;
					break;
				case DR:			
					direction = U;
					break;
			}
				break;
			case DR:		
				
				switch (d2) {
				
				case D:
					direction = UL;
					break;
				case L:
					direction = UR;
					break;
				case R:
					direction = DL;
					break;
				case UL:			
					direction = R;
					break;
				case UR:	
					direction = D;
					break;
				case DL:			
					direction = U;
					break;
				case DR:			
					direction = L;
					break;
			}
				break;
							
			}	
		return direction;	
	}
	
	public static Direction getOppositeDirection(Direction direction){
		switch (direction) {
		case U:
			return D;
		case D:
			return U;
		case L:
			return R;
		case R:
			return L;
		case UL:
			return DR;
		case UR:
			return DL;
		case DL:
			return UR;
		case DR:
			return UL;
		}
		return STOP;
	}
	
	public static ArrayList<Direction> getAllDirection(){
		ArrayList<Direction> allDirection = new ArrayList<Direction>();
		allDirection.add(U);
		allDirection.add(D);
		allDirection.add(L);
		allDirection.add(R);
		allDirection.add(UL);
		allDirection.add(UR);
		allDirection.add(DL);
		allDirection.add(DR);
		allDirection.add(STOP);

		return allDirection;
	}
	
	
}
