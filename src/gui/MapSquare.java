package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import game.Tribe;

public class MapSquare extends Canvas {
	public static final int EMPTY= 0;  // Constants used to indicate
	public static final int FRIENDLY= 1;    // what is on a square.
	public static final int ENEMY= 2;
	public Tribe nullTribe= new Tribe("kappa", 0);

	/** The color of the MapSquare */
	public Color color;
	/** The tribe in control of the MapSquare */
	public Tribe tribe;
	/** The row of the board the MapSquare is on */
	public int row;
	/** The column of the board the Mapsquare is on */
	public int col;
	/** The number of armies present on the MapSquare */
	public int army;
	/** The population of the province */
	public int pop;
	/** True if mountains and etc are present, false otherwise */
	public boolean feature= coinFlip();
	/** A rectangle representing the MapSquare */
	public Rectangle b;

	/** Initialize a MapSquare at row r and column c */
	public MapSquare(int r, int c) {
		row= r;
		col= c;

		if ((c + r) % 2 == 1) {
			color= Color.DARK_GRAY;
			setBackground(color);

		} else {
			color= Color.GRAY;
			setBackground(color);
		}
		Dimension size= new Dimension(80, 80);
		setSize(size);
		b= getBounds();
		repaint();
		tribe= nullTribe;
		Random rand= new Random();
		pop= rand.nextInt(10) + 1;
	}

	/** Return true approximately half the time, false the other half */
	public boolean coinFlip() {
		return 1 == new Random().nextInt(2);
	}

	/** Paint the square with graphics g */
	@Override
	public void paint(Graphics g) {
		Color save= g.getColor(); // Save the color, to be reset at end

		if (tribe == nullTribe) {
			g.setColor(Color.WHITE);
			if (pop >= 10) {
				g.drawRect(b.width / 3, b.height / 3, b.width / 3, b.height / 3);
			} else if (feature && pop <= 2) {
				// draw a mountain
				g.drawLine(b.x + b.width / 3, b.y + 2 * b.height / 3, b.x + b.width / 2, b.y + b.height / 3);
				g.drawLine(b.x + b.width / 2, b.y + b.height / 3, b.x + 2 * b.width / 3, b.y + 2 * b.height / 3);
			}
			return;
		}

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
		g.setColor(Color.WHITE);
		if (pop >= 10) {
			g.drawRect(b.width / 3, b.height / 3, b.width / 3, b.height / 3);
		} else if (feature && pop <= 2) {
			// draw a mountain
			g.drawLine(b.x + b.width / 3, b.y + 2 * b.height / 3, b.x + b.width / 2, b.y + b.height / 3);
			g.drawLine(b.x + b.width / 2, b.y + b.height / 3, b.x + 2 * b.width / 3, b.y + 2 * b.height / 3);
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

	/** Decreases the pop of the square by x, to a minimum of 0 */
	public void decreasePop(int x) {
		pop= pop - x;
		if (pop < 0) {
			pop= 0;
		}
		repaint();
	}

	/** Increases the pop of the square by x, to a maximum of 10 */
	public void increasePop(int x) {
		pop= pop + x;
		if (pop > 10) {
			pop= 10;
		}
		repaint();
	}

	/** Sets the number of armies present on this MapSquare to newArmy */
	public void setArmy(int newArmy) {
		army= newArmy;
		repaint();
	}

	/** Decreases the number of armies present by x, to a minimum of 0 */
	public void decreaseArmy(int x) {
		army= army - x;
		if (army < 0) {
			army= 0;
		}
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

	/** return true if this tile has no armies present on it */
	public boolean isEmpty() {
		return army == 0;
	}

	/** Processes the To Pillage or not to Pillage event given the choice plunder that the player made
	 * If plunder is true then the player chose to pillage, if plunder is false then the player chose
	 * not to pillage */
	public void processPlunder(boolean plunder) {
		if (plunder) {
			decreasePop(2);
			// grant the player gold
		} else {
			increasePop(1);
		}

	}

	/** returns a list of adjacent, empty squares to square ms in GUI gui */
	public static ArrayList<MapSquare> validAdjacents(MapSquare ms, GUI gui) {
		ArrayList<MapSquare> accum= new ArrayList<>();
		for (MapSquare[] a : gui.board) {
			for (MapSquare b : a) {
				if (b != gui.currentlySelected && b.isEmpty() && b.isValidInvade(ms)) accum.add(b);
			}

		}
		Random rand= new Random();
		if (accum.size() == 0) return null;
		return accum;
	}

	/** returns a square with army armies on it that is adjacent to ms in GUI gui */
	public static ArrayList<MapSquare> validAdjacents(MapSquare ms, int army, GUI gui) {
		ArrayList<MapSquare> accum= new ArrayList<>();
		for (MapSquare[] a : gui.board) {
			for (MapSquare b : a) {
				if (b != gui.currentlySelected && b.army == army && b.isValidInvade(ms)) accum.add(b);
			}

		}
		Random rand= new Random();
		if (accum.size() == 0) return null;
		return accum;

	}

	/** returns a list of adjacent, empty squares to square ms that are not of tribe t in GUI gui */
	public static ArrayList<MapSquare> validAdjacents(MapSquare ms, Tribe t, GUI gui) {
		ArrayList<MapSquare> accum= new ArrayList<>();
		for (MapSquare[] a : gui.board) {
			for (MapSquare b : a) {
				if (b.tribe != t && b != gui.currentlySelected && b.isEmpty() && b.isValidInvade(ms)) accum.add(b);

			}

		}
		Random rand= new Random();
		if (accum.size() == 0) return null;
		return accum;
	}

	/** returns a LinkedList of Mapsquares adjacent to ms in the GUI gui */
	public static LinkedList<MapSquare> allAdjacents(MapSquare ms, GUI gui) {
		LinkedList<MapSquare> accum= new LinkedList<>();
		for (MapSquare[] a : gui.board) {
			for (MapSquare b : a) {
				if (b.isValidInvade(ms)) {
					accum.add(b);
				}
			}
		}
		return accum;
	}

	/** Return the MapSquare in list squares with the greatest number of armies present */
	public static MapSquare mostArmies(List<MapSquare> squares) {
		int accumArmy= 0;
		MapSquare accumSquare= squares.get(0);
		for (MapSquare e : squares) {
			if (accumArmy < e.army) {
				accumSquare= e;
				accumArmy= e.army;
			}

		}
		return accumSquare;
	}

	/** Return a MapSquare from list that has army armies present on it. If list does not contain any
	 * MapSquares with army armies present then return the square with the most armies present */
	public static MapSquare randomSquare(List<MapSquare> list, int army) {
		for (MapSquare e : list) {
			if (e.army == army) { return e; }
		}
		return mostArmies(list);
	}

}