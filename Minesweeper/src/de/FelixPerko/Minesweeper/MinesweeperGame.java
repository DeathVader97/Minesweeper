package de.FelixPerko.Minesweeper;

import java.util.ArrayList;

import de.FelixPerko.Minesweeper.Fields.DefaultField;
import de.FelixPerko.Minesweeper.Fields.Field;
import de.FelixPerko.Minesweeper.Fields.Mine;

public class MinesweeperGame {
	
//	public static void main(String[] args) {
//		boolean[] testPossibility = new boolean[8];
//		boolean cont = true;
//		while (true){
//			boolean carry = true;
//			for (int i2 = 0 ; i2 < testPossibility.length ; i2++){
//				if (carry){
//					testPossibility[i2] = !testPossibility[i2];
//					if (testPossibility[i2])
//						carry = false;
//				} else
//					break;
//			}
//			if (carry)
//				break;
//			for (int i = 0 ; i < testPossibility.length ; i++){
//				if (testPossibility[i])
//					System.out.print("1");
//				else
//					System.out.print("0");
//			}
//			System.out.println();
//		}
//	}
	
	public boolean gameRunning;
	long startTime;
	public int fieldsLeft = 0;
	public int bombsLeft = 0;
	
	int w,h,b;
	Field[][] fields;
	public boolean hasGenerated = false;
	
	//TIMING
	long lastTime = 0;
	double runningTime = 0;
	
	public MinesweeperGame(int w, int h, int b) {
		this.w = w;
		this.h = h;
		this.b = b;
		bombsLeft = b;
		createFields();
		gameRunning = true;
		startTime = System.nanoTime();
	}

	private void createFields() {
		fields = new Field[w][h];
		for (int x = 0 ; x < w ; x++){
			for (int y = 0 ; y < h ; y++){
				fields[x][y] = new DefaultField(this, x, y);
			}
		}			
	}

	public void generate(int freeX, int freeY) {
		
		hasGenerated = true;
		
		boolean[][] bombPos = new boolean[w][h];
		for (int i = 0 ; i < b ; i++){
			int x = (int) (Math.random()*w);
			int y = (int) (Math.random()*h);
			if (!bombPos[x][y] && !(x == freeX && y == freeY))
				bombPos[x][y] = true;
			else
				i--;
		}
		
		for (int x = 0 ; x < w ; x++){
			for (int y = 0 ; y < h ; y++){
				if (bombPos[x][y])
					fields[x][y] = new Mine(this, x, y);
			}
		}
		
		for (int x = 0 ; x < w ; x++){
			for (int y = 0 ; y < h ; y++){
				if (fields[x][y] == null || !fields[x][y].getClass().equals(Mine.class)){
					int danger = getDanger(x, y);
					if (danger == 0)
						fields[x][y] = new DefaultField(this, x, y);
					else
						fields[x][y] = new DangerField(this, x, y, danger);
				}
			}
		}
		
		fieldsLeft = w*h-b;
	}
	
	private int getDanger(int x, int y){
		int count = 0;
		for (int x2 = x-1 ; x2 <= x+1 ; x2++){
			for (int y2 = y-1 ; y2 <= y+1 ; y2++){
				if (x2 == x && y2 == y)
					continue;
				Field f = getField(x2, y2);
				if (f != null && f.getClass().equals(Mine.class))
					count++;
			}
		}
		return count;
	}
	
	public Field getField(int x, int y){
		if (x < 0 || y < 0 || x >= w || y >= h)
			return null;
		return fields[x][y];
	}

	public void countBlock(){
		fieldsLeft--;
		if (fieldsLeft < 1)
			wonGame();
	}

	public void lostGame() {
		gameRunning = false;
		for (int x = 0 ; x < w ; x++){
			for (int y = 0 ; y < h ; y++){
				if (fields[x][y].getClass().equals(Mine.class))
					fields[x][y].setVisible(false);
			}
		}
		Main.drawImg();
		Main.main.openGeneratorFrameEnd(false, 0);
//		System.out.println("Spiel verloren.");
	}

	public void wonGame() {
		gameRunning = false;
		Main.drawImg();
		double winTime = ((double)((System.nanoTime()-startTime)/100000000))/10;
		Main.main.openGeneratorFrameEnd(true, winTime);
//		System.out.println("Spiel gewonnen. "+winTime+"s");
	}
	
