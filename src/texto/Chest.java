package texto;

import java.util.Vector;

import texto.parsing.Sentence;

/**
 * A base class for a chest. It has the basic functionality of what you would expect of a chest.
 * @author Lukas
 *
 */
public class Chest extends Item {
	private Vector<String> locks = new Vector<String>();
	private Vector<String> unlocked = new Vector<String>();
	
	private boolean opened = false;
	
	public Chest(Game game) {
		super(game);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		String[] aliases;
		
		aliases = new String[] { "use-on", "use-with" };
		getIndObjCommands().put("usechest", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				Item key = game.getItemByName(sentence.getDirObject().getName());
				
				if (game.getPlayer().getItems().contains(key))
					unlock(key);
				else if (key == null)
					game.getTextOutput().printLine("What?");
				else
					game.getTextOutput().printLine("You don't have that...");
			}
		});
		
		aliases = new String[] { "open" };
		getDirObjCommands().put("openchest", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				if (isUnlocked()) {
					game.printLine("You opened the chest.");
					opened = true;
					onOpened();
				}
				else
					game.printLine("You would if you could, but you can't 'cuz it's locked... Stupid.");
			}
		});
		
		aliases = new String[] { "close" };
		getDirObjCommands().put("usechest", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				if (isUnlocked() && isOpened()) {
					game.printLine("You closed the chest.");
					opened = false;
					onClosed();
				}
				else
					game.printLine("It's already closed...");
			}
		});
	}
	
	public Vector<String> getLocks() { return locks; }
	public Vector<String> getUnlocked() { return unlocked; }
	
	public boolean isUnlocked() { return locks.size() == 0; }
	public boolean isOpened() { return isUnlocked() && opened; }
	
	/**
	 * Is called when you want to try to unlock the chest with a key.
	 * @param key
	 */
	public void unlock(Item key) {
		if (!(key instanceof Key)) {
			game.getTextOutput().printLine("This isn't a key...");
			return;
		}
		
		if (locks.contains(key.getIdentifier())) {
			locks.remove(key.getIdentifier());
			unlocked.add(key.getIdentifier());
			
			if (isUnlocked()){
				game.getTextOutput().printLine("You unlocked the chest.");
				onUnlocked();
				key.destroy();
			}
			else
				game.getTextOutput().printLine("One of the locks were unlocked.");
		}
		else {
			game.getTextOutput().printLine("The key doesn't fit in the lock.");
		}
	}
	
	/* Called when unlocked */
	public void onUnlocked() {}
	/* Called when closed */
	public void onClosed() {}
	/* Called when opened */
	public void onOpened() {}
}