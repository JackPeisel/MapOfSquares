package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import game.Tribe;

public class GUI extends JFrame {
	private static final long serialVersionUID= 1L;
	/** A list of all possible events that can occur at the start of a turn */
	public static final ArrayList<String> randomEvents= new ArrayList<>();
	public ArrayList<String> currentRandomEvents= new ArrayList<>();
	/** object that processes mouse clicks */
	private MouseEvents mouseEvent= new MouseEvents();
	/** object that process keystrokes */
	private KeyEvents keyEvent= new KeyEvents();
	/** The JLabel indicating which tribe owns the most recently clicked MapSquare */
	private JLabel tribeName= new JLabel("Tribe Name:               ");
	/** The JLabel indicating the population of the most recently clicked MapSquare */
	private JLabel squarePop= new JLabel("Province Population:             ");
	/** The JLabel indicating the number of armies on the most recently clicked MapSquare */
	private JLabel armyCount= new JLabel("Armies Present:              ");
	/** The JLabel displaying what the computer chose to do with the first move of its turn */
	private JLabel aiTurn1= new JLabel("The computer elected to first:FILLER TEXT");
	/** The JLabel displaying what the computer chose to do with the second move of its turn */
	private JLabel aiTurn2= new JLabel("Then the computer elected to:FILLER TEXT");
	/** The JLabel displaying what the player most recently rolled in battle; only visible if a battle
	 * has recently occurred */
	private JLabel playerRolls= new JLabel("You rolled:");
	/** The JLabel displaying what the computer most recently rolled in battle; only visible if a battle
	 * has recently occurred */
	private JLabel aiRolls= new JLabel("Your opponent rolled:");
	/** The JLabel displaying how much gold the player has; exists in the player info box */
	private JLabel playerGold= new JLabel("You have 0 gold");
	/** The JLabel displaying the player's name; exists in the player info box */
	private JLabel playerNameLabel= new JLabel("");
	/** The MenuButton used to create a new army; found in the infoBox */
	private MenuButton buildArmy= new MenuButton("Build a basic army?", 0);
	/** The MenuButton that starts the game from the nation selection screen */
	private MenuButton startGame= new MenuButton("CLICK HERE TO START A NEW GAME", 1);
	/** The MenuButton that appears when the player runs out of moves. Pressing clickTurn ends the
	 * player's turn */
	private MenuButton clickTurn= new MenuButton("Next Turn?", 2);
	/** The JLabel containing the image representing the player's nation */
	private JLabel nationPic= new JLabel(player.image);
	/** The player's Tribe */
	public static Tribe player= new Tribe("Player1", 1);
	/** The computer's Tribe */
	public static Tribe npc1= new Tribe("AI", -1);
	/** A list of the MapSquares where player armies are present
	 *
	 * Note: If the player builds a new army on a MapSquare not in playerArmies on their turn, that
	 * MapSquare will not be added to playerArmies until the end of the player's turn */
	public ArrayList<MapSquare> playerArmies= new ArrayList<>();
	/** A list of the MapSquares that the player controls */
	public ArrayList<MapSquare> playerTiles= new ArrayList<>();
	/** A list of the MapSquares that the computer controls */
	public ArrayList<MapSquare> aiTiles= new ArrayList<>();
	/** A list of the MapSquares where computer controlled armies are present */
	public ArrayList<MapSquare> aiArmies= new ArrayList<>();
	/** The Mapsquare which is currently selected by the player or computer opponent. null if there is
	 * not currently selected square */
	private MapSquare currentlySelected= null;
	/** A 2d array of MapSquares containing the layout of the game board */
	public MapSquare[][] board;
	/** The Box containing the game board(ie the MapSquares) */
	private Box boardBox= new Box(BoxLayout.Y_AXIS);
	/** The Box containing game info for the player. More specifically it contains information like what
	 * the player and computer roll during battle, buttons to proceed to the next turn and build armies,
	 * and information on the most recently selected square */
	private Box infoBox= new Box(BoxLayout.Y_AXIS);
	/** CURRENTLY NOT IMPLEMENTED. The Box containing information on the player */
	private Box playerInfoBox= new Box(BoxLayout.Y_AXIS);
	/** The Box containing the all aspects of the game the player plays. In other words, contains the
	 * boardBox, infoBox, and playerInfoBox */
	private Box mainGameBox= new Box(BoxLayout.X_AXIS);
	/** A Box contained within infoBox which stores the next turn and build army buttons */
	private Box buttonBox= new Box(BoxLayout.Y_AXIS);
	/** A Box contained within infoBox which stores the JLabels that inform the player what the computer
	 * did on its turn. Specifically those JLabels are aiTurn1 and aiTurn2 */
	private Box turnBox= new Box(BoxLayout.Y_AXIS);
	/** The Box contained within infoBox that contains the player's and computer's rolls from a battle.
	 * If no battle has taken place recently then this box is not visible */
	private Box rollBox= new Box(BoxLayout.Y_AXIS);
	/** The Box containing the nation selection screen */
	private Box startBox= new Box(BoxLayout.Y_AXIS);
	/** The Box containing the startButtonBox and the nation's picture */
	private Box startChoiceBox= new Box(BoxLayout.X_AXIS);
	/** The Box containing the nation selection buttons */
	private Box startButtonBox= new Box(BoxLayout.Y_AXIS);
	/** The Box containing the welcome screen and instructions for the player on how to play the game */
	private Box instructionBox= new Box(BoxLayout.Y_AXIS);
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
	/** A boolean representing whether the game is frozen(typically by an event) */
	public boolean frozen= false;
	/** The title of the nation the player is playing */
	public String playerName= "";
	/** The TempSquare which holds the player's profile picture */
	public TempSquare playerImage;

