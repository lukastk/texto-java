package texto;

import java.util.*;
import texto.parsing.Sentence;

/**
 * The base class of all items.
 * 
 * Each item has a String identifier, which is unique to each item, and has to be assigned upon creation.
 * Each item also has several names. The names do not have to be uniqe, and there can be multiple names as well.
 * When the player wants to call a particular item's command, he will refer to the object by its name (not its identifier.).
 * 
 * Items also has a visibility property. If the item is not visible, the player can not see them or deal with them. Even if the
 * player is in the same room as the item.
 * 
 * The item can also be destroyed using the destroy() method.
 * @author Lukas
 *
 */
public class Item {
	protected Game game;
	
	private String identifier;
	private Vector<String> names = new Vector<String>();
	protected String location;
	private boolean visible = true;
	private boolean exists = true;
	
	private HashMap<String, Command> dirObjCommands = new HashMap<String, Command>();
	private HashMap<String, Command> indObjCommands = new HashMap<String, Command>();
	
	public Item(final Game game) {
		this.game = game;
	}
	
	/**
	 * Sets up some intitial commands.
	 */
	public void initialize() {
		String[] aliases;
		
		aliases = new String[] { "look-at" };
		indObjCommands.put("lookatitem", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				look();
			}
		});
		
		aliases = new String[] { "examine" };
		dirObjCommands.put("examineitem", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				look();
			}
		});
	}
	
	public String getIdentifier() { return identifier; }
	public void setIdentifier(String identifier) { this.identifier = identifier; }
	
	public Vector<String> getNames() { return names; }
	
	public String getLocation() { return location; }
	/**
	 * Sets the location of the item to a new location. It erases it's trace from the old location and adds itself to the new one.
	 * @param location
	 */
	public void setLocation(String location) {
		if (!game.hasStarted()) {
			this.location = location;
			return;
		}
		
		if (location.equals("player"))
		{
			Room oldLocation = game.getRoom(this.location);
			if (oldLocation != null)
				oldLocation.getItems().remove(this);
			this.location = "player";
			game.getPlayer().getItems().add(this);
		}
		else {
			Room newLocation = game.getRoom(location);
			Room oldLocation = game.getRoom(this.location);
			
			if (newLocation != null) {
				this.location = location;
				
				if (oldLocation != null)
					oldLocation.getItems().remove(this);
				newLocation.getItems().add(this);
			}
		}
	}
	
	public boolean isVisible() { return visible; }
	public void setVisibility(boolean visibility) { this.visible = visibility; }
	public boolean getExists() { return exists; }
	/**
	 * Gets the name that should be printed when the item is in the inventory.
	 * @return
	 */
	public String getInventoryName() { return names.get(0); }
	public HashMap<String, Command> getDirObjCommands() { return dirObjCommands; }
	public HashMap<String, Command> getIndObjCommands() { return indObjCommands; }
	
	/**
	 * Destroys the item and erases it from the game.
	 */
	public void destroy() {
		if (location.equals("player"))
			game.getPlayer().getItems().remove(this);
		else
			game.getRoom(location).getItems().remove(this);
		game.getItemMap().remove(this);
		exists = false;
	}
	
	/**
	 * Adds a basic "take" command
	 * @param cond
	 */
	public void addTakeCommand(Conditional cond)
	{
		String takeMessage = "You got the " + getInventoryName() + ".";
		String alreadyHaveMessage = "You already have the " + getInventoryName() + ".";
		addTakeCommand(takeMessage, alreadyHaveMessage, cond);
	}
	/**
	 * Adds a basic "take" command, as in: "take itemX".
	 * @param cond
	 */
	public void addTakeCommand(final String takeMessage, final String alreadyHaveMessage, final Conditional cond) {		
		String[] aliases = new String[] { "take", "grab" };
		
		Command command;
		if (cond == null){
			command =  new Command(aliases) {
				@Override
				public void doAction(Sentence sentence) {
					if (game.getPlayer().hasItem(getIdentifier())) {
						game.printLine(alreadyHaveMessage);
						return;
					}
					
					setLocation("player");
					game.printLine(takeMessage);
				}
			};
		}
		else {
			command = new Command(aliases) {
				@Override
				public void doAction(Sentence sentence) {
					if (!cond.func()) {
						return;
					}
					
					if (game.getPlayer().hasItem(getIdentifier())) {
						game.printLine(alreadyHaveMessage);
						return;
					}
					
					setLocation("player");
					game.printLine(takeMessage);
				}
			};
		}
		
		getDirObjCommands().put("takeitem", command);
	}
	
	/**
	 * Prints out a basic description of the item.
	 */
	public void look() {
		game.getTextOutput().printLine("You see a " + getInventoryName() + ".");
	}
}
