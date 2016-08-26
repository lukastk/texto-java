package texto;

import java.util.Vector;

import texto.parsing.Sentence;

/*
 * A base class for a door item, that behaves in a way that is reasonably similar to that of a door.
 */
public class Door extends Item {
	private Vector<String> locks = new Vector<String>();
	private Vector<String> unlocked = new Vector<String>();
	private String leadsTo1;
	private String leadsTo2;
	
	public Door(Game game) {
		super(game);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		String[] aliases;
		
		aliases = new String[] { "use", "open" };
		getDirObjCommands().put("opendoor", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				goThrough();
			}
		});
		
		aliases = new String[] { "use-on", "use-with" };
		getIndObjCommands().put("usedoor", new Command(aliases) {
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
	}
	
	/*
	 * Gets a list of all of the locks within the door.
	 * It's a list of all of the identifiers of all of the key's that are needed to unlock this door.
	 */
	public Vector<String> getLocks() { return locks; }
	/*
	 * Gets a list of all of the unlocked locks.
	 */
	public Vector<String> getUnlocked() { return unlocked; }
	/*
	 * Gets the room that this door leads to.
	 */
	public String getLeadsTo() {
		if (game.getPlayer().getLocation().equals(leadsTo1))
			return leadsTo2;
		else
			return leadsTo1;
	}
	/*
	 * Sets where this door leads to.
	 */
	public void setLeadsTo(String leadsTo1, String leadsTo2) {
		this.leadsTo1 = leadsTo1;
		this.leadsTo2 = leadsTo2;
	}
	
	public boolean isUnlocked() { return locks.size() == 0; }
	
	public void unlock(Item key) {
		if (!(key instanceof Key)) {
			game.getTextOutput().printLine("This isn't a key...");
			return;
		}
		
		if (locks.contains(key.getIdentifier())) {
			locks.remove(key.getIdentifier());
			unlocked.add(key.getIdentifier());
			
			if (isUnlocked()) {
				game.getTextOutput().printLine("You unlocked the door.");
				onUnlocked();
				key.destroy();
			}
			else
				game.getTextOutput().printLine("One of the locks were unlocked.");
		}
	}
	
	/*
	 * Takes the player throught the door.
	 */
	public void goThrough() {
		if (isUnlocked())
			game.getPlayer().setLocation(getLeadsTo());
		else
			game.getTextOutput().printLine("The door is locked.");
	}
	
	/*
	 * Is called when the door is unlocked.
	 */
	public void onUnlocked() {}
}