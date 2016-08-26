package texto;

import texto.parsing.Parser;
import texto.parsing.Sentence;
import texto.parsing.WordBank;


public class Program2 {
	private static boolean running = true;
	
	/**
	 * @param args
	 */
	public static void main2(String[] args) {
		//Setup the game
		final TextManager tm = new SwingConsoleManager();
		while (!tm.isReady()) {}
		
		new Thread() {
			public void run(){
				WordBank wbank = new WordBank();
				wbank.initialize("wordList.txt");
				Parser parser = new Parser(wbank);
				
				Game gm = new Game(tm, parser);
				initializeGame(gm);
				
				gm.start();
				
				while (running) {
					gm.update();
				}
			}
		}.start();
	}
	
	public static void initializeGame(Game gm) {
		gm.printLine("You wake up in a strange room with nothing much in it. You can't seem to remember how you got here, where you were " +
				"before you got here or even who you are for the matter. \n\n" +
				"\"Well...\", you think for yourself. \"This is kind of cliché! I guess I got to find a way out of here!\"");
		createWhiteRoom(gm);
		
		Player player = new Player(gm);
		gm.setPlayer(player);
		gm.getPlayer().setLocation("white room");
	}
	
	public static void createWhiteRoom(final Game gm) {		
		final Item carrot = whiteRoom_createCarrot(gm);
		final Item jar = whiteRoom_createJar(gm);
		final Item chest = whiteRoom_createChest(gm);
		final Door door = whiteRoom_createDoor(gm);
		
		Room room = new Room(gm) {
			@Override
			public void onEntry() {}
			
			@Override
			public void look() {
				String line = "You are in a room with nothing much in it. ";
				line += "There's a treasure chest, a chair that looks comfortable";
				
				if (this.getItems().contains(carrot))
					line += ", a carrot";
				if (this.getItems().contains(jar))
					line += ", a jar with a key in it";
				if (this.hasItem("glasspiece"))
					line += ", a piece of glass";
				if (this.hasItem("chestkey"))
					line += ", a key";
				
				line += " and a door to the north.";
				
				gm.printLine(line);
			}
		};
		room.initialize();
		room.setIdentifier("white room");
		
		String[] aliases = new String[] { "go north", "n", "north" };
		room.getLiteralCommands().put("gonorth", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				door.goThrough();
			}
		});
		
		gm.addRoom(room);
		
		gm.addItem(carrot, room.getIdentifier());
		gm.addItem(jar, room.getIdentifier());
		gm.addItem(chest, room.getIdentifier());
		gm.addItem(door, room.getIdentifier());
	}
	public static Item whiteRoom_createCarrot(final Game gm) {
		final Item carrot = new Item(gm) {
			@Override
			public void look() {
				if (gm.getData("hasBittenCarrot").equals(Boolean.FALSE)) {
					if (gm.getData("carrotLookCount").equals(new Integer(0)))
						gm.printLine("It sort of looks like carrot, but not really. A carrot isn't supposed to be purple, right?");
					else if (gm.getData("carrotLookCount").equals(new Integer(1)))
						gm.printLine("It's definitely not the type of carrot that you would ever find yourself eating. You wouldn't even try doing that.");
					else if (gm.getData("carrotLookCount").equals(new Integer(2)))
						gm.printLine("You wouldn't even dream of trying to eat a purple carrot!");
					else
						gm.printLine("Even if that was the last carrot on earth, you wouldn't eat it.");
				}
				else {
					if (gm.getData("carrotLookCount").equals(new Integer(0)))
						gm.printLine("You check the entire surface of the carrot-looking object. You find a tiny little button.");
					else if (gm.getData("carrotLookCount").equals(new Integer(1))) {
						gm.printLine("You tried to push the button. It shot out a slippery substance. Ew.");
						gm.setData("carrotDiscoveredButton", new Boolean(true));
					}
					else
						gm.printLine("You've always wanted on of these. NOT...");
				}
				
				gm.setData("carrotLookCount", ((Integer)gm.getData("carrotLookCount")).intValue() + 1);
			}
		};
		carrot.setIdentifier("carrot");
		carrot.getNames().add("carrot");
		carrot.getNames().add("weird carrot");
		gm.setData("hasBittenCarrot", new Boolean(false));
		gm.setData("carrotLookCount", new Integer(0));
		gm.setData("carrotDiscoveredButton", new Boolean(false));
		
		carrot.initialize();
		carrot.addTakeCommand(null);
		
		String[] alias = new String[] { "bite", "eat" };
		carrot.getDirObjCommands().put("bitecarrot", new Command(alias) {
			@Override
			public void doAction(Sentence sentence) {
				if (!gm.getPlayer().hasItem("carrot")) {
					gm.printLine("Maybe you should pick it up first?");
					return;
				}
				
				if (gm.getData("hasBittenCarrot").equals(Boolean.FALSE)) {
					if ( ((Integer)gm.getData("carrotLookCount")).intValue() > 1 )
						gm.printLine("I just said that you'd nev- Uhh... Whatever... \n");
					
					gm.printLine("You tried to take a bite out of the weird-looking purple carrot. It's super hard! Maybe it needs some further examination...");
					gm.setData("hasBittenCarrot", new Boolean(true));
					gm.setData("carrotLookCount", new Integer(0));
				} else {
					gm.printLine("No... I refuse, you're not going to do that.");
				}
			}
		});
		alias = new String[] { "use" };
		carrot.getDirObjCommands().put("usecarrot", new Command(alias) {
			@Override
			public void doAction(Sentence sentence) {
				if (!gm.getPlayer().hasItem("carrot")) {
					gm.printLine("Maybe you should pick it up first?");
					return;
				}
				
				if (gm.getData("carrotDiscoveredButton").equals(Boolean.TRUE)) {
					gm.printLine("That slippery stuff sprayed onto the floor.");
					
					if (gm.getPlayer().getLocation().equals("last room"))
						gm.setData("finalRoomIsSlippery", new Boolean(true));
				} else {
					gm.printLine("How could you possibly use this carrot?");
				}
			}
		});
		alias = new String[] { "push-on" };
		carrot.getIndObjCommands().put("pushbuttooncarrot", new Command(alias) {
			@Override
			public void doAction(Sentence sentence) {
				if (gm.getData("carrotDiscoveredButton").equals(Boolean.FALSE) || !sentence.getIndObject().getName().equals("button"))
					gm.printLine("Push what?!");
				else
					carrot.getDirObjCommands().get("usecarrot").doAction(sentence);
			}
		});
		
		return carrot;
	}
	public static Item whiteRoom_createJar(final Game gm) {
		final Item mirrorPiece = new Item(gm) {
			@Override
			public void look() {
				gm.printLine("It's a piece of a mirrored surface. You can see yourself in the reflection, meh.");
			}
		};
		mirrorPiece.setIdentifier("mirrorpiece");
		mirrorPiece.getNames().add("mirror piece");
		mirrorPiece.getNames().add("mirror shard");
		mirrorPiece.getNames().add("mirror");
		mirrorPiece.getNames().add("piece");
		mirrorPiece.getNames().add("shard");
		
		mirrorPiece.initialize();
		mirrorPiece.addTakeCommand(null);
		
		String[] alias = new String[] { "use", "reflect" };
		mirrorPiece.getDirObjCommands().put("usemirror", new Command(alias) {
			@Override
			public void doAction(Sentence sentence) {
				if (gm.getPlayer().getLocation().equals("last room")) {
					gm.printLine("You hold the mirror in front of you.");
					gm.setData("mirrorUsed", new Boolean(true));
				} else {
					gm.printLine("What for?");
				}
			}
		});
		
		final Key chestKey = new Key(gm) {
			@Override
			public void look() {
				gm.printLine("It's a key. You know what key is right?");
			}
		};
		chestKey.setIdentifier("chestkey");
		chestKey.getNames().add("key");
		
		chestKey.initialize();
		chestKey.addTakeCommand(null);
		
		final Item jar = new Item(gm) {
			@Override
			public void look() {
				gm.printLine("It's a jar with a mirrory surface of the breakable sort *hint* *hint*. It's got a key in it.");
			}
		};
		jar.setIdentifier("jar");
		jar.getNames().add("jar");
		jar.getNames().add("mirror jar");
		
		jar.initialize();
		jar.addTakeCommand(null);
		
		alias = new String[] { "break", "smash" };
		jar.getDirObjCommands().put("breakjar", new Command(alias) {
			@Override
			public void doAction(Sentence sentence) {
				if (jar.getExists())
				{
					String line = "You went ahead and did it, you smashed the jar on the floor. There's a huge bunch of mirrory pieces on the floor";
					line += " as well as the key.";
					gm.printLine(line);
					
					jar.destroy();
					gm.addItem(mirrorPiece, "white room");
					gm.addItem(chestKey, "white room");
				}
			}
		});
		
		return jar;
	}
	public static Item whiteRoom_createChest(final Game gm) {
		final Item apple = new Item(gm) {
			@Override
			public void look() {
				gm.printLine("It's an apple. You would eat it if you were hungry, but you're not so you won't.");
			}
		};
		apple.setIdentifier("apple");
		apple.getNames().add("apple");
		
		apple.initialize();
		apple.addTakeCommand(null);
		apple.setVisibility(false);
		gm.addItem(apple, "white room");
		
		final Key doorKey = new Key(gm) {
			@Override
			public void look() {
				gm.printLine("It's a key. You know what key is right?");
			}
		};
		doorKey.setIdentifier("doorkey");
		doorKey.getNames().add("key");
		
		doorKey.initialize();
		doorKey.addTakeCommand(null);
		doorKey.setVisibility(false);
		gm.addItem(doorKey, "white room");
		
		final Chest chest = new Chest(gm) {
			public void describeContents() {
				String line = "";
				
				if (gm.getRoom("white room").getItems().contains(apple)) {
					line += "an apple";
				}
				if (gm.getRoom("white room").getItems().contains(doorKey)) {
					if (line.equals(""))
						line += "a key";
					else
						line += " and a key";
				}
				
				if (line.equals(""))
					line = "There's nothing inside.";
				else
					line = "There is " + line + " inside.";
				
				gm.printLine(line);
			}
			
			@Override
			public void look() {
				if (!this.isOpened())
					gm.printLine("It's a chest that looks like it could potentially contains some treasure.");
				else {
					gm.printLine("The chest is open.");
					
					describeContents();
				}
			}
			
			@Override
			public void onOpened() {
				describeContents();
				if (gm.getRoom("white room").getItems().contains(apple)) {
					apple.setVisibility(true);
				}
				if (gm.getRoom("white room").getItems().contains(doorKey)) {
					doorKey.setVisibility(true);
				}
			}
			
			@Override
			public void onClosed() {
				if (gm.getRoom("white room").getItems().contains(apple))
					apple.setVisibility(false);
				if (gm.getRoom("white room").getItems().contains(doorKey))
					doorKey.setVisibility(false);
			}
		};
		chest.setIdentifier("chest");
		chest.getNames().add("treasure chest");
		chest.getNames().add("chest");
		chest.getLocks().add("chestkey");
		
		chest.initialize();
		
		return chest;
	}
	public static Door whiteRoom_createDoor(final Game gm) {
		final Door door = new Door(gm) {
			@Override
			public void look() {
				gm.printLine("It's a door.");
			}
		};
		door.setIdentifier("door");
		door.getNames().add("door");
		door.setLeadsTo("white room", "black room");
		door.getLocks().add("doorkey");
		
		door.initialize();
		
		return door;
	}

}
