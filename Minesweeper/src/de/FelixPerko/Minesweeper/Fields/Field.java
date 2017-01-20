package de.FelixPerko.Minesweeper.Fields;

import java.awt.Color;
import java.awt.Graphics;

import de.FelixPerko.Minesweeper.MinesweeperGame;

public abstract class Field {
	
	public abstract void onClick();
	public abstract void draw(Graphics g, int x, int y, int sizeX, int sizeY);
	
	int x,y;
	public Color color;
	protected MinesweeperGame game;
	private boolean visible = false;
	public boolean flagged;
	
	public Field(MinesweeperGame game, int x, int y) {
		this.game = game;
		this.x = x;
		this.y = y;
	}

	public void drawSuper(Graphics g, int x, int y, int sizeX, int sizeY) {
		if (visible)
			draw(g,x,y,sizeX,sizeY);
		else{
			if (flagged){
				if (game.gameRunning)
					g.setColor(new Color(0f,0f,1f));
				else if (Mine.class.isInstance(this))
					g.setColor(new Color(0f,1f,0f));
				else
					g.setColor(new Color(1f,0f,1f));
			} else
				g.setColor(new Color(0.5f,0.5f,0.5f));
			g.fillRect(x, y, sizeX, sizeY);
		}
	}
	
	protected void writeCharInMiddle(Graphics g, int x, int y, int sizeX, int sizeY, String s){
		g.drawString(s, x+sizeX/2-g.getFontMetrics().stringWidth(s)/3, y+sizeY/2+g.getFontMetrics().getHeight()/3);
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean count) {
		if (game.gameRunning && count && !visible)
			game.countBlock();
		this.visible = true;
	}
}