	public GUI() {
		addKeyListener(keyEvent);
		playerName= "Nation1";
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addPlayerInfo();
		addSquares();
		// newInstance(); don't call this here as any start game bonuses will be messed up
		addStart();
		addEvents();
		// add(startBox, BorderLayout.CENTER);
		startBox.setVisible(false);
		mainGameBox.setVisible(false);
		addInstructions();
		add(instructionBox, BorderLayout.CENTER);
		pack();
		setVisible(true);

	}

	/** Set up the instructions info screen */
	public void addInstructions() {
		instructionBox.addMouseListener(mouseEvent);
		ImageIcon intro= new ImageIcon("introScreen.png");
		instructionBox.add(new JLabel(intro));

	}

	/** Closes the instruction window and open the character selection screen */
	public void closeInstructions() {
		startBox.setVisible(true);
		remove(instructionBox);
		add(startBox, BorderLayout.CENTER);
		pack();

	}

	/** Set up the player info screen */
	public void addPlayerInfo() {
		/*
		playerNameLabel.setText(playerName);
		playerInfoBox.add(playerNameLabel);
		TempSquare temp= new TempSquare(Color.BLUE);
		playerImage= temp;
		playerInfoBox.add(playerImage);
		playerInfoBox.add(playerGold);
		mainGameBox.add(playerInfoBox);
		*/

	}

	/** Updates the player info box */
	public void updatePlayerInfo() {
		System.out.println("dog");
		if (playerName == "Nation2") {
			System.out.println("Never sell");
			playerImage= new TempSquare(Color.YELLOW);
		} else if (playerName == "Nation" + 3) {
			playerImage= new TempSquare(Color.CYAN);
		}

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

	/** Fill the list randomEvents with the possible names of events that occur randomly. Additionally
	 * set the currentRandomEvents list to be equal to the randomEvents list */
	public void addEvents() {
		addToRandom("Famine");
		addToRandom("Bountiful Harvest");
		addToRandom("Rally to the Flag");
		addToRandom("Supply Shortage");

	}

	/** Add an event to the randomEvents and currentRandomEvents lists */
	public void addToRandom(String event) {
		randomEvents.add(event);
		currentRandomEvents.add(event);

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
		eventRoll(player);
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
			updatePlayerInfo();
			pack();

		}
		if (mb.TYPE == 2) {
			turn= 0;
			nextTurn();
		}
		if (mb.TYPE > 29 && mb.TYPE < 40) {
			int temp= mb.TYPE - 30;
			playerName= "Nation" + temp;
			System.out.println(playerName);
			playerNameLabel.setText(playerName);
			player.changeTribe(temp);
			startChoiceBox.remove(nationPic);
			nationPic= new JLabel(player.image);
			startChoiceBox.add(nationPic);
			pack();
			// System.out.println("nation change test");

		}

	}

