package texto;

import java.util.*;

/**
 * The base class for the player.
 * 
 * The player extends the Item class but does not use of all it's functionality.
 * @author Lukas
 *
 */
public class Player extends Item {
	private Set<Item> items = new HashSet<Item>();
	
	public Player(final Game game) {
		super(game);
	}

	public Set<Item> getItems() { return items; }
	
	/**
	 * Checks whether an item with the specified identifier is within the player's inventory.
	 * @param identifier
	 * @return
	 */
	public boolean hasItem(String identifier) {
		for (Item item : items)
			if (item.getIdentifier().equals(identifier))
				return true;
		return false;
	}
	
	@Override
	public void setLocation(String location) {
		this.location = location;
		
		Room newLocation = game.getRoom(location);
		newLocation.onEntry();
	}

	/**
	 * Gets a list of all of the items that are within the players visible range. Specifically:
	 * all of the items that are visible and that are in the current room that the player is in.
	 * @return
	 */
	public Set<Item> getVisibleInRangeItems()
	{
		HashSet<Item> itemList = new HashSet<Item>();
		Room currentRoom = game.getRoom(getLocation());
		
		for (Item item : currentRoom.getItems()) {
			if (item.isVisible())
				itemList.add(item);
		}
		
		// Add all of the items in the inventory as well.
		itemList.addAll(items);

		return itemList;
	}

}