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
	private JLabel squarePop= new JLabel("Province Population:             ");
	private JLabel armyCount= new JLabel("Armies Present:              ");
	private JLabel aiTurn1= new JLabel("The computer elected to first:FILLER TEXT");
	private JLabel aiTurn2= new JLabel("Then the computer elected to:FILLER TEXT");
	private JLabel playerRolls= new JLabel("You rolled:");
	private JLabel aiRolls= new JLabel("Your opponent rolled:");
	private MenuButton buildArmy= new MenuButton("Build a basic army?", 0);
	private MenuButton startGame= new MenuButton("CLICK HERE TO START A NEW GAME", 1);
	private MenuButton clickTurn= new MenuButton("Next Turn?", 2);
	private JLabel nationPic= new JLabel(player.image);
	public static Tribe player= new Tribe("Player1", 1);
	public static Tribe npc1= new Tribe("AI", -1);
	public ArrayList<MapSquare> playerArmies= new ArrayList<>();
	public ArrayList<MapSquare> playerTiles= new ArrayList<>();
	public ArrayList<MapSquare> aiTiles= new ArrayList<>();
	public ArrayList<MapSquare> aiArmies= new ArrayList<>();

	/** The Mapsquare which is currently selected by the player or computer opponent. null if there is
	 * not currently selected square */
	private MapSquare currentlySelected= null;

	MapSquare[][] board;
	private Box boardBox= new Box(BoxLayout.Y_AXIS);
	private Box infoBox= new Box(BoxLayout.Y_AXIS);
	private Box mainGameBox= new Box(BoxLayout.X_AXIS);
	private Box buttonBox= new Box(BoxLayout.Y_AXIS);
	private Box turnBox= new Box(BoxLayout.Y_AXIS);
	private Box rollBox= new Box(BoxLayout.Y_AXIS);
	private Box startBox= new Box(BoxLayout.Y_AXIS);
	private Box startChoiceBox= new Box(BoxLayout.X_AXIS);
	private Box startButtonBox= new Box(BoxLayout.Y_AXIS);
	/** A representation of whose turn it is currently in the game turn 0 means it is the player's turn,
	 * turn 1 means it is the computer's turn. turn= -1 means the game has not started yet */
	public int turn= -1;
	/** The width(in MapSquares) of the game board */
	public int dimensionX= 5;
	/** The height(in MapSquares) of the game board */
	public int dimensionY= 5;
	/** Keeps track of whether the next click the player makes is one with intent to invade if invade is
	 * 1 then the next click is one with intent to invade, if invade is 0 then the next click is not
	 * intended as an invade */
	public int invade= 0;
	/** The number of remaining moves the player has to make */
	public int movesRemaining= 2;

	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addSquares();
		// newInstance(); don't call this here as any start game bonuses will be messed up
		addStart();
		add(startBox, BorderLayout.CENTER);
		mainGameBox.setVisible(false);
		pack();
		setVisible(true);

	}

	/** set up the start menu, includes adding check boxes for colors */
	public void addStart() {
		startGame.addMouseListener(mouseEvent);
		startBox.add(startGame);
		// For later when adding constants, the types are in the 30s because this is the 3rd type of button
		// added
		MenuButton nation1= new MenuButton("PLAY NATION 1", 31);
		MenuButton nation2= new MenuButton("PLAY NATION 2", 32);
		MenuButton nation3= new MenuButton("PLAY NATION 3", 33);
		nation1.addMouseListener(mouseEvent);
		nation2.addMouseListener(mouseEvent);
		nation3.addMouseListener(mouseEvent);
		startButtonBox.add(nation1);
		startButtonBox.add(nation2);
		startButtonBox.add(nation3);
		startChoiceBox.add(startButtonBox);
		startChoiceBox.add(nationPic); // requires it to not be null, ie requires a temp image(nation 1)
		startBox.add(startChoiceBox);
		// startBox.add(Box.createGlue());

	}

	/** Configures the Java Boxes that make up the game. Sets up the game grid and buttons */
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
		infoBox.add(squarePop);
		mainGameBox.add(infoBox);
		buildArmy.addMouseListener(mouseEvent);
		clickTurn.addMouseListener(mouseEvent);
		buttonBox.add(buildArmy);
		buttonBox.add(clickTurn);
		clickTurn.setVisible(false);
		turnBox.add(aiTurn1);
		turnBox.add(aiTurn2);
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

	}

	/** initialize a new game instance */
	public void newInstance() {
		Random rand= new Random();
		int initX= rand.nextInt(dimensionX);
		int initY= rand.nextInt(dimensionY);
		board[initX][initY].changeTribe(player, player.color);
		if (player.type == 1) {
			board[initX][initY].addArmy(1);
		}
		if (player.type == 2) {
			ArrayList<MapSquare> bonus= validAdjacents(board[initX][initY]);
			bonus.get(0).changeTribe(player, player.color);
			bonus.get(0).removeArmy();
			bonus.get(1).changeTribe(player, player.color);
			bonus.get(1).removeArmy();

		}
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
		setArmyOwners();
		grantArmies(npc1);// grant new armies to the npc
		aiTurn(1);        // perform the first action
		setSquareOwners();// update tile ownership
		aiTurn(2);		  // perform the second action
		setSquareOwners();// update tile ownership
		grantArmies(player);// grant new armies to the player
		resetPlayerTurn();  // restore agency to the player
		clickTurn.setVisible(false);
		buttonBox.setVisible(false);

	}

	/** update the info box corresponding to the given square */
	public void updateInfo(MapSquare ms) {
		tribeName.setText("Tribe Name:" + ms.tribe.name + "           ");
		// tribeLeader.setText("Tribe Leader:" + ms.tribe.chief.name + " ");
		armyCount.setText("Armies Present:" + ms.army + "          ");
		squarePop.setText("Province Population:" + ms.pop + "         ");

	}

	/** Process a click on a menu button */
	public void processMenuClick(MenuButton mb) {
		if (turn == 0 && mb.TYPE == 0) {
			currentlySelected.addArmy(1);
			updateInfo(currentlySelected);
			/*
			if (!playerArmies.contains(currentlySelected)) {
				playerArmies.add(currentlySelected);
			}
			*/
			setRollLabels();

			decrementMoves();
			currentlySelected= null;
			buildArmy.setVisible(false);
			if (movesRemaining == 0) {
				clickTurn.setVisible(true);
			}
			// NEXT TURN

		}
		if (turn == -1 & mb.TYPE == 1) {
			newInstance();
			System.out.println("button processed");
			mainGameBox.setVisible(true);
			remove(startBox);
			turn= 0;
			add(mainGameBox, BorderLayout.CENTER);
			pack();

		}
		if (mb.TYPE == 2) {
			turn= 0;
			nextTurn();
		}
		if (mb.TYPE > 29 && mb.TYPE < 40) {
			int temp= mb.TYPE - 30;
			player.changeTribe(temp);
			startChoiceBox.remove(nationPic);
			nationPic= new JLabel(player.image);
			startChoiceBox.add(nationPic);
			pack();
			// System.out.println("nation change test");

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
		LinkedList<Integer> attackerRolls= rolls(attackHost, true);
		LinkedList<Integer> defenderRolls= rolls(defenseHost, false);
		playerRolls.setText("You rolled: " + attackerRolls);
		aiRolls.setText("Your opponent rolled: " + defenderRolls);
		setRollLabels();
		// Is the defending tile a mountain?
		boolean feature= ms.feature && ms.pop <= 2;
		if (attackHost > 1 && defenseHost == 2) {
			// 2 or more attacker rolls and 2 defender rolls
			if (feature) {
				battle(attackerRolls.poll() - 1, defenderRolls.poll(), currentlySelected, ms);
				battle(attackerRolls.poll() - 1, defenderRolls.poll(), currentlySelected, ms);
			} else {
				battle(attackerRolls.poll(), defenderRolls.poll(), currentlySelected, ms);
				battle(attackerRolls.poll(), defenderRolls.poll(), currentlySelected, ms);
			}
		} else {
			// Any other combination of rolls
			if (feature) {
				battle(attackerRolls.poll() - 1, defenderRolls.poll(), currentlySelected, ms);
			} else {
				battle(attackerRolls.poll(), defenderRolls.poll(), currentlySelected, ms);

			}
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

	/** an ai army has moved from as into ds, which is occupied by an ally, let the battle commence. If
	 * the invading army is victorious then change the ownership of this square */
	public boolean aiConquest(MapSquare ds, MapSquare as) {
		int attackHost= howManyAttackers(as);
		int defenseHost= howManyDefenders(ds);
		LinkedList<Integer> attackerRolls= rolls(attackHost, false);
		LinkedList<Integer> defenderRolls= rolls(defenseHost, true);
		playerRolls.setText("You rolled: " + defenderRolls);
		aiRolls.setText("Your opponent rolled: " + attackerRolls);
		// Is the defending tile a mountain?
		boolean feature= ds.feature && ds.pop <= 2;
		if (attackHost > 1 && defenseHost == 2) {
			// 2 or more attacker rolls and 2 defender rolls
			if (feature) {
				battle(attackerRolls.poll() - 1, defenderRolls.poll(), as, ds);
				battle(attackerRolls.poll() - 1, defenderRolls.poll(), as, ds);
			} else {
				battle(attackerRolls.poll(), defenderRolls.poll(), as, ds);
				battle(attackerRolls.poll(), defenderRolls.poll(), as, ds);
			}

		} else {
			// Any other combination of rolls
			if (feature) {
				battle(attackerRolls.poll() - 1, defenderRolls.poll(), as, ds);

			} else {
				battle(attackerRolls.poll(), defenderRolls.poll(), as, ds);

			}
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
				} else if (e.pop >= 10) {
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
				} else if (e.pop >= 10) {
					e.addArmy(1);
					if (!aiArmies.contains(e)) {
						aiArmies.add(e);
					}
				}
			}

		}

		setArmyOwners();
	}

	/** Transfer occupation of ms to the player, and transfer the players armies to this newly conquered
	 * square, ms */
	public void occupy(MapSquare ms) { // add in a check for whether it is the player or opponent occupying
		ms.changeTribe(player, player.color);
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
		if (playerRoll && player.type == 3) {
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
			decrementMoves();
			setRollLabels();

			// NEXT TURN
			return;
		}
		playerConquest(ms);
		setArmyOwners();
		endgame();

		setArmyOwners();
		decrementMoves();
		System.out.println(movesRemaining);

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
			currentlySelected= null;
			return;
		}
		if (turn == 0 && ms.tribe == player && currentlySelected == null) {
			provinceSelect(ms);

		} else if (turn == 0 && ms.tribe == player && currentlySelected == ms &&
			playerArmies.contains(currentlySelected)) {
			// Select the army on ms rather than the province. If this occurs then cease execution of this
			// method
			invade= 1;
			buttonBox.setVisible(false);
			endgame();
			updateInfo(ms);
			return;

		} else if (turn == 0 && ms.tribe != player && currentlySelected != ms) {
			// a non friendly block has been selected so stop showing province interaction buttons
			buttonBox.setVisible(false);
			currentlySelected= null;
		} else {
			// display info about the square
		}
		// ms.addArmy(1, 10);
		endgame();
		updateInfo(ms);
		invade= 0;

	}

	/** returns a list of adjacent, empty squares to square ms */
	public ArrayList<MapSquare> validAdjacents(MapSquare ms) {
		ArrayList<MapSquare> accum= new ArrayList<>();
		for (MapSquare[] a : board) {
			for (MapSquare b : a) {
				if (b != currentlySelected && b.isEmpty() && b.isValidInvade(ms)) accum.add(b);
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
				if (b != currentlySelected && b.army == army && b.isValidInvade(ms)) accum.add(b);
			}

		}
		Random rand= new Random();
		if (accum.size() == 0) return null;
		return accum;

	}

	/** returns a list of adjacent, empty squares to square ms that are not of tribe t */
	public ArrayList<MapSquare> validAdjacents(MapSquare ms, Tribe t) {
		ArrayList<MapSquare> accum= new ArrayList<>();
		for (MapSquare[] a : board) {
			for (MapSquare b : a) {
				if (b.tribe != t && b != currentlySelected && b.isEmpty() && b.isValidInvade(ms)) accum.add(b);

			}

		}
		Random rand= new Random();
		if (accum.size() == 0) return null;
		return accum;
	}

	/** Prepare for the player's turn */
	public void resetPlayerTurn() {
		currentlySelected= null;
		turn= 0;
		movesRemaining= 2;
	}

	/** Calculate and process the ai's turn */
	public void aiTurn(int i) {
		Random rand= new Random();
		if (idealTurn(rand)) {
			if (i == 1) {
				aiTurn1.setText("<html><p>First the computer elected to: Invade an unoccupied square</p></html>");
			} else {
				aiTurn2.setText("<html><p>Then the computer elected to: Invade an unoccupied square</p></html>");
				currentlySelected= null;
			}

			turnBox.setVisible(true);
			endgame();

			return;
		}

		if (battleTurn(rand)) {
			if (i == 1) {
				aiTurn1.setText("<html><p>First the computer elected to: Attack one of your armies </p></html>");

			} else {
				aiTurn2.setText("<html><p>Then the computer elected to: Attack one of your armies </p></html>");
				currentlySelected= null;
			}

			turnBox.setVisible(true);
			endgame();

			return;
		}
		if (emptyInvadeTurn(rand)) {
			if (i == 1) {
				aiTurn1.setText("<html><p>First the computer elected to: Invade an unoccupied square</p></html>");
			} else {
				aiTurn2.setText("<html><p>Then the computer elected to: Invade an unoccupied square</p></html>");
				currentlySelected= null;
			}

			turnBox.setVisible(true);
			endgame();

			return;
		}
		// The computer must resort to building a new army in a random square
		aiArmies.get(rand.nextInt(aiArmies.size())).addArmy(1);
		if (i == 1) {
			aiTurn1.setText("<html><p>First the computer elected to: Build a new army</p></html>");
		} else {
			aiTurn2.setText("<html><p>Then the computer elected to: Build a new army</p></html>");
			currentlySelected= null;
		}
		turnBox.setVisible(true);
		setRollLabels();
		endgame();

	}

	/** Return true if a suboptimal turn, a turn where the ai is able to take a decent fight against the
	 * player, is able to be performed, and false otherwise. If the turn is able to be performed then
	 * the turn is performed */
	public boolean battleTurn(Random rand) {
		for (MapSquare ms : aiArmies) {
			ArrayList<MapSquare> adjacents= validAdjacents(ms, 1);
			if (adjacents != null) {
				aiConquest(adjacents.get(rand.nextInt(adjacents.size())), ms);
				currentlySelected= ms;
				return true;

			}
		}
		currentlySelected= null;
		for (MapSquare ms : aiArmies) {
			ArrayList<MapSquare> adjacents= validAdjacents(ms, 2);
			if (adjacents != null) {
				aiConquest(adjacents.get(rand.nextInt(adjacents.size())), ms);
				currentlySelected= ms;
				return true;

			}
		}

		return false;
	}

	/** Return true if an empty square is able to be invaded, false otherwise. If the turn is able to be
	 * performed then perform it */
	public boolean emptyInvadeTurn(Random rand) {
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
				currentlySelected= ms;
				return true;
			}
		}
		return false;

	}

	/** Return true if an empty, non-allied square is able to be invaded, false otherwise. If the turn
	 * is able to be performed then perform it */
	public boolean idealTurn(Random rand) {
		// the first MapSquare in aiArmies that has a valid and ideal adjacent tile will be used
		for (MapSquare ms : aiArmies) {
			ArrayList<MapSquare> adjacents= validAdjacents(ms, npc1);
			if (adjacents != null) {

				MapSquare idealSquare= adjacents.get(rand.nextInt(adjacents.size()));
				idealSquare.changeTribe(npc1, Color.RED);
				idealSquare.addArmy(ms.army);
				idealSquare.army= idealSquare.army - 1;
				ms.removeArmy();
				aiArmies.remove(ms); // This is probably not optimal, should probably find a way to use set if that's
									 // better
				aiArmies.add(idealSquare);
				currentlySelected= ms;
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
