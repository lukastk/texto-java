package texto;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import texto.parsing.Parser;
import texto.parsing.Sentence;

public class Game {
	private TextManager textOutput;
	private Parser parser;
	private HashMap<String, Item> items = new HashMap<String, Item>();
	private HashMap<String, Room> rooms = new HashMap<String, Room>();
	private Player player;
	private HashMap<String, Object> gameData = new HashMap<String, Object>();
	private Vector<Repeater> repeaters = new Vector<Repeater>();
	
	private boolean started = false;
	
	public Game(TextManager textOutput, Parser parser) {
		this.textOutput = textOutput;
		this.parser = parser;
	}
	
	public TextManager getTextOutput() { return textOutput; }
	public Item getItem(String itemIdentifier) { return items.get(itemIdentifier); }
	public Room getRoom(String roomIdentifier) { return rooms.get(roomIdentifier); }
	public HashMap<String, Item> getItemMap() { return items; }
	public HashMap<String, Room> getRoomMap() { return rooms; }
	public Player getPlayer() { return player; }
	public void setPlayer(Player player) { this.player = player; }
	public boolean hasStarted() { return started; }
	
	/* The game data is a hashmap that all of the items has access to, so that they can share information amongst eachother. */
	public HashMap<String, Object> getDataMap() { return gameData; }
	public Object getData(String dataKey) { return gameData.get(dataKey); }
	public void setData(String dataKey, Object value) { gameData.put(dataKey, value); }
	
	public Vector<Repeater> getRepeaters() { return repeaters; }
	
	/*
	 * Adds an item to the game.
	 */
	public void addItem(Item item, String location) {
		if (items.containsValue(item))
		{
			System.out.println("Tried to add already existing item.");
			return;
		}
		
		items.put(item.getIdentifier(), item);
		item.setLocation(location);
	}
	/*
	 * Adds a room to the game.
	 */
	public void addRoom(Room room) {
		if (rooms.containsValue(room))
		{
			System.out.println("Tried to add already existing room.");
			return;
		}
		
		rooms.put(room.getIdentifier(), room);
	}
	
	/*
	 * Initializes and starts the game.
	 */
	public void start() {
		if (hasStarted())
		{
			System.out.println("Tried to start game multiple times.");
			return;
		}
		
		started = true;
		
		// Reload all of the item locations.
		for (Item item : items.values()) {
			item.setLocation(item.getLocation());
		}
	}
	
	/*
	 * Updates the game.
	 */
	public void update() {
		if (!hasStarted()) {
			System.out.println("It is required to call start() before trying to update the game.");
			return;
		}
		
		String entry = textOutput.getInput();

		if (entry != null) {
			if (!processEntry(entry))
			{
				textOutput.printLine("Uh... What?");
			}
			textOutput.printLine("");

			//Call all of the repeaters.
			for (Iterator<Repeater> it = repeaters.iterator(); it.hasNext();) {
				it.next().doAction();
			}
		}
	}
	
	/**
	 * Processes the entry, by sending it to the parser and looking for matches.
	 * 
	 * @param newEntry the entry to process.
	 * @return returns true if a match to the entry could be found.
	 */
	public boolean processEntry(String newEntry) {
		newEntry = newEntry.toLowerCase();
		
		Sentence sentence;
		if (!newEntry.equals(""))
			sentence = parser.parse(newEntry);
		else 
			sentence = new Sentence();
		
		Set<Item> visibleItems = player.getVisibleInRangeItems();
		
		//Check literal commands
		Room playerRoom = (Room)getRoom(player.getLocation());
		
		for (Command command : playerRoom.getLiteralCommands().values()) {
			for (int i = 0; i < command.getAliases().length; i++) {
				if (command.getAliases()[i].equals(newEntry)) {
					command.doAction(sentence);
					return true;
				}	
			}
		}
		
		//Check direct object commands
		String inputtedName = "";
		for (String adj : sentence.getDirObject().getAdjectives())
			inputtedName += adj + " ";
		inputtedName += sentence.getDirObject().getName();
		
		for (Item item : visibleItems) {
			if (item.getNames().contains(inputtedName))
			{
				for (Command command : item.getDirObjCommands().values()) {
					for (int i = 0; i < command.getAliases().length; i++) {
						if (command.getAliases()[i].equals(sentence.getVerb())) {
							command.doAction(sentence);
							return true;
						}	
					}
				}
			}
		}
		
		//Check indirect object commands
		inputtedName = "";
		for (String adj : sentence.getIndObject().getAdjectives())
			inputtedName += adj + " ";
		inputtedName += sentence.getIndObject().getName();
		
		String actionName = sentence.getVerb() + "-" + sentence.getPreposition();
		
		for (Item item : visibleItems) {
			if (item.getNames().contains(inputtedName))
			{
				for (Command command : item.getIndObjCommands().values()) {
					for (int i = 0; i < command.getAliases().length; i++) {
						if (command.getAliases()[i].equals(actionName)) {
							command.doAction(sentence);
							return true;
						}	
					}
				}
			}
		}
		
		return false;
	}

	/**
	 * Gets an item by it's name (instead of by its identifier.). It only seeks items that are in the player's visibility range.
	 * @param name the name of the Item
	 * @return the item found. Null if no item is found.
	 */
	public Item getItemByName(String name) {
		Set<Item> items = player.getVisibleInRangeItems();
		
		for (Item item : items) {
			for (String item_name : item.getNames())
				if (name.equals(item_name))
					return item;
		}
		
		return null;
	}
	
	public void printLine(String line) {
		textOutput.printLine(line);
	}
}