	/** Return a MapSquare from list that has army armies present on it. If list does not contain any
	 * MapSquares with army armies present then return the square with the most armies present */
	public MapSquare randomSquare(List<MapSquare> list, int army) {
		for (MapSquare e : list) {
			if (e.army == army) { return e; }
		}
		return mostArmies(list);
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

	/** Check for whether a random event will occur. If the event should occur then execute the event
	 * call on a random square/group of squares */
	public boolean eventRoll(Tribe tribe) {
		Random rand= new Random();
		if (rand.nextInt(10) != 0) { return false; }
		String eventType= randomEvents.get(rand.nextInt(randomEvents.size()));
		if (EventWindow.isChoice(eventType)) {

		} else processRandomEvent(eventType, tribe, rand);

		return true;
	}

	/** Processes a random event with no choices */
	public void processRandomEvent(String type, Tribe tribe, Random rand) {
		MapSquare origin;
		if (tribe == player) {
			origin= playerTiles.get(rand.nextInt(playerTiles.size()));
		} else {
			origin= aiTiles.get(rand.nextInt(aiTiles.size()));
		}
		LinkedList<MapSquare> adjacents= allAdjacents(origin);
		LinkedList<MapSquare> squares= new LinkedList<>();
		squares.add(origin);
		squares.add(adjacents.poll());
		squares.add(adjacents.poll());
		if (type == "Famine") {
			for (MapSquare e : squares) {
				e.decreasePop(2);
			}
		}
		if (type == "Bountiful Harvest") {
			for (MapSquare e : squares) {
				e.increasePop(2);
			}
		}
		if (type == "Rally to the Flag") {
			if (tribe == player) {
				origin= randomSquare(playerArmies, 1);
			} else { // tribe==npc1
				origin= randomSquare(aiArmies, 1);
			}
			origin.addArmy(1);
		}
		if (type == "Supply Shortage") {
			if (tribe == player) {
				if (playerArmies.size() <= 2) return;
				origin= randomSquare(playerArmies, 5);
			} else {
				if (aiArmies.size() <= 2) return;
				origin= randomSquare(aiArmies, 5);
			}
			if (origin.army < 2) {
				origin.setArmy(0);
			} else {
				origin.decreaseArmy(2);
			}

		}
		if (tribe == player) {
			frozen= true;
			EventWindow E= new EventWindow(type, origin, this);
		}

	}

	/** Processes the To Pillage or not to Pillage event given the choice plunder that the player made
	 * If plunder is true then the player chose to pillage, if plunder is false then the player chose
	 * not to pillage */
	public void processPlunder(boolean plunder, MapSquare ms) {
		if (plunder) {
			ms.decreasePop(2);
			// grant the player gold
		} else {
			ms.increasePop(1);
		}

	}

	/** Return true if the player is able to lose armies from this tile via an event, return false
	 * otherwise. The player is able to lose armies iff there are armies to lose, and the player will
	 * not lose the game off the event
	 *
	 * Precondition: currentlySelected is not null */
	public boolean canPlayerLose() {
		if (currentlySelected.army > 0 && playerArmies.size() > 1) { return true; }
		return false;

	}

	/** an allied army has moved into ms, let the battle commence. If the army enters to no resistance
	 * or the invading army is victorious then change the ownership of this square. */
	public boolean playerConquest(MapSquare ms) {
		Random rand= new Random();
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
		// Measures the difference between the player's army and the opponent's army
		int whoWon= currentlySelected.army - ms.army;
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
		// Check whether a "Cowards" event can occur(ie, if the player lost the battle and has multiple
		// armies)
		if (canPlayerLose() && whoWon >= currentlySelected.army - ms.army && rand.nextInt(4) == 0) {
			currentlySelected.decreaseArmy(1);
			frozen= true;
			EventWindow E= new EventWindow("Cowards", currentlySelected, this);

		}
		// Now the battle has commenced, check whether the defender has any more armies left, then move in
		// if possible
		if (ms.army == 0) {
			occupy(ms);
			if (rand.nextInt(4) == 0) {
				// Pillage event
				frozen= true;
				EventWindow E= new EventWindow("To Pillage or not to Pillage", ms, this);
			}
		}
		currentlySelected= null;
		setArmyOwners();

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
		// Measures the difference between the player's army and the opponent's army
		int whoWon= as.army - ds.army;
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
		// Check whether a "Cowards" event can occur(ie, if the computer lost the battle and has multiple
		// armies)
		Random rand= new Random();
		if (aiArmies.size() > 1 && whoWon > as.army - ds.army && rand.nextInt(4) == 0) {
			as.decreaseArmy(1);

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

	/** returns a LinkedList of Mapsquares adjacent to ms */
	public LinkedList<MapSquare> allAdjacents(MapSquare ms) {
		LinkedList<MapSquare> accum= new LinkedList<>();
		for (MapSquare[] a : board) {
			for (MapSquare b : a) {
				if (b.isValidInvade(ms)) {
					accum.add(b);
				}
			}
		}
		return accum;
	}

	/** Return the MapSquare in squares with the greatest number of armies present */
	public MapSquare mostArmies(List<MapSquare> squares) {
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

	/** Class contains a method that responds to a mouse click */
	class MouseEvents extends MouseInputAdapter {
		/** If e is a click in a MapSquare, process it */
		@Override
		public void mouseClicked(MouseEvent e) {
			if (frozen) return;

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
			if (ob == instructionBox) {
				closeInstructions();
			}
			System.out.println("failure");

		}

	}

	/** Class contains a method that responds to a keystroke */
	class KeyEvents implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			System.out.println("in keypressed method");
			char key= e.getKeyChar();
			if (key == 'H' || key == 'h') {
				System.out.println("It just works");
				InstructionWindow iw= new InstructionWindow(GUI.this);
			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
			// filler text
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// filler text
		}

	}

}
