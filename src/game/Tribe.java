package game;

import java.util.ArrayList;

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

	/** A constructor for a new Tribe. The tribe is of race culture, made up of Populace pop, and lead
	 * by Chief leader. The Tribe's BattleStats, a measure of its competence in battle, is generated
	 * based on the stats of its leader and populace. */
	public Tribe(String title) {
		// population= pop;
		// chief= leader;
		// race= culture;
		// battle= getAccumen(pop, leader);
		// armies.add(new Army());
		name= title;

	}

	/** Create a BattleStats object representing the Populace's readiness for war */
	public BattleStats getAccumen(Populace pop, Leader leader) {
		return null;
	}

}
