package de.FelixPerko.Minesweeper;

import java.util.ArrayList;

public class SelectionRestrictor {
	ArrayList<Integer> px = new ArrayList<>();
	ArrayList<Integer> py = new ArrayList<>();
	int count = 0;
	
	public SelectionRestrictor(int count){
		this.count = count;
	}
	
	public void addRestriction(int x, int y){
		px.add(x);
		py.add(y);
	}

	public boolean isRelevant(int x, int y){
		for (int i = 0 ; i < px.size() ; i++){
			if (px.get(i) == x && py.get(i) == y)
				return true;
		}
		return false;
	}

	public boolean isApplicable(ArrayList<Integer> possibleX, ArrayList<Integer> possibleY, boolean[] testPossibility) {
		int testedCount = 0;
		System.out.println();
		System.out.print("isApplicable? "+count+" ");
		for (int i = 0 ; i < px.size() ; i++){
			int testX = px.get(i);
			int testY = py.get(i);
			for (int i2 = 0 ; i2 < possibleX.size() ; i2++){
				if (testX != possibleX.get(i2) || testY != possibleY.get(i2))
					continue;
				System.out.print(possibleX.get(i2)+","+possibleY.get(i2)+" ");
				if (testPossibility[i2])
					testedCount++;
				if (testedCount > count)
					return false;
			}
		}
		System.out.println();
		return testedCount != count;
	}
}
