package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class BoardInit {

	/** Initializes the GUI gui by adding the start screens, MapSquares, and various Boxes */
	public static void initializeBoard(GUI gui) {
		addEvents(gui);
		addSquares(gui);
		addStart(gui);
		addInstructions(gui);

	}

	/** initialize a new game instance for gui */
	public static void newInstance(GUI gui) {
		Random rand= new Random();
		int initX= rand.nextInt(gui.dimensionX);
		int initY= rand.nextInt(gui.dimensionY);
		gui.board[initX][initY].changeTribe(gui.player, gui.player.color);
		if (gui.player.type == 1) {
			gui.board[initX][initY].addArmy(1);
		}
		if (gui.player.type == 2) {
			ArrayList<MapSquare> bonus= MapSquare.validAdjacents(gui.board[initX][initY], gui);
			bonus.get(0).changeTribe(gui.player, gui.player.color);
			bonus.get(0).removeArmy();
			bonus.get(1).changeTribe(gui.player, gui.player.color);
			bonus.get(1).removeArmy();

		}
		int aix= rand.nextInt(gui.dimensionX);
		int aiy= rand.nextInt(gui.dimensionY);
		while (aix == initX && aiy == initY) {
			aix= rand.nextInt(gui.dimensionX);
			aiy= rand.nextInt(gui.dimensionY);
		}
		gui.board[aix][aiy].changeTribe(gui.npc1, Color.RED);
		gui.battleInfo.setArmyOwners();

	}

	/** Configures the Java Boxes of gui that make up the game. Sets up gui's game grid and buttons */
	public static void addSquares(GUI gui) {
		gui.board= new MapSquare[gui.dimensionX][gui.dimensionY];
		// invariant: rows 0..i-1 have been set up
		for (int i= 0; i != gui.dimensionX; i= i + 1) {
			// Set up row i of the board.
			Box row= new Box(BoxLayout.X_AXIS);

			// inv: board[i][0..j-1] have been set up
			for (int j= 0; j != gui.dimensionY; j= j + 1) {
				gui.board[i][j]= new MapSquare(i, j);
				row.add(gui.board[i][j]);
				gui.board[i][j].addMouseListener(gui.mouseEvent);
			}
			gui.boardBox.add(row);
		}
		gui.mainGameBox.add(gui.boardBox);

		gui.infoBox.add(gui.tribeName);
		// infoBox.add(tribeLeader);
		gui.infoBox.add(gui.armyCount);
		gui.infoBox.add(gui.squarePop);
		gui.mainGameBox.add(gui.infoBox);
		gui.buildArmy.addMouseListener(gui.mouseEvent);
		gui.clickTurn.addMouseListener(gui.mouseEvent);
		gui.buttonBox.add(gui.buildArmy);
		gui.buttonBox.add(gui.clickTurn);
		gui.clickTurn.setVisible(false);
		gui.turnBox.add(gui.aiTurn1);
		gui.turnBox.add(gui.aiTurn2);
		gui.infoBox.add(gui.turnBox);
		gui.infoBox.add(gui.buttonBox);
		gui.rollBox.add(gui.playerRolls);
		gui.rollBox.add(gui.aiRolls);

		gui.infoBox.add(Box.createGlue());
		gui.infoBox.add(gui.turnBox);
		gui.infoBox.add(gui.rollBox);
		gui.turnBox.setVisible(false);
		gui.buttonBox.setVisible(false);
		gui.rollBox.setVisible(false);

	}

	/** set up the start menu of gui, includes adding check boxes for colors */
	public static void addStart(GUI gui) {
		gui.startGame.addMouseListener(gui.mouseEvent);
		gui.startBox.add(gui.startGame);
		// For later when adding constants, the types are in the 30s because this is the 3rd type of button
		// added
		MenuButton nation1= new MenuButton("PLAY NATION 1", 31);
		MenuButton nation2= new MenuButton("PLAY NATION 2", 32);
		MenuButton nation3= new MenuButton("PLAY NATION 3", 33);
		nation1.addMouseListener(gui.mouseEvent);
		nation2.addMouseListener(gui.mouseEvent);
		nation3.addMouseListener(gui.mouseEvent);
		gui.startButtonBox.add(nation1);
		gui.startButtonBox.add(nation2);
		gui.startButtonBox.add(nation3);
		gui.startChoiceBox.add(gui.startButtonBox);
		gui.startChoiceBox.add(gui.nationPic); // requires nationPic to not be null, ie requires a temp image(nation 1)
		gui.startBox.add(gui.startChoiceBox);
		// startBox.add(Box.createGlue());

	}

	/** Set up the instructions info screen of gui */
	public static void addInstructions(GUI gui) {
		gui.instructionBox.addMouseListener(gui.mouseEvent);
		ImageIcon intro= new ImageIcon("resources/introScreen.png");
		gui.instructionBox.add(new JLabel(intro));
		gui.add(gui.instructionBox, BorderLayout.CENTER);

	}

	/** Closes the instruction window of gui and open its character selection screen */
	public static void closeInstructions(GUI gui) {
		gui.startBox.setVisible(true);
		gui.remove(gui.instructionBox);
		gui.add(gui.startBox, BorderLayout.CENTER);
		gui.pack();

	}

	/** For the GUI gui, check whether one of the players is out of armies and has thus lost, and then
	 * takes appropriate action */
	public static void endgame(GUI gui) {
		if (gui.playerArmies.size() == 0) {
			// the player has lost
			VictoryScreen c= new VictoryScreen(false);
			gui.setVisible(false);
			gui.dispose();

		}
		if (gui.aiArmies.size() == 0) {
			// the computer has lost
			VictoryScreen c= new VictoryScreen(true);
			gui.setVisible(false);
			gui.dispose();

		}

	}

	/** Fill the list randomEvents in GUI gui with the possible names of events that occur randomly.
	 * Additionally set the currentRandomEvents list of GUI gui to be equal to the randomEvents list */
	public static void addEvents(GUI gui) {
		addToRandom("Famine", gui);
		addToRandom("Bountiful Harvest", gui);
		addToRandom("Rally to the Flag", gui);
		addToRandom("Supply Shortage", gui);

	}

	/** Add an event to the randomEvents and currentRandomEvents lists in GUI gui */
	public static void addToRandom(String event, GUI gui) {
		GUI.randomEvents.add(event);
		gui.currentRandomEvents.add(event);

	}
}
