package gui;

import javax.swing.JButton;

public class MenuButton extends JButton {
	public int TYPE; // type 0 is reserved for building an army
					 // type 1 is reserved for the start menu
					 // type 2 is reserved for the next turn button

	public MenuButton(String name, int type) {
		super(name);
		TYPE= type;
	}

}
