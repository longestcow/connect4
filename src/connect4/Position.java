package connect4;

import java.util.ArrayList;
import java.util.Arrays;

public class Position {
	int[][] position;
	public int ry=100,rx=100,yx=100,yy=100;
	Position(int[][] position){
		this.position=position;
	}

	public Position move(int turn, int y)  {
		for(int i= 5; i>=0; i--) {
			if(position[i][y]==0) {
				position[i][y]=turn;
				if(turn==1) {
					rx=i;ry=y;
				}
				else if(turn==-1) {
					yx=i;yy=y;
				}
				return this;
			}
		}
		return null;
	}
	
	public ArrayList<Position> moveGen(int forTurn){
		ArrayList<Position> children = new ArrayList<>();
		int ix=rx,iy=ry,jx=yx,jy=yy;
		for(int k = 0, j; k<7; k++) { 
			j=new int[] {3,2,4,1,5,0,6}[k];
			if(this.move(forTurn, j)!=(null)) {
				Position temp = new Position(Arrays.stream(position.clone()).map(int[]::clone).toArray(int[][]::new));
				temp.rx=rx;temp.ry=ry;temp.yx=yx;temp.yy=yy;
				children.add(temp);
				this.position[(forTurn==1)?rx:yx][(forTurn==1)?ry:yy]=0; //unmove
			}
		}
		this.rx=ix; this.ry=iy; this.yx=jx; this.yy=jy;
		return children;
	}
	
	public void print() {
		for(int i = 0; i<position.length; i++) {
			for(int j = 0; j<position[i].length; j++) {
				if( i==yx && j == yy && position[i][j]==-1)
					System.out.print("\033[1;93mO\033[0m ");
				else
					System.out.print(((position[i][j]==1)?"\u001b[1;31mO":(position[i][j]==-1)?"\u001b[1;33mO":"\033[1;37m.")+"\u001b[0m ");
					
			}
		System.out.println();
		}
		System.out.println("1 2 3 4 5 6 7");
	}
}
