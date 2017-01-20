package de.FelixPerko.Minesweeper.Fields;

import java.awt.Graphics;

import de.FelixPerko.Minesweeper.Main;
import de.FelixPerko.Minesweeper.MinesweeperGame;

public class DefaultField extends Field{

	public DefaultField(MinesweeperGame game, int x, int y) {
		super(game, x, y);
	}

	@Override
	public void onClick() {
		if (!game.hasGenerated){
			game.generate(x, y);
			if (!game.getField(x, y).getClass().equals(DefaultField.class)){
				game.getField(x, y).setVisible(true);
				Main.drawImg();
				return;
			}
		}
		if (isVisible())
			return;
		setDefaultFieldVisible();
		Main.drawImg();
	}
	
	public void setDefaultFieldVisible(){
		setVisible(true);
		for (int ox = -1 ; ox < 2 ; ox++){
			for (int oy = -1 ; oy < 2 ; oy++){
				if (ox == 0 && oy == 0)
					continue;
				int x2 = x+ox;
				int y2 = y+oy;
				Field f = game.getField(x2, y2);
				if (f == null || f.isVisible())
					continue;
				if (f.getClass().equals(DefaultField.class)){
					((DefaultField)f).setDefaultFieldVisible();
				}
				f.setVisible(true);
			}
		}
	}

	@Override
	public void draw(Graphics g, int x, int y, int sizeX, int sizeY) {
		// TODO Auto-generated method stub
		
	}

}
