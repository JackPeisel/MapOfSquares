package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import game.Tribe;

public class GUI extends JFrame {
	private static final long serialVersionUID= 1L;
	private MouseEvents mouseEvent= new MouseEvents(); // object that processes mouse clicks
	private JLabel tribeName= new JLabel("Tribe Name:               ");
//	private JLabel tribeLeader= new JLabel("Tribe Leader:             ");
	private JLabel armyCount= new JLabel("Square Population:              ");
	private JLabel aiTurn= new JLabel("The computer elected to:FILLER TEXT");
	private JLabel playerRolls= new JLabel("You rolled:");
	private JLabel aiRolls= new JLabel("Your opponent rolled:");
	private MenuButton buildArmy= new MenuButton("Build a basic army?", 0);
	private MenuButton startGame= new MenuButton("CLICK HERE TO START A NEW GAME", 1);
	private MenuButton clickTurn= new MenuButton("Are you ready for the next turn?", 2);
	public static Tribe player= new Tribe("Player1");
	public static Tribe npc1= new Tribe("AI");
	public ArrayList<MapSquare> playerArmies= new ArrayList<>();
	public ArrayList<MapSquare> playerTiles= new ArrayList<>();
	public ArrayList<MapSquare> aiTiles= new ArrayList<>();
	public ArrayList<MapSquare> aiArmies= new ArrayList<>();

	private MapSquare currentlySelected= null;

