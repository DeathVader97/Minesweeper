package de.FelixPerko.Minesweeper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import de.FelixPerko.Minesweeper.Fields.Field;

public class Main {
	
	public static Main main;
	static MinesweeperGame game;
	
	static BufferedImage img;
	static JLabel label;
	static int w = 1000;
	static int h = 1000;
	
	static int borderX, borderY, sizeX, sizeY;
	
	public static void main(String[] args) {
		main = new Main();
		
		newGame();

		img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		JFrame f = new JFrame();
		label = new JLabel(new ImageIcon(img));
		
		f.add(label);
		f.setSize(w, h);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		f.setUndecorated(true);
		addListener(label);

		borderX = 30;
		borderY = 30;
		sizeX = (w-borderX*2)/game.w;
		sizeY = (h-borderY*2)/game.h;
		
		drawImg();
		while (!Thread.interrupted()){
			game.loop();
		}
	}
	
	public static void newGame(){
		game = new MinesweeperGame(50,50,500);
	}

	public static void drawImg() {
		img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(new Color(0,0,0));
		for (int x = 0 ; x < game.w ; x++){
			for (int y = 0 ; y < game.h ; y++){
				Field f = game.getField(x, y);
				f.drawSuper(g, getDrawX(x), getDrawY(y), sizeX, sizeY);
			}
		}
		for (int x = 0 ; x <= game.w ; x++){
			g.setColor(new Color(0,0,0));
			g.drawLine(x*sizeX+borderX, borderY, x*sizeX+borderX, borderY+sizeY*game.h-1);
		}
		for (int y = 0 ; y <= game.h ; y++){
			g.setColor(new Color(0,0,0));
			g.drawLine(borderX, y*sizeY+borderY, borderX+sizeX*game.w-1, y*sizeY+borderY);
		}
		label.setIcon(new ImageIcon(img));
	}
	
	public static int getDrawX(int x){
		return x*sizeX+borderX;
	}
	
	public static int getDrawY(int y){
		return y*sizeY+borderY;
	}
	
	public static int getFieldX(int drawX){
		return (drawX+8-borderX)/sizeX;
	}
	
	public static int getFieldY(int drawY){
		return (drawY+19-borderY)/sizeY;
	}

	private static void addListener(JLabel label) {
		
		label.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!game.gameRunning)
					return;
				int drawX = e.getX();
				int drawY = e.getY();
				int button = e.getButton();
				int x = getFieldX(drawX);
				int y = getFieldY(drawY);
				if (button == 1){
					if (game.fields[x][y].flagged)
						return;
					game.fields[x][y].onClick();
					drawImg();
				} else if (button == 3){
					boolean wasFlagged = game.fields[x][y].flagged;
					game.fields[x][y].flagged = !wasFlagged;
					if (wasFlagged)
						game.bombsLeft++;
					else
						game.bombsLeft--;
					System.out.println("verbleibende Bomben: "+game.bombsLeft);
					drawImg();
				} else if (button == 2){
					game.continueAutomatic = true;
//					if (game.doStep())
//						drawImg();
//					else
//						System.out.println("didnt find");
				}
			}
		});
	}
	
	public void openGeneratorFrameEnd(boolean won, double time){
		if (won)
			openGeneratorFrame("Spiel gewonnen. Zeit: "+time+" Sekunden");
		else
			openGeneratorFrame("Spiel verloren.");
	}
	
	private void openGeneratorFrame(String msg){
		JFrame f = new JFrame();
		f.setVisible(true);
		
		JTextPane text = new JTextPane();
		text.setText(msg);
		text.setEditable(false);
		JPanel panel = new JPanel();

		String newGameStr = "Neues Spiel";
		JButton newGame = new JButton();
		newGame.setText(newGameStr);
		newGame.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				f.setVisible(false);
				f.dispose();
				newGame();
				drawImg();
			}
		});
		JPanel buttonPanel = new JPanel();
		
		panel.setSize(f.getFontMetrics(f.getFont()).stringWidth(msg), f.getFontMetrics(f.getFont()).getHeight());
		panel.setAlignmentX(0.5f);
		panel.setAlignmentY(0.1f);
		panel.add(text);
		panel.setSize(f.getFontMetrics(f.getFont()).stringWidth(msg), f.getFontMetrics(f.getFont()).getHeight());

		buttonPanel.setAlignmentX(0.5f);
		buttonPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		buttonPanel.add(newGame);
		
		f.setSize(300,400);
		f.add(panel);
		f.add(buttonPanel);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
