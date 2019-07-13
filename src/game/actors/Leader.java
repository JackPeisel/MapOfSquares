package game.actors;

public class Leader extends Person {

	public String name= "test leader123";
	public int gold;

	public Leader(String title, int startingGold) {
		name= title;
		gold= startingGold;
	}

}
