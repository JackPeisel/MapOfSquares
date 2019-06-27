package gui;

import javax.swing.JButton;

public class MenuButton extends JButton {
	/** The type of MenuButton this is. Type 0 is for building an army, type 1 is for starting the game,
	 * type 2 is for proceeding to the next turn, 31-3x are for nation selection, 400-499 are for
	 * events */
	public int TYPE;

	public MenuButton(String name, int type) {
		super(name);
		TYPE= type;
	}

}
