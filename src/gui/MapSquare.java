package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import game.Tribe;

public class MapSquare extends Canvas {
	public static final int EMPTY= 0;  // Constants used to indicate
	public static final int FRIENDLY= 1;    // what is on a square.
	public static final int ENEMY= 2;
	public Tribe nullTribe= new Tribe("kappa");

	public Color color;
	public Tribe tribe;
	public int row;
	public int col;
	public int army;
	public Rectangle b;

	public MapSquare(int r, int c) {
		row= r;
		col= c;

		if ((c + r) % 2 == 1) {
			setBackground(Color.DARK_GRAY);
			color= Color.DARK_GRAY;
		} else {
			setBackground(Color.GRAY);
			color= Color.GRAY;
		}
		Dimension size= new Dimension(40, 40);
		setSize(size);
		b= getBounds();
		repaint();
		tribe= nullTribe;
	}

	/** Paint the square with graphics g */
	@Override
	public void paint(Graphics g) {
		if (tribe == nullTribe) return;

		Color save= g.getColor(); // Save the color, to be reset at end

		if (tribe == GUI.player) {
			g.setColor(Color.GREEN);
			setBackground(color);
		} else {
			g.setColor(Color.GREEN);
			setBackground(Color.RED);
		}
		if (army > 0) {
			g.drawString("" + army, b.x, b.y + b.height / 3);
		}
		g.drawRect(b.x, b.y, b.width, b.height);

		g.setColor(save);

	}

	/** Adds an army to this square */
	public void addArmy(int amount) {
		// if (type != foe) return; work on this line later, may not need depending on how tile selection
		// will work
		army= army + amount;
		repaint();

	}

	/** change the tribe(ownership) of the square to Tribe t */
	public void changeTribe(Tribe t, Color c) {
		tribe= t;
		color= c;
		addArmy(1);
		repaint();
	}

	/** Removes the army from this square */
	public void removeArmy() {
		army= 0;
		repaint();
	}

	/** return true if this is a valid square for the army at root to invade, ie is this a square
	 * adjacent to root */
	public boolean isValidInvade(MapSquare root) {
		// TODO Auto-generated method stub
		if (root.row == row && (root.col == col + 1 || root.col == col - 1) ||
			root.col == col && (root.row == row + 1 || root.row == row - 1)) {
			return true;
		}
		return false;
	}

	/** return true if this tile is empty */
	public boolean isEmpty() {
		return army == 0;
	}

}