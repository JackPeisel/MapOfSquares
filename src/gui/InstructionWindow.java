package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class InstructionWindow extends JFrame {
	private static final JLabel page1= new JLabel(new ImageIcon("resources/detailed1.png"));
	private static final JLabel page2= new JLabel(new ImageIcon("resources/detailed2.png"));
	private static final JLabel page3= new JLabel(new ImageIcon("resources/detailed3.png"));
	/** The page of the instructions that is currently being displayed */
	private JLabel page= page1;
	/** object that process keystrokes */
	private KeyEvents keyEvent= new KeyEvents();
	/** The number of the latest page */
	public static final int maxPage= 3;
	/** The page that is currently selected */
	public int pageNumber= 1;
	/** The GUI associated with this Instruction Window */
	public GUI gui;

	/** Creates an instruction window instance
	 *
	 * Precondition: temp is not null */
	public InstructionWindow(GUI temp) {
		gui= temp;
		addKeyListener(keyEvent);
		add(page);
		setVisible(true);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setPage(int number) {
		remove(page);
		if (number == 1) {
			page= page1;
		} else if (number == 2) {
			page= page2;
		} else if (number == 3) {
			page= page3;
		}
		add(page);
		pack();
	}

	/** Increases the page number by 1, if applicable */
	public void goForward() {
		if (pageNumber == maxPage) { return; }
		pageNumber++ ;
		setPage(pageNumber);
	}

	/** Decreases the page number by 1, if applicable */
	public void goBackward() {
		if (pageNumber == 1) { return; }
		pageNumber-- ;
		setPage(pageNumber);

	}

	/** Class contains a method that responds to a keystroke */
	class KeyEvents implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			int key= e.getKeyCode();
			if (key == KeyEvent.VK_RIGHT) {
				goForward();
			} else if (key == KeyEvent.VK_LEFT) {
				goBackward();
			} else if (pageNumber == maxPage && key == KeyEvent.VK_SPACE) {
				BoardInit.closeInstructions(gui);
				setVisible(false);
				dispose();
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