	MapSquare[][] board;
	private Box boardBox= new Box(BoxLayout.Y_AXIS);
	private Box infoBox= new Box(BoxLayout.Y_AXIS);
	private Box mainGameBox= new Box(BoxLayout.X_AXIS);
	private Box buttonBox= new Box(BoxLayout.Y_AXIS);
	private Box turnBox= new Box(BoxLayout.Y_AXIS);
	private Box rollBox= new Box(BoxLayout.Y_AXIS);
	private Box startBox= new Box(BoxLayout.Y_AXIS);
	public int turn= 0; // turn 0 means it is the player's turn, turn 1 means it is the computer's turn
	public int dimensionX= 5; // these give the dimensions of the map
	public int dimensionY= 5;
	public int invade= 0; // if invade is 1 then the next click on a square means the intent is to invade
	public int movesRemaining= 2; // the number of moves remaining left for the player

	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addSquares();
		startGame.addMouseListener(mouseEvent);
		startBox.add(startGame);
		add(startBox, BorderLayout.CENTER);
		mainGameBox.setVisible(false);
		pack();
		setVisible(true);

	}

	/** set up the start menu, includes adding check boxes for colors */
	public void addStart() {

	}

	public void addSquares() {
		board= new MapSquare[dimensionX][dimensionY];
		// invariant: rows 0..i-1 have been set up
		for (int i= 0; i != dimensionX; i= i + 1) {
			// Set up row i of the board.
			Box row= new Box(BoxLayout.X_AXIS);

			// inv: board[i][0..j-1] have been set up
			for (int j= 0; j != dimensionY; j= j + 1) {
				board[i][j]= new MapSquare(i, j);
				row.add(board[i][j]);
				board[i][j].addMouseListener(mouseEvent);
			}
			boardBox.add(row);
		}
		mainGameBox.add(boardBox);

		infoBox.add(tribeName);
		// infoBox.add(tribeLeader);
		infoBox.add(armyCount);
		mainGameBox.add(infoBox);
		buildArmy.addMouseListener(mouseEvent);
		clickTurn.addMouseListener(mouseEvent);
		buttonBox.add(buildArmy);
		buttonBox.add(clickTurn);
		clickTurn.setVisible(false);
		turnBox.add(aiTurn);
		infoBox.add(turnBox);
		infoBox.add(buttonBox);
		rollBox.add(playerRolls);
		rollBox.add(aiRolls);

		infoBox.add(Box.createGlue());
		infoBox.add(turnBox);
		infoBox.add(rollBox);
		turnBox.setVisible(false);
		buttonBox.setVisible(false);
		rollBox.setVisible(false);
		newInstance();

	}

	/** initialize a new game instance */
	public void newInstance() {
		Random rand= new Random();
		int initX= rand.nextInt(dimensionX);
		int initY= rand.nextInt(dimensionY);
		board[initX][initY].changeTribe(player, Color.BLUE);
		int aix= rand.nextInt(dimensionX);
		int aiy= rand.nextInt(dimensionY);
		while (aix == initX && aiy == initY) {
			aix= rand.nextInt(dimensionX);
			aiy= rand.nextInt(dimensionY);
		}
		board[aix][aiy].changeTribe(npc1, Color.RED);
		setArmyOwners();

	}

	/** Decrease the number of the moves the player has remaining by 1. If this results in no more moves
	 * remaining then set turn to 0 and display the next turn button */
	public void decrementMoves() {
		movesRemaining-- ;
		if (movesRemaining == 0) {
			turn= 1;
			clickTurn.setVisible(true);
			buttonBox.setVisible(true);
		}

	}

	/** Process the ai's turn and surrounding events */
	public void nextTurn() {
		aiTurn();
		clickTurn.setVisible(false);
		buttonBox.setVisible(false);

	}

	/** update the info box corresponding to the given square */
	public void updateInfo(MapSquare ms) {
		tribeName.setText("Tribe Name:" + ms.tribe.name + "           ");
		// tribeLeader.setText("Tribe Leader:" + ms.tribe.chief.name + " ");
		armyCount.setText("Square Population:" + ms.army + "          ");

	}

	/** Process a click on a menu button */
	public void processMenuClick(MenuButton mb) {
		if (turn == 0 && mb.TYPE == 0) {
			currentlySelected.addArmy(1);
			updateInfo(currentlySelected);
			if (!playerArmies.contains(currentlySelected)) {
				playerArmies.add(currentlySelected);
			}
			setRollLabels();

			turn= 1;
			currentlySelected= null;
			buildArmy.setVisible(false);
			clickTurn.setVisible(true);
			// NEXT TURN

		}
		if (turn == 0 & mb.TYPE == 1) {
			System.out.println("button processed");
			mainGameBox.setVisible(true);
			remove(startBox);
			add(mainGameBox, BorderLayout.CENTER);
			pack();

		}
		if (mb.TYPE == 2) {
			turn= 0;
			nextTurn();
		}

	}

	/** Sets the roll labels to invisible if they are not already invisible */
	public void setRollLabels() {
		if (rollBox.isVisible()) {
			rollBox.setVisible(false);
		}
	}

	/** calculate how many units the defender of ms can utilize in defense */
	public int howManyDefenders(MapSquare ms) {
		if (ms.army == 0) return 0;
		if (ms.army == 1) return 1;
		return 2;
	}

	/** calculate how many units an attacker from ms can utilize in attack */
	public int howManyAttackers(MapSquare ms) {
		if (ms.army == 0) return 0;
		if (ms.army > 2) return 3;
		return ms.army;
	}

	/** an allied army has moved into ms, let the battle commence. If the army enters to no resistance
	 * or the invading army is victorious then change the ownership of this square. */
	public boolean playerConquest(MapSquare ms) {
		if (ms.army == 0) {
			setRollLabels();
			occupy(ms);
			setArmyOwners();
			return true;
		}
		int attackHost= howManyAttackers(currentlySelected);
		int defenseHost= howManyDefenders(ms);
		LinkedList<Integer> attackerRolls= rolls(attackHost);
		LinkedList<Integer> defenderRolls= rolls(defenseHost);
		playerRolls.setText("You rolled: " + attackerRolls);
		aiRolls.setText("Your opponent rolled: " + defenderRolls);
		setRollLabels();
		if (attackHost > 1 && defenseHost == 2) {
			// 2 or more attacker rolls and 2 defender rolls
			battle(attackerRolls.poll(), defenderRolls.poll(), currentlySelected, ms);
			battle(attackerRolls.poll(), defenderRolls.poll(), currentlySelected, ms);
		} else {
			// Any other combination of rolls
			battle(attackerRolls.poll(), defenderRolls.poll(), currentlySelected, ms);
		}
		// Now the battle has commenced, check whether the defender has any more armies left, then move in
		// if possible
		if (ms.army == 0) {
			occupy(ms);
			setArmyOwners();
		}
		currentlySelected= null;

		return true;
	}

	/** an ai army has moved into ds from as , which is occupied by an ally, let the battle commence. If
	 * the invading army is victorious then change the ownership of this square */
	public boolean aiConquest(MapSquare ds, MapSquare as) {
		int attackHost= howManyAttackers(as);
		int defenseHost= howManyDefenders(ds);
		LinkedList<Integer> attackerRolls= rolls(attackHost);
		LinkedList<Integer> defenderRolls= rolls(defenseHost);
		playerRolls.setText("You rolled: " + defenderRolls);
		aiRolls.setText("Your opponent rolled: " + attackerRolls);
		if (attackHost > 1 && defenseHost == 2) {
			// 2 or more attacker rolls and 2 defender rolls
			battle(attackerRolls.poll(), defenderRolls.poll(), as, ds);
			battle(attackerRolls.poll(), defenderRolls.poll(), as, ds);
		} else {
			// Any other combination of rolls
			battle(attackerRolls.poll(), defenderRolls.poll(), as, ds);
		}
		if (ds.army == 0) {
			ds.changeTribe(npc1, Color.RED);
			ds.addArmy(as.army);
			as.removeArmy();
			ds.army= ds.army - 1;
		}
		setArmyOwners();
		return true;
	}

	/** calculate and correctly set the arrays containing the player and computer controlled squares */
	public void setSquareOwners() {
		ArrayList<MapSquare> playerAccum= new ArrayList<>();
		ArrayList<MapSquare> aiAccum= new ArrayList<>();
		for (MapSquare[] a : board) {
			for (MapSquare b : a) {
				if (b.tribe == player) {
					playerAccum.add(b);
				} else if (b.tribe == npc1) {
					aiAccum.add(b);
				}
			}

		}

		playerTiles= playerAccum;
		aiTiles= aiAccum;
	}

	/** correct the arrays containing the player and computer controled armies */
	public void setArmyOwners() {
		ArrayList<MapSquare> playerAccum= new ArrayList<>();
		ArrayList<MapSquare> aiAccum= new ArrayList<>();
		for (MapSquare[] a : board) {
			for (MapSquare b : a) {
				if (b.tribe == player && b.army > 0) {
					playerAccum.add(b);
				} else if (b.tribe == npc1 && b.army > 0) {
					aiAccum.add(b);
				}
			}

		}
		playerArmies= playerAccum;
		aiArmies= aiAccum;

	}

	/** Grant the tribe in question new units at the start of their turn */
	public void grantArmies(Tribe tribe) {
		Random rand= new Random();

		if (tribe == player) {
			for (MapSquare e : playerTiles) {
				int i= rand.nextInt(20);
				if (i < 2) {
					e.addArmy(1);
					if (!playerArmies.contains(e)) {
						playerArmies.add(e);
					}
				}
			}

		}
		if (tribe == npc1) {
			for (MapSquare e : aiTiles) {
				int i= rand.nextInt(20);
				if (i < 2) {
					e.addArmy(1);
					if (!aiArmies.contains(e)) {
						aiArmies.add(e);
					}
				}
			}

		}

		setArmyOwners();
	}

	public void occupy(MapSquare ms) { // add in a check for whether it is the player or opponent occupying
		ms.changeTribe(currentlySelected.tribe, Color.BLUE);
		ms.addArmy(currentlySelected.army);
		currentlySelected.removeArmy();
		ms.army= ms.army - 1;
		currentlySelected= null;
	}

	/** Given an attacker from as of roll attack, and a defender from ds of roll defend, decide the
	 * victor of the battle and deal out the casualties */
	public void battle(int attack, int defend, MapSquare as, MapSquare ds) {
		if (!rollBox.isVisible()) {
			rollBox.setVisible(true);
		}
		if (attack > defend) ds.army= ds.army - 1;
		else as.army= as.army - 1;
		as.repaint();
		ds.repaint();
	}

	/** return a list of the top values from n dice rolls. If the list of top values is of length
	 * greater than 0 then the first int is the largest int.
	 *
	 * Precondition: n is equal to either 1, 2, or 3 */
	public LinkedList<Integer> rolls(int n) {
		Random rand= new Random();
		LinkedList<Integer> chances= new LinkedList<>();
		LinkedList<Integer> topValues= new LinkedList<>();
		for (int i= 0; i < n; i++ ) {
			chances.add(rand.nextInt(6) + 1);
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

	/** Move the currentlySelected square's army to square ms */
	public void processArmyMove(MapSquare ms) {
		if (ms.tribe == player) {
			ms.addArmy(currentlySelected.army);
			currentlySelected.removeArmy();
			playerArmies.remove(currentlySelected);
			if (!playerArmies.contains(ms)) {
				playerArmies.add(ms);
			}
			currentlySelected= null;
			turn= 1;
			setRollLabels();

			// NEXT TURN
			return;
		}
		playerConquest(ms);
		setArmyOwners();
		endgame();

		setArmyOwners();
		turn= 1;

	}

	/** display the build army button and set the gui able to respond to the buttons press */
	public void provinceSelect(MapSquare ms) {
		buttonBox.setVisible(true);
		buildArmy.setVisible(true);
		currentlySelected= ms;

	}

	/** Process a click on square ms */
	private void processSquareClick(MapSquare ms) {
		if (turn == 0 && invade == 1 && ms != null && currentlySelected != null &&
			ms.isValidInvade(currentlySelected)) {
			invade= 0;
			processArmyMove(ms);
			updateInfo(ms);
			return;
		}
		if (turn == 0 && ms.tribe == player && currentlySelected == null) {// must change this since need to ask user
			// which they want
			// Select the square, so call a method to handle the second click
			provinceSelect(ms);

		} else if (turn == 0 && ms.tribe == player && currentlySelected == ms && currentlySelected.army > 0) {
			// Select the army on ms rather than the province
			invade= 1;
			buttonBox.setVisible(false);

		} else if (turn == 0 && ms.tribe != player && currentlySelected != ms) {
			// a non friendly block has been selected so stop showing province interaction buttons
			buttonBox.setVisible(false);
			currentlySelected= null;
			turn= 0;
		} else {
			// display info about the square
		}
		// ms.addArmy(1, 10);
		endgame();
		updateInfo(ms);

	}

	/** returns an adjacent, empty square to square ms */
	public ArrayList<MapSquare> validAdjacents(MapSquare ms) {
		ArrayList<MapSquare> accum= new ArrayList<>();
		for (MapSquare[] a : board) {
			for (MapSquare b : a) {
				if (b.isEmpty() && b.isValidInvade(ms)) accum.add(b);
			}

		}
		Random rand= new Random();
		if (accum.size() == 0) return null;
		return accum;
	}

	/** returns a square with army armies on it that is adjacent to ms */
	public ArrayList<MapSquare> validAdjacents(MapSquare ms, int army) {
		ArrayList<MapSquare> accum= new ArrayList<>();
		for (MapSquare[] a : board) {
			for (MapSquare b : a) {
				if (b.army == army && b.isValidInvade(ms)) accum.add(b);
			}

		}
		Random rand= new Random();
		if (accum.size() == 0) return null;
		return accum;

	}

	/** Calculate and process the ai's turn */
	public void aiTurn() {
		grantArmies(npc1);
		Random rand= new Random();
		if (idealTurn(rand)) {
			turnBox.setVisible(true);
			endgame();
			grantArmies(player);
			turn= 0;
			return;
		}
		if (battleTurn(rand)) {
			aiTurn.setText("<html><p>The computer has elected to: Attack one of your armies </p></html>");
			turnBox.setVisible(true);
			endgame();
			grantArmies(player);
			turn= 0;
			return;
		}
		// The computer must resort to building a new army in a random square
		aiArmies.get(rand.nextInt(aiArmies.size())).addArmy(1);
		aiTurn.setText("<html><p>The computer has elected to: Build a new army</p></html>");
		turnBox.setVisible(true);
		setRollLabels();
		endgame();
		grantArmies(player);
		turn= 0;

	}

	/** Return true if a suboptimal turn, a turn where the ai is able to take a decent fight against the
	 * player, is able to be performed, and false otherwise. If the turn is able to be performed then
	 * the turn is performed */
	public boolean battleTurn(Random rand) {
		for (MapSquare ms : aiArmies) {
			ArrayList<MapSquare> adjacents= validAdjacents(ms, 1);
			if (adjacents != null) {
				aiConquest(adjacents.get(rand.nextInt(adjacents.size())), ms);
				return true;

			}
		}
		for (MapSquare ms : aiArmies) {
			ArrayList<MapSquare> adjacents= validAdjacents(ms, 2);
			if (adjacents != null) {
				aiConquest(adjacents.get(rand.nextInt(adjacents.size())), ms);
				return true;

			}
		}

		return false;
	}

	/** Return true if the ideal turn is able to be performed, false otherwise. If the turn is able to
	 * be performed then perform it */
	public boolean idealTurn(Random rand) {
		// the first MapSquare in aiArmies that has a valid and ideal adjacent tile will be used
		for (MapSquare ms : aiArmies) {
			ArrayList<MapSquare> adjacents= validAdjacents(ms);
			if (adjacents != null) {

				MapSquare idealSquare= adjacents.get(rand.nextInt(adjacents.size()));
				idealSquare.changeTribe(npc1, Color.RED);
				idealSquare.addArmy(ms.army);
				idealSquare.army= idealSquare.army - 1;
				ms.removeArmy();
				aiArmies.remove(ms); // This is probably not optimal, should probably find a way to use set if that's
									 // better
				aiArmies.add(idealSquare);
				aiTurn.setText("<html><p>The computer has elected to: Invade an unoccupied square</p></html>");
				return true;
			}
		}
		return false;

	}

	/** Checks whether one of the players is out of armies and has thus lost, and thus takes appropriate
	 * action */
	public void endgame() {
		if (playerArmies.size() == 0) {
			// the player has lost
			VictoryScreen c= new VictoryScreen(false);
			setVisible(false);
			dispose();

		}
		if (aiArmies.size() == 0) {
			// the computer has lost
			VictoryScreen c= new VictoryScreen(true);
			setVisible(false);
			dispose();

		}

	}

	/** display the next turn button if it should be shown */
	public void showNext() {
		if (turn == 1) {
			buildArmy.setVisible(false);
			clickTurn.setVisible(true);
			buttonBox.setVisible(true);

		}
	}

	public static void main(String[] args) {
		GUI c= new GUI();
	}

	/** Class contains a method that responds to a mouse click in a CheckerSquare */
	class MouseEvents extends MouseInputAdapter {
		/** If e is a click in a MapSquare, process it */
		@Override
		public void mouseClicked(MouseEvent e) {

			Object ob= e.getSource();
			if (ob instanceof MapSquare &&
				SwingUtilities.isLeftMouseButton(e)) {
				processSquareClick((MapSquare) ob);
				setSquareOwners();
				showNext();
				return;
			}
			if (ob instanceof MenuButton) {
				processMenuClick((MenuButton) ob);
				setSquareOwners();
				showNext();
				return;
			}
			System.out.println("failure");

		}

	}

}
