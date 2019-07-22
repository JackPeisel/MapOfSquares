package gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;

public class EventWindow extends JFrame {
	/** The image associated with the event */
	public ImageIcon image;
	/** The box containing the picture and button choices */
	public Box mainBox= new Box(BoxLayout.Y_AXIS);
	/** An object that process mouse clicks */
	private MouseEvents mouseEvent= new MouseEvents();
	/** The GUI associated with this event */
	public GUI gui;
	/** The MapSquare where the event occurs */
	public MapSquare eventSquare;

	public EventWindow(String type, MapSquare ms, GUI main) {
		gui= main;
		eventSquare= ms;
		add(new JLabel(type), BorderLayout.NORTH);
		addPicture(type);
		addButtons(type);
		pack();
		setVisible(true);

	}

	/** Return true if the event has a choice, false otherwise */
	public static boolean isChoice(String type) {
		if (type == "To Pillage or not to Pillage") { return true; }
		return false;

	}

	/** Return true if the event is random, false otherwise */
	public static boolean isRandom(String type) {
		if (type == "Cowards" || type == "To Pillage or not to Pillage") { return false; }
		return true;
	}

	/** Return true if the event is good for the player
	 *
	 * Precondition: type is not an event with a choice */
	public static boolean isGood(String type) {
		if (type == "Bountiful Harvest" || type == "Rally to the Flag") { return true; }
		return false;
	}

	/** add the picture needed to represent the event */
	public void addPicture(String type) {
		mainBox.add(new JLabel(new ImageIcon("resources/TempEvent.png")));
	}

	/** add the buttons needed for the type event. If the event doesn't have any choices for the player,
	 * simply add a close tab button. The boolean good represents whether or not the event is good for
	 * the player */
	public void addButtons(String type) {
		if (!isChoice(type)) {
			if (isGood(type)) {
				MenuButton mb= new MenuButton("Ahh, good news at last", 400);
				mb.addMouseListener(mouseEvent);
				mainBox.add(mb);
			} else {
				MenuButton mb= new MenuButton("Curses", 400);
				mb.addMouseListener(mouseEvent);
				mainBox.add(mb);
			}

		} else {
			if (type == "To Pillage or not to Pillage") {
				MenuButton mb= new MenuButton("Pillage", 401);
				mb.addMouseListener(mouseEvent);
				mainBox.add(mb);
				mb= new MenuButton("Don't Pillage", 402);
				mb.addMouseListener(mouseEvent);
				mainBox.add(mb);
			}

		}
		add(mainBox, BorderLayout.SOUTH);

	}

	/** Process the click of a menu button */
	public void processMenuClick(MenuButton mb) {
		// Choiceless event
		if (mb.TYPE == 400) {
			delete();
		}
		// To Pillage or not to Pillage
		if (mb.TYPE == 401 || mb.TYPE == 402) {
			eventSquare.processPlunder(mb.TYPE == 401 ? true : false);
			delete();

		}

	}

	/** Unfreezes the GUI and removes the EventWindow */
	public void delete() {
		gui.frozen= false;
		setVisible(false);
		dispose();
	}

	/** Class contains a method that responds to a mouse click in a CheckerSquare */
	class MouseEvents extends MouseInputAdapter {
		/** If e is a click in a MapSquare, process it */
		@Override
		public void mouseClicked(MouseEvent e) {

			Object ob= e.getSource();

			if (ob instanceof MenuButton) {
				processMenuClick((MenuButton) ob);
				return;
			}
			System.out.println("failure");

		}

	}

}
