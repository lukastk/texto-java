package texto;

import java.util.*;
import texto.parsing.*;

/**
 * The base class for a room within the game.
 * 
 * It can contain items and also the player.
 * @author Lukas
 *
 */
public class Room {
	protected Game game;

	private String north;
	private String south;
	private String west;
	private String east;

	private String identifier;
	private Set<Item> items = new HashSet<Item>();
	private HashMap<String, Command> literalCommands = new HashMap<String, Command>();

	public String getNorth() { return north; }
	public void setNorth(String north) { this.north = north; }
	public String getSouth() { return north; }
	public void setSouth(String south) { this.south = south; }
	public String getWest() { return north; }
	public void setWest(String west) { this.west = west; }
	public String getEast() { return north; }
	public void setEast(String east) { this.east = east; }

	public String getIdentifier() { return identifier; }
	public void setIdentifier(String identifier) { this.identifier = identifier; }
	public Set<Item> getItems() { return items; }
	public HashMap<String, Command> getLiteralCommands() { return literalCommands; }
	public boolean checkHasPlayer() { return game.getPlayer().getLocation().equals(this.identifier); }

	public Room(Game game) {
		this.game = game;
	}

	/**
	 * Creates some initial commands.
	 */
	public void initialize() {
		String[] aliases;
		
		aliases = new String[] { "help", "help me" };
		literalCommands.put("help" , new Command(aliases) {
			int numHelp = 0;
			
			@Override
			public void doAction(Sentence sentence) {
				if (numHelp == 0)
					game.printLine("Hahahahahaha... Yeah... I don't think so.");
				else if (numHelp == 1)
					game.printLine("Haha! Stop it! You're too funny! XD");
				else if (numHelp == 2)
					game.printLine("Bwahahaha!!! You're serious?? You think I'm going to help you??");
				else if (numHelp == 3)
					game.printLine("Jeeeez, stop bothering me... Why are you even playing this game? Don't you wanna do things for yourself?");
				else if (numHelp == 4)
					numHelp = -1;
				numHelp++;
			}
		});
		
		aliases = new String[] { "go north", "n", "north" };
		literalCommands.put("gonorth" , new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				if (!movePlayerTo(north))
					game.printLine("You can't go that way.");
			}
		});
		
		aliases = new String[] { "go south", "s", "south" };
		literalCommands.put("gosouth", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				if (!movePlayerTo(south))
					game.printLine("You can't go that way.");
			}
		});
		
		aliases = new String[] { "go west", "w", "west" };
		literalCommands.put("gowest", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				if (!movePlayerTo(west))
					game.printLine("You can't go that way.");
			}
		});
		
		aliases = new String[] { "go east", "e", "east" };
		literalCommands.put("goeast", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				if (!movePlayerTo(east))
					game.printLine("You can't go that way.");
			}
		});
		
		aliases = new String[] { "inventory", "i", "look at inventory" };
		literalCommands.put("inventory", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				game.getTextOutput().printLine("Here's what you have:");
				
				for (Item item : game.getPlayer().getItems())
					game.getTextOutput().printLine(" - " + item.getInventoryName());

				if (game.getPlayer().getItems().size() == 0)
					game.getTextOutput().printLine("Absolutely nothing...");
			}
		});

		aliases = new String[] { "look" };
		literalCommands.put("look", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				look();
			}
		});
	}

	/**
	 * Checks whether an item with the specified identifier exists.
	 * @param identifier
	 * @return
	 */
	public boolean hasItem(String identifier) {
		for (Item item : items)
			if (item.getIdentifier().equals(identifier))
				return true;
		return false;
	}
	
	/**
	 * Tries to move the player to the location
	 * @param location
	 * @return returns if it was a successful move.
	 */
	public boolean movePlayerTo(String location) {
		if (!game.getPlayer().getLocation().equals(this.getIdentifier()))
			return false;
		
		if (location != null) {
			game.getPlayer().setLocation(location);
			return true;
		}
		
		return false;
	}

	/**
	 * Is called whenever the player enters the room.
	 */
	public void onEntry() {
		look();
	}

	/**
	 * Prints out a simple description of the room.
	 */
	public void look() {
	}
}