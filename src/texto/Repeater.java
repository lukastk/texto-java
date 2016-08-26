package texto;

/**
 * Can be added to the game's repeater list.
 * It's doAction() method will be called every update, until the repeater's close() method is called.
 * @author Lukas
 *
 */
public abstract class Repeater {
	protected Game game;
	
	public Repeater(Game game) {
		this.game = game;
	}
	
	public abstract void doAction();
	
	public void close(){
		game.getRepeaters().remove(this);
	}
}
