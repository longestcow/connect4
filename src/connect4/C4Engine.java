package connect4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
//https://discord.gg/wVbzUHcj
public class C4Engine{
	static Scanner s = new Scanner(System.in);
	static boolean over = false;
	static Position pos;
	static LinkedHashMap<Integer, Integer> TTMap;
	static int DEPTH = 5, rounds = 0, control = 20;
	public static void main(String[] args) throws Exception {
		//make new function to determine how good a move is and use that if exit in negamax is true in the if statement checking if depth is reached
		//do the 6 first moves thing for transposition table
		TTMap=getMap();
		String start="";
		int[][] grid = new int[6][7];
		
		for(int i = 0; i<grid.length; i++)
			for(int j = 0; j<grid[i].length; j++) 
				grid[i][j]=0;
		int turn=0;
		while(!start.isEmpty()) {
			turn++;
			rounds++;
			for(int i = 5; i >= 0; i--) {
				if(!(grid[i][Integer.parseInt(""+start.charAt(0))-1]==0))
					continue;
				else {
					grid[i][Integer.parseInt(""+start.charAt(0))-1]=(turn%2==0)?-1:1;
					start=start.substring(1);
					break;
				}
			}
		}
		pos = new Position(grid);
//		TTMap.clear();
//		updateMap(TTMap);
//		if(true) {
//			return;
//		}
		
		while(!prompt()) {}
		System.out.println(checkWinner(pos)==2?"Tied":((checkWinner(pos)==1)?"\u001b[1;31mO\u001b[0m":"\u001b[1;33mO\u001b[0m") + " has won");
		updateMap(TTMap);
	}
	
	public static boolean prompt() {
		pos.print();
	
		if((checkWinner(pos)!=0)){
			return true;
		}
		
		System.out.print("\u001b[1;31mO\u001b[0m: ");
		int move = s.nextInt()-1;
		System.out.println();
		if(move+1==11)
			for(int i = 0; i<6; i++) {
				for(int j =0;j<7;j++)
					System.out.print(((pos.position[i][j]==-1)?"\033[1;33my":(pos.position[i][j]==1)?"\033[1;31mr":"\033[1;37m.") + " ");
				System.out.println();
			}
		if(!(move>=0 && move<=6) || (pos.position[0][move]!=0)) {
			System.out.println("invalid move");
			prompt();
		}
		else {
			Position temp = pos.move(1, move);
			pos.position=Arrays.stream(temp.position.clone()).map(int[]::clone).toArray(int[][]::new);
			pos.rx=temp.rx; pos.ry=temp.ry;	pos.yx=temp.yx; pos.yy=temp.yy;
			pos.print();
			rounds++;
			if(!(checkWinner(pos)==0)){
				return true;
			}
			bestMove();//yellow
		}
		return false;
	}
	
