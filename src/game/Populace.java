package game;

import java.util.LinkedList;

import game.actors.Person;

public class Populace {

	public LinkedList<Person> people;
	public int happiness;
	public int population;
	public Culture race;

	public Populace(int happy, int pop, Culture cult) {
		happiness= happy;
		population= pop;
		race= cult;

	}

	/** Based on the Culture and size of the Populace, generate a LinkedList of People that will
	 * represent the populace */
	public LinkedList<Person> generatePeople(int pop, Culture cult) {
		return null;
	}

}
