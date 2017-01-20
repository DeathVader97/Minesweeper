package de.FelixPerko.Minesweeper.Fields;

import java.awt.Color;
import java.awt.Graphics;

import de.FelixPerko.Minesweeper.MinesweeperGame;

public class Mine extends Field {

	public Mine(MinesweeperGame game, int x, int y) {
		super(game, x, y);
	}

	@Override
	public void onClick() {
		setVisible(false);
		game.lostGame();
	}

	@Override
	public void draw(Graphics g, int x, int y, int sizeX, int sizeY) {
		g.setColor(new Color(1f,0f,0f));
		g.fillRect(x, y, sizeX, sizeY);
	}
}
