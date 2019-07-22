package game;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import game.actors.Leader;
import game.battle.Army;

public class Tribe {

	public Leader chief;
	public ArrayList<Army> armies;
	public String name;
	/** The type of nation this Tribe is. If it is the ai tribe then type is -1 */
	public int type;
	public ImageIcon image;
	public Color color;

	/** A constructor for a new Tribe. The tribe is named title, is of type which with image being the
	 * appropriate ImageIcon(if it is a player tribe), and the color fitting the type(or red if it is
	 * not a player tribe) . */
	public Tribe(String title, int which) {
		name= title;
		changeTribe(which);
		if (type == -1) {
			color= Color.RED;
		}

	}

	/** Change the type of the tribe to t */
	public void changeTribe(int t) {
		type= t;
		// System.out.println(type);
		if (t == 1) {
			image= new ImageIcon("resources/nation1.png");
			color= Color.BLUE;
		} else if (t == 2) {
			image= new ImageIcon("resources/nation2.png");
			color= Color.YELLOW;
		} else { // t==3
			image= new ImageIcon("resources/nation3.png");
			color= Color.CYAN;
		}
	}

}
