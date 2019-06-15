package game;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import game.actors.Leader;
import game.battle.Army;
import game.battle.BattleStats;

public class Tribe {

	public Populace population;
	public Leader chief;
	public BattleStats battle;
	public Culture race;
	public ArrayList<Army> armies;
	public String name;
	public int type;
	public ImageIcon image;
	public Color color;

	/** A constructor for a new Tribe. The tribe is of race culture, made up of Populace pop, and lead
	 * by Chief leader. The Tribe's BattleStats, a measure of its competence in battle, is generated
	 * based on the stats of its leader and populace. */
	public Tribe(String title, int which) {
		// population= pop;
		// chief= leader;
		// race= culture;
		// battle= getAccumen(pop, leader);
		// armies.add(new Army());
		name= title;
		type= which;
		image= new ImageIcon("nation1.png");
		color= Color.BLUE;
		if (type == -1) {
			color= Color.RED;
		}

	}

	/** Create a BattleStats object representing the Populace's readiness for war */
	public BattleStats getAccumen(Populace pop, Leader leader) {
		return null;
	}

	/** Change the type of the tribe to t */
	public void changeTribe(int t) {
		type= t;
		// System.out.println(type);
		if (t == 1) {
			image= new ImageIcon("nation1.png");
			color= Color.BLUE;
		} else if (t == 2) {
			image= new ImageIcon("nation2.png");
			color= Color.YELLOW;
		} else { // t==3
			image= new ImageIcon("nation3.png");
			color= Color.CYAN;
		}
	}

}
