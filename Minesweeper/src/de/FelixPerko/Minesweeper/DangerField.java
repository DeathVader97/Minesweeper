package de.FelixPerko.Minesweeper;

import java.awt.Color;
import java.awt.Graphics;

import de.FelixPerko.Minesweeper.Fields.Field;

public class DangerField extends Field {
	
	int danger;
	char c;
	
	static char[] chars = new char[]{'1','2','3','4','5','6','7','8'};
	
	public DangerField(MinesweeperGame game, int x, int y, int danger) {
		super(game, x, y);
		this.danger = danger;
		c = chars[danger-1];
	}

	@Override
	public void onClick() {
		setVisible(true);
	}

	@Override
	public void draw(Graphics g, int x, int y, int sizeX, int sizeY) {
		g.setColor(new Color(1f,1f-(danger/10f),1f-(danger/10f)));
		g.fillRect(x, y, sizeX, sizeY);
		g.setColor(new Color(0f,0f,0f));
		writeCharInMiddle(g, x, y, sizeX, sizeY, c+"");
	}

}