	public void loop() {
		long currentTime = System.nanoTime();
		long deltaT = currentTime-lastTime;
		if (lastTime == 0)
			deltaT = 0;
		lastTime = currentTime;
		if (gameRunning){
			runningTime += deltaT/1000000000.0;
			solveAutomatically();
		}
		try {
			Thread.sleep(16);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	boolean continueAutomatic = false;
	double cooldownTime = 0.0;
	double nextSolveTime;
	private void solveAutomatically() {
		if (!continueAutomatic || runningTime < nextSolveTime)
			return;
		if (!doStep())
			continueAutomatic = false;
		else
			Main.drawImg();
		nextSolveTime = runningTime+1;
	}

	public boolean doStep() {
		boolean changed = false;
		for (int x = 0 ; x < w ; x++){
			for (int y = 0 ; y < h ; y++){
				Field f = fields[x][y];
				if (!f.isVisible() || !f.getClass().equals(DangerField.class))
					continue;
				int left = ((DangerField)f).danger;
				int possibleMines = left;
				for (int x2 = x-1 ; x2 < x+2 ; x2++){
					for (int y2 = y-1 ; y2 < y+2 ; y2++){
						if (outOfBounds(x2,y2))
							continue;
						Field f2 = fields[x2][y2];
						if (!f2.isVisible()){
							left--;
							if (f2.flagged)
								possibleMines--;
						}
					}
				}
				if (left == 0){
					for (int x2 = x-1 ; x2 < x+2 ; x2++){
						for (int y2 = y-1 ; y2 < y+2 ; y2++){
							if (outOfBounds(x2,y2))
								continue;
							Field f2 = fields[x2][y2];
							if (!f2.isVisible()){
								if (!f2.flagged)
									changed = true;
								f2.flagged = true;
							}
						}
					}
				}
				if (possibleMines == 0){
					for (int x2 = x-1 ; x2 < x+2 ; x2++){
						for (int y2 = y-1 ; y2 < y+2 ; y2++){
							if (outOfBounds(x2,y2))
								continue;
							Field f2 = fields[x2][y2];
							if (!f2.isVisible() && !f2.flagged){
								f2.onClick();
								changed = true;
							}
						}
					}
				}
			}
		}
		if (changed)
			return true;
		
		ArrayList<SelectionRestrictor> restrictions = new ArrayList<>();
		for (int x = 0 ; x < w ; x++){
			for (int y = 0 ; y < h ; y++){
				Field f = fields[x][y];
				if (!f.isVisible() || !f.getClass().equals(DangerField.class))
					continue;
				SelectionRestrictor res = new SelectionRestrictor(((DangerField)f).danger);
				for (int x2 = x-1 ; x2 < x+2 ; x2++){
					for (int y2 = y-1 ; y2 < y+2 ; y2++){
						if (outOfBounds(x2,y2))
							continue;
						Field f2 = fields[x2][y2];
						if (!f2.isVisible()){
							if (!f2.flagged)
								res.count--;
							else
								res.addRestriction(x2, y2);
						}
					}
				}
				if (!res.px.isEmpty())
					restrictions.add(res);
			}
		}
		
		int selectedX;
		int selectedY;
		double chance = -1;
		
		ArrayList<Integer> alreadyManaged = new ArrayList<>();
		
		for (SelectionRestrictor res : restrictions){
			for (int i = 0 ; i < res.px.size() ; i++){
				int x = res.px.get(i);
				int y = res.py.get(i);
				
				if (alreadyManaged.contains(x+y*w))
					continue;
				
				ArrayList<SelectionRestrictor> possibleRestrictions = new ArrayList<>();
				ArrayList<Integer> possibleX = new ArrayList<>();
				ArrayList<Integer> possibleY = new ArrayList<>();
				
				for (SelectionRestrictor res2 : restrictions){
					if (res == res2 || !res2.isRelevant(x,y))
						continue;
					possibleRestrictions.add(res2);
				}
				
				for (SelectionRestrictor sr : possibleRestrictions){
					for (int i2 = 0 ; i2 < sr.px.size() ; i2++){
						boolean add = true;
						int testX = sr.px.get(i2);
						int testY = sr.py.get(i2);
						for (int i3 = 0 ; i3 < possibleX.size() ; i3++){
							if (possibleX.get(i3) == testX && possibleY.get(i3) == testY){
								add = false;
								break;
							}
						}
						if (add){
							possibleX.add(testX);
							possibleY.add(testY);
						}
					}
				}
				
				ArrayList<boolean[]> possibilities = new ArrayList<>();
				boolean[] testPossibility = new boolean[possibleX.size()];
				boolean cont = true;
				while (true){
					boolean carry = true;
					for (int i2 = 0 ; i2 < testPossibility.length ; i2++){
						if (carry){
							testPossibility[i2] = !testPossibility[i2];
							if (testPossibility[i2])
								carry = false;
						} else
							break;
					}
					if (carry)
						break;
					boolean applicable = true;
					System.out.println("testPossibility...");
					for (int j = 0 ; j < testPossibility.length ; j++){
						System.out.print(possibleX.get(j)+","+possibleY.get(j)+" ");
					}
					System.out.println();
					for (SelectionRestrictor sr2 : possibleRestrictions){
						if (sr2.isApplicable(possibleX, possibleY, testPossibility)){
							applicable = false;
							break;
						}
					}
					if (applicable)
						possibilities.add(testPossibility);
				}
				int[] trueCount = new int[possibleX.size()];
				int[] falseCount = new int[possibleX.size()];
				for (boolean[] possibility : possibilities){
					for (int i2 = 0 ; i2 < possibility.length ; i2++){
						if (possibility[i2])
							trueCount[i2]++;
						else
							falseCount[i2]++;
					}
				}
				for (int i2 = 0 ; i2 < trueCount.length ; i2++){
					if (trueCount[i2] == 0){
						System.out.println("click on field "+possibleX.get(i2)+", "+possibleY.get(i2)+" false:"+falseCount[i2]);
//						fields[possibleX.get(i2)][possibleY.get(i2)].onClick();
						changed = true;
					} else if (falseCount[i2] == 0){
//						fields[possibleX.get(i2)][possibleY.get(i2)].flagged = true;
						changed = true;
					} else {
						double chance2 = (double)falseCount[i2] / (trueCount[i2]+falseCount[i2]);
						if (chance2 > chance){
							chance = chance2;
							selectedX = possibleX.get(i2);
							selectedY = possibleY.get(i2);
						}
					}
				}
				if (changed)
					return true;
			}
		}
		return false;
	}

	private boolean outOfBounds(int x, int y) {
		return x < 0 || y < 0 || x >= w || y >= h;
	}
}
