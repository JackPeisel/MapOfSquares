package gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class BattleInfo {

	/** The GUI that owns this BattleInfo object. All called methods will use the fields and information
	 * of root. Root may be manipulated by certain methods of this object */
	public GUI root;

	/** Initialize a BattleInfo with root field of gui */
	public BattleInfo(GUI gui) {
		root= gui;
	}

	/** Sets root's roll labels to invisible if they are not already invisible */
	public void setRollLabels() {
		if (root.rollBox.isVisible()) {
			root.rollBox.setVisible(false);
		}
	}

	/** calculate how many units the defender of MapSquare ms can utilize in defense */
	public int howManyDefenders(MapSquare ms) {
		if (ms.army == 0) return 0;
		if (ms.army == 1) return 1;
		return 2;
	}

	/** calculate how many units an attacker from MapSquare ms can utilize in attack */
	public int howManyAttackers(MapSquare ms) {
		if (ms.army == 0) return 0;
		if (ms.army > 2) return 3;
		return ms.army;
	}

	/** Given an attacker from as of roll attack, and a defender from ds of roll defend, decide the
	 * victor of the battle and deal out the casualties */
	public void battle(int attack, int defend, MapSquare as, MapSquare ds) {
		if (!root.rollBox.isVisible()) {
			root.rollBox.setVisible(true);
		}
		if (attack > defend) ds.army= ds.army - 1;
		else as.army= as.army - 1;
		as.repaint();
		ds.repaint();
	}

	/** return a list of the top values from n dice rolls. If the list of top values is of length
	 * greater than 0 then the first int is the largest int. boolean attack dictates whether these rolls
	 * are for the player or not
	 *
	 * Precondition: n is equal to either 1, 2, or 3 */
	public LinkedList<Integer> rolls(int n, boolean playerRoll) {
		Random rand= new Random();
		LinkedList<Integer> chances= new LinkedList<>();
		LinkedList<Integer> topValues= new LinkedList<>();
		for (int i= 0; i < n; i++ ) {
			chances.add(rand.nextInt(6) + 1);
		}
		if (playerRoll && root.player.type == 3) {
			chances.add(rand.nextInt(6));
		}
		if (n == 1) {
			topValues.add(maxValue(chances));
		} else {
			topValues.add(maxValue(chances));
			topValues.add(maxValue(chances));
		}
		return topValues;
	}

	/** return the maximum value of the LinkedList<Integer> b and remove it from the list */
	public int maxValue(LinkedList<Integer> b) {
		int accum= 0;
		for (int a : b) {
			if (a > accum) accum= a;
		}
		b.removeFirstOccurrence(accum);
		return accum;
	}

	/** display the next turn button in root if it should be shown */
	public void showNext() {
		if (root.turn == 1) {
			root.buildArmy.setVisible(false);
			root.clickTurn.setVisible(true);
			root.buttonBox.setVisible(true);

		}
	}

	/** calculate and correctly set the array of root that contain the player and computer controlled
	 * squares */
	public void setSquareOwners() {
		ArrayList<MapSquare> playerAccum= new ArrayList<>();
		ArrayList<MapSquare> aiAccum= new ArrayList<>();
		for (MapSquare[] a : root.board) {
			for (MapSquare b : a) {
				if (b.tribe == root.player) {
					playerAccum.add(b);
				} else if (b.tribe == root.npc1) {
					aiAccum.add(b);
				}
			}

		}

		root.playerTiles= playerAccum;
		root.aiTiles= aiAccum;
	}

	/** correct the arrays of root that contain the player and computer controlled armies */
	public void setArmyOwners() {
		ArrayList<MapSquare> playerAccum= new ArrayList<>();
		ArrayList<MapSquare> aiAccum= new ArrayList<>();
		for (MapSquare[] a : root.board) {
			for (MapSquare b : a) {
				if (b.tribe == root.player && b.army > 0) {
					playerAccum.add(b);
				} else if (b.tribe == root.npc1 && b.army > 0) {
					aiAccum.add(b);
				}
			}

		}
		root.playerArmies= playerAccum;
		root.aiArmies= aiAccum;

	}
}
