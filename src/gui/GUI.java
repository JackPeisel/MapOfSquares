package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
	/** A list of all possible events that can occur at the start of a turn */
	public static final ArrayList<String> randomEvents= new ArrayList<>();
	public ArrayList<String> currentRandomEvents= new ArrayList<>();
	/** object that processes mouse clicks */
	MouseEvents mouseEvent= new MouseEvents();
	/** object that process keystrokes */
	private KeyEvents keyEvent= new KeyEvents();
	/** The JLabel indicating which tribe owns the most recently clicked MapSquare */
	JLabel tribeName= new JLabel("Tribe Name:               ");
	/** The JLabel indicating the population of the most recently clicked MapSquare */
	JLabel squarePop= new JLabel("Province Population:             ");
	/** The JLabel indicating the number of armies on the most recently clicked MapSquare */
	JLabel armyCount= new JLabel("Armies Present:              ");
	/** The JLabel displaying what the computer chose to do with the first move of its turn */
	JLabel aiTurn1= new JLabel("The computer elected to first:FILLER TEXT");
	/** The JLabel displaying what the computer chose to do with the second move of its turn */
	JLabel aiTurn2= new JLabel("Then the computer elected to:FILLER TEXT");
	/** The JLabel displaying what the player most recently rolled in battle; only visible if a battle
	 * has recently occurred */
	JLabel playerRolls= new JLabel("You rolled:");
	/** The JLabel displaying what the computer most recently rolled in battle; only visible if a battle
	 * has recently occurred */
	JLabel aiRolls= new JLabel("Your opponent rolled:");
	/** The JLabel displaying how much gold the player has; exists in the player info box */
	JLabel playerGold= new JLabel("You have 0 gold");
	/** The JLabel displaying the player's name; exists in the player info box */
	JLabel playerNameLabel= new JLabel("");
	/** The MenuButton used to create a new army; found in the infoBox */
	MenuButton buildArmy= new MenuButton("Build a basic army?", 0);
	/** The MenuButton that starts the game from the nation selection screen */
	MenuButton startGame= new MenuButton("START A NEW GAME?", 1);
	/** The MenuButton that appears when the player runs out of moves. Pressing clickTurn ends the
	 * player's turn */
	MenuButton clickTurn= new MenuButton("Next Turn?", 2);
	/** The JLabel containing the image representing the player's nation */
	JLabel nationPic= new JLabel(player.image);
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
	MapSquare currentlySelected= null;
	/** A 2d array of MapSquares containing the layout of the game board */
	public MapSquare[][] board;
	/** The Box containing the game board(ie the MapSquares) */
	Box boardBox= new Box(BoxLayout.Y_AXIS);
	/** The Box containing game info for the player. More specifically it contains information like what
	 * the player and computer roll during battle, buttons to proceed to the next turn and build armies,
	 * and information on the most recently selected square */
	Box infoBox= new Box(BoxLayout.Y_AXIS);
	/** CURRENTLY NOT IMPLEMENTED. The Box containing information on the player */
	Box playerInfoBox= new Box(BoxLayout.Y_AXIS);
	/** The Box containing the all aspects of the game the player plays. In other words, contains the
	 * boardBox, infoBox, and playerInfoBox */
	Box mainGameBox= new Box(BoxLayout.X_AXIS);
	/** A Box contained within infoBox which stores the next turn and build army buttons */
	Box buttonBox= new Box(BoxLayout.Y_AXIS);
	/** A Box contained within infoBox which stores the JLabels that inform the player what the computer
	 * did on its turn. Specifically those JLabels are aiTurn1 and aiTurn2 */
	Box turnBox= new Box(BoxLayout.Y_AXIS);
	/** The Box contained within infoBox that contains the player's and computer's rolls from a battle.
	 * If no battle has taken place recently then this box is not visible */
	Box rollBox= new Box(BoxLayout.Y_AXIS);
	/** The Box containing the nation selection screen */
	Box startBox= new Box(BoxLayout.Y_AXIS);
	/** The Box containing the startButtonBox and the nation's picture */
	Box startChoiceBox= new Box(BoxLayout.X_AXIS);
	/** The Box containing the nation selection buttons */
	Box startButtonBox= new Box(BoxLayout.Y_AXIS);
	/** The Box containing the welcome screen and instructions for the player on how to play the game */
	Box instructionBox= new Box(BoxLayout.Y_AXIS);
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
	/** The BattleInfo object containing methods and information pertaining to the battles that take
	 * place in this GUI and the consequences of these battles on the game state */
	public BattleInfo battleInfo= new BattleInfo(this);

	public GUI() {
		addKeyListener(keyEvent);
		playerName= "Nation1";
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addPlayerInfo();
		BoardInit.initializeBoard(this);
		startBox.setVisible(false);
		mainGameBox.setVisible(false);
		pack();
		setVisible(true);

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
		battleInfo.setArmyOwners();
		grantArmies(npc1);// grant new armies to the npc
		aiTurn(1);        // perform the first action
		battleInfo.setSquareOwners();// update tile ownership
		aiTurn(2);		  // perform the second action
		battleInfo.setSquareOwners();// update tile ownership
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
			battleInfo.setRollLabels();

			decrementMoves();
			currentlySelected= null;
			buildArmy.setVisible(false);
			if (movesRemaining == 0) {
				clickTurn.setVisible(true);
			}
			// NEXT TURN

		}
		if (turn == -1 & mb.TYPE == 1) {
			BoardInit.newInstance(this);
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
			playerNameLabel.setText(playerName);
			player.changeTribe(temp);
			startChoiceBox.remove(nationPic);
			nationPic= new JLabel(player.image);
			startChoiceBox.add(nationPic);
			pack();

		}

	}

	/** Check for whether a random event will occur. If the event should occur then execute the event
	 * call on a random square/group of squares */
	public boolean eventRoll(Tribe tribe) {
		Random rand= new Random();
		if (rand.nextInt(10) != 0) { return false; }
		String eventType= randomEvents.get(rand.nextInt(randomEvents.size()));
		if (EventWindow.isChoice(eventType)) {
			// currently no random choice events(random meaning can happen in any turn in any situation)
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
		LinkedList<MapSquare> adjacents= MapSquare.allAdjacents(origin, this);
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
				origin= MapSquare.randomSquare(playerArmies, 1);
			} else { // tribe==npc1
				origin= MapSquare.randomSquare(aiArmies, 1);
			}
			origin.addArmy(1);
		}
		if (type == "Supply Shortage") {
			if (tribe == player) {
				if (playerArmies.size() <= 2) return;
				origin= MapSquare.randomSquare(playerArmies, 5);
			} else {
				if (aiArmies.size() <= 2) return;
				origin= MapSquare.randomSquare(aiArmies, 5);
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
			battleInfo.setRollLabels();
			occupy(ms);
			battleInfo.setArmyOwners();
			return true;
		}
		int attackHost= battleInfo.howManyAttackers(currentlySelected);
		int defenseHost= battleInfo.howManyDefenders(ms);
		LinkedList<Integer> attackerRolls= battleInfo.rolls(attackHost, true);
		LinkedList<Integer> defenderRolls= battleInfo.rolls(defenseHost, false);
		playerRolls.setText("You rolled: " + attackerRolls);
		aiRolls.setText("Your opponent rolled: " + defenderRolls);
		battleInfo.setRollLabels();
		// Measures the difference between the player's army and the opponent's army
		int whoWon= currentlySelected.army - ms.army;
		// Is the defending tile a mountain?
		boolean feature= ms.feature && ms.pop <= 2;
		if (attackHost > 1 && defenseHost == 2) {
			// 2 or more attacker rolls and 2 defender rolls
			if (feature) {
				battleInfo.battle(attackerRolls.poll() - 1, defenderRolls.poll(), currentlySelected, ms);
				battleInfo.battle(attackerRolls.poll() - 1, defenderRolls.poll(), currentlySelected, ms);
			} else {
				battleInfo.battle(attackerRolls.poll(), defenderRolls.poll(), currentlySelected, ms);
				battleInfo.battle(attackerRolls.poll(), defenderRolls.poll(), currentlySelected, ms);
			}
		} else {
			// Any other combination of rolls
			if (feature) {
				battleInfo.battle(attackerRolls.poll() - 1, defenderRolls.poll(), currentlySelected, ms);
			} else {
				battleInfo.battle(attackerRolls.poll(), defenderRolls.poll(), currentlySelected, ms);

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
		battleInfo.setArmyOwners();

		return true;
	}

	/** an ai army has moved from as into ds, which is occupied by an ally, let the battle commence. If
	 * the invading army is victorious then change the ownership of this square */
	public boolean aiConquest(MapSquare ds, MapSquare as) {
		int attackHost= battleInfo.howManyAttackers(as);
		int defenseHost= battleInfo.howManyDefenders(ds);
		LinkedList<Integer> attackerRolls= battleInfo.rolls(attackHost, false);
		LinkedList<Integer> defenderRolls= battleInfo.rolls(defenseHost, true);
		playerRolls.setText("You rolled: " + defenderRolls);
		aiRolls.setText("Your opponent rolled: " + attackerRolls);
		// Measures the difference between the player's army and the opponent's army
		int whoWon= as.army - ds.army;
		// Is the defending tile a mountain?
		boolean feature= ds.feature && ds.pop <= 2;
		if (attackHost > 1 && defenseHost == 2) {
			// 2 or more attacker rolls and 2 defender rolls
			if (feature) {
				battleInfo.battle(attackerRolls.poll() - 1, defenderRolls.poll(), as, ds);
				battleInfo.battle(attackerRolls.poll() - 1, defenderRolls.poll(), as, ds);
			} else {
				battleInfo.battle(attackerRolls.poll(), defenderRolls.poll(), as, ds);
				battleInfo.battle(attackerRolls.poll(), defenderRolls.poll(), as, ds);
			}

		} else {
			// Any other combination of rolls
			if (feature) {
				battleInfo.battle(attackerRolls.poll() - 1, defenderRolls.poll(), as, ds);
			} else {
				battleInfo.battle(attackerRolls.poll(), defenderRolls.poll(), as, ds);
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
		battleInfo.setArmyOwners();
		return true;
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

		battleInfo.setArmyOwners();
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
			battleInfo.setRollLabels();

			// NEXT TURN
			return;
		}
		playerConquest(ms);
		battleInfo.setArmyOwners();
		BoardInit.endgame(this);

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
			BoardInit.endgame(this);
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
		BoardInit.endgame(this);
		updateInfo(ms);
		invade= 0;

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
			BoardInit.endgame(this);

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
			BoardInit.endgame(this);

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
			BoardInit.endgame(this);

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
		battleInfo.setRollLabels();
		BoardInit.endgame(this);

	}

	/** Return true if a suboptimal turn, a turn where the ai is able to take a decent fight against the
	 * player, is able to be performed, and false otherwise. If the turn is able to be performed then
	 * the turn is performed */
	public boolean battleTurn(Random rand) {
		for (MapSquare ms : aiArmies) {
			ArrayList<MapSquare> adjacents= MapSquare.validAdjacents(ms, 1, this);
			if (adjacents != null) {
				aiConquest(adjacents.get(rand.nextInt(adjacents.size())), ms);
				currentlySelected= ms;
				return true;

			}
		}
		currentlySelected= null;
		for (MapSquare ms : aiArmies) {
			ArrayList<MapSquare> adjacents= MapSquare.validAdjacents(ms, 2, this);
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
			ArrayList<MapSquare> adjacents= MapSquare.validAdjacents(ms, this);
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
			ArrayList<MapSquare> adjacents= MapSquare.validAdjacents(ms, npc1, this);
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

	public static void main(String[] args) {
		GUI c= new GUI();
	}

	/** Class contains a method that responds to a mouse click */
	class MouseEvents extends MouseInputAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (frozen) return;

			Object ob= e.getSource();
			if (ob instanceof MapSquare &&
				SwingUtilities.isLeftMouseButton(e)) {
				processSquareClick((MapSquare) ob);
				battleInfo.setSquareOwners();
				battleInfo.showNext();
				return;
			}
			if (ob instanceof MenuButton) {
				processMenuClick((MenuButton) ob);
				battleInfo.setSquareOwners();
				battleInfo.showNext();
				return;
			}
			if (ob == instructionBox) {
				BoardInit.closeInstructions(GUI.this);
			}
			System.out.println("failure");

		}

	}

	/** Class contains a method that responds to a keystroke */
	class KeyEvents implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
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