	public static int checkWinner(Position pos) {
		int x = pos.rx, y=pos.ry, turn=1;
		
		
		for(int a = 0; a<2; a++) {
			//horizontal -
			for(int j = y-3, count=0; j<=y+3; j++) {
				try {
					if(pos.position[x][j]==turn)
						count++;
					else
						count=0;
					if(count==4) 
						return turn;
				}
				catch(Exception e){continue;}
			}
			
			//vertical |
			for(int i = x-3, count=0; i<=x+3; i++) {
				try {
					if(pos.position[i][y]==turn)
						count++;
					else
						count=0;
					if(count==4) 
						return turn;
				}
				catch(Exception e){continue;}
			}
			
			//diagonal \
			for(int i = x-3, j = y-3, count=0; i<=x+3 && j<=y+3; i++, j++) {
				try {
					if(pos.position[i][j]==turn)
						count++;
					else
						count=0;
					if(count==4) 
						return turn;
				}
				catch(Exception e){continue;}
			}
			
			//diagonal /
			for(int i = x+3, j = y-3, count=0; i>=x-3 && j<=y+3; i--, j++) {
				try {
					if(pos.position[i][j]==turn)
						count++;
					else
						count=0;
					if(count==4) 
						return turn;
				}
				catch(Exception e){continue;}
			}
			
			
			x = pos.yx; y=pos.yy; turn=-1;
		}

		
		for(int i = 0; i<pos.position.length; i++)
			for(int j = 0; j<pos.position[i].length; j++)
				if(pos.position[i][j]==0)
					return 0; //not a tie or win
		return 2; //tie		
	}

	
	public static void askMove() {
		System.out.println("Enter best move: ");
		int y=s.nextInt()-1;
		for(Position child : pos.moveGen(-1)) {
			if(child.yy==y) {
				TTMap.put(Arrays.deepHashCode(child.position), 100);
				updateMap(TTMap);
			}
			else {
				TTMap.put(Arrays.deepHashCode(child.position), -100);
				updateMap(TTMap);
			}
			
		}
		Position temp = pos.move(-1, y);
		pos.position=Arrays.stream(temp.position.clone()).map(int[]::clone).toArray(int[][]::new); 
		pos.rx=temp.rx; pos.ry=temp.ry;	pos.yx=temp.yx; pos.yy=temp.yy;
	}
	public static void bestMove() {
		int score = 0, bestScore = -100_000, y = 0;
		long time = System.currentTimeMillis();
		for(Position child : pos.moveGen(-1)) {
			score = -negamax(1, 0, -100_000, 100_000, child, (rounds < control));
			TTMap.put(Arrays.deepHashCode(child.position), score);
			updateMap(TTMap);
			if(score > bestScore) {
				bestScore = score;
				y = child.yy;
			}
			child.print();
			System.out.println(score+"\r\n------------------------------------------------");
			if(score >= 98)
				break;
		}	
		Position temp = pos.move(-1, y);
		pos.position=Arrays.stream(temp.position.clone()).map(int[]::clone).toArray(int[][]::new); 
		pos.rx=temp.rx; pos.ry=temp.ry;	pos.yx=temp.yx; pos.yy=temp.yy;
		rounds++;
		
		System.out.println(System.currentTimeMillis()-time + " ms");
	}
		
	public static int negamax(int turn, int depth, int alpha, int beta, Position position, boolean exit) {
//		if(TTMap.containsKey(Arrays.deepHashCode(position.position))) { -----------------------------------------------
//			return -TTMap.get(Arrays.deepHashCode(position.position));
//		}
		int score = -100_000;
		
		int winner = checkWinner(position);
		if(winner != 0) {
			return (winner==2)?0:-100+depth;
		}
		if(exit && depth/2 == DEPTH) {
			return (winner==0 || winner==2)?0:-100+depth;
		}
		
		
		for(Position child : position.moveGen(turn)) {
			score = Math.max(score, -negamax(-turn, depth+1, -beta, -alpha, child, exit));
			alpha = Math.max(alpha, score);
			if(alpha >= beta)
				break;
		}
		return score;
	}
	
	
	// transposition table stuff
	@SuppressWarnings({ "unchecked", "serial" })
	public static LinkedHashMap<Integer, Integer> getMap() {
		File file = new File("src/connect4/hashmap");
		LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>(){
	        @SuppressWarnings("rawtypes")
			@Override
	        protected boolean removeEldestEntry(final Map.Entry eldest) {
	            return size() > 10000;
	        }
	    };
		if(!file.exists()) {
			return map;
		}
		FileInputStream f;
		try {
			f = new FileInputStream(file);
			ObjectInputStream s = new ObjectInputStream(f);
			map = (LinkedHashMap<Integer, Integer>) s.readObject();
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}
	
	public static boolean updateMap(LinkedHashMap<Integer, Integer> map) {
		File file = new File("src/connect4/hashmap");
		try {
		file.createNewFile();
		FileOutputStream f = new FileOutputStream(file);
	    ObjectOutputStream s = new ObjectOutputStream(f);
	    s.writeObject(map);
	    s.close();
	    return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	    
	}
}

