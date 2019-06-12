package gui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class VictoryScreen extends JFrame {

	private static final long serialVersionUID= 1L;

	public VictoryScreen(boolean victory) {
		ImageIcon image= null;
		if (victory) {
			image= new ImageIcon("victory.png");
		} else {
			image= new ImageIcon("defeat.png");
		}
		JLabel imageLabel= new JLabel(image);
		add(imageLabel);
		setVisible(true);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public static void main(String[] args) {
		VictoryScreen c= new VictoryScreen(false);

	}

}
