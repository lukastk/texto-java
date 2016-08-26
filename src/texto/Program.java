package texto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import texto.parsing.Parser;
import texto.parsing.Sentence;
import texto.parsing.WordBank;


public class Program {
	private static boolean running = true;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
		gm.printLine("You wake up in a white-room with nothing much in it, except an odd selection of " +
					"akwardly selected furniture. There's a " +
					"treasure chest, a chair that looks comfortable, something that looks like a carrot, " +
					"a jar with a key in it and a door to the north. Some douche also drew a window on the wall. " +
					"You can't seem to remember exactly who you are, or what your name is. You realize that " +
					"you don't even know what gender you belong to, after a quick inspection you come to the " +
					"conclusion that you must've been a man at some point.\n");
		
		createWhiteRoom(gm);
		createBlackRoom(gm);
		createCellar(gm);
		createElevatorRoom(gm);
		createLastRoom(gm);
		
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
				if (this.hasItem("mirrorpiece"))
					line += ", a piece of mirror";
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

	public static void createBlackRoom(final Game gm) {		
		final Door southernDoor = blackRoom_createSoutherDoor(gm);
		final Door northernDoor = blackRoom_createNorthernDoor(gm);
		
		final Room room = new Room(gm) {
			@Override
			public void onEntry() {
				String line;
				if ((Boolean)gm.getData("lightsTurnedOn").equals(Boolean.TRUE)) {
					line = "You enter into another white room";
				} else {
					line = "You enter into a pitch-black room.";
				}
				
				gm.printLine(line);
				look();
			}
			
			@Override
			public void look() {
				String line;
				if ((Boolean)gm.getData("lightsTurnedOn").equals(Boolean.TRUE)) {
					line = "You see a pretty random-looking guy standing in a corner, and a trapdoor in the middle of the room. ";
					line += "There's doors on the southern and the northern walls of the room.";
				} else {
					line = "You can't really see anything, it's all dark in here. But hey, crazy idea here: why don't you try your 4 other senses?";
				}
				
				gm.printLine(line);
			}
		};
		room.initialize();
		room.setIdentifier("black room");
		gm.setData("lightsTurnedOn", new Boolean(false));
		
		String[] aliases = new String[] { "go north", "n", "north" };
		room.getLiteralCommands().put("gonorth", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				northernDoor.goThrough();
			}
		});
		aliases = new String[] { "go south", "s", "south" };
		room.getLiteralCommands().put("gosouth", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				southernDoor.goThrough();
			}
		});
		aliases = new String[] { "taste" };
		room.getLiteralCommands().put("taste", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				gm.printLine("Taste!? Taste what!? You really thought that was going to work? ...");
			}
		});
		aliases = new String[] { "feel", "touch" };
		room.getLiteralCommands().put("feel", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				gm.printLine("There's not really that much to feel except the sensation of time passing as you type in commands that are utterly useless...");
			}
		});
		aliases = new String[] { "smell" };
		room.getLiteralCommands().put("smell", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				String line = 	"Now the word \"smell\" can either be used as a verb to describe the act of using the olfactory organ ";
				line +=			"to precieve the odour of one's immediate surroundings, as in the sentence: \"Hey Johnny, I really love this ";
				line +=			"time of spring when you can SMELL the flowery scent within the breeze\". It can also take an ";
				line +=			"entirely different meaning when used as an intransative verb, as in the following sentence: \"Johnny, would you ";
				line +=			"please lend me some deoderant because I SMELL like horse manure!\". You are certainly already performing the latter ";
				line +=			"meaning of the verb. Which makes the entire excercise of typing in \"smell\" an entirely pointless excerise. Stupid, ";
				line +=			"stupid indeed.";
				gm.printLine(line);
			}
		});
		
		final Item guy = new Item(gm) {
			@Override
			public void look() {
				if (!(boolean)gm.getData("lightsTurnedOn")) {
					gm.printLine("It's dark, remember? You can't see him. Not even if you try super-hard.");
				} else {
					gm.printLine("Don't worry, you're way prettier than him.");
				}
			}
		};
		guy.setIdentifier("guy");
		guy.getNames().add("guy");
		guy.getNames().add("fellow");
		guy.getNames().add("dude");
		guy.getNames().add("man");
		guy.getNames().add("random guy");
		guy.getNames().add("random fellow");
		guy.getNames().add("random dude");
		guy.getNames().add("random man");
		guy.setVisibility(false);
		
		guy.initialize();
		guy.addTakeCommand(null);
		
		aliases = new String[] { "talk-to", "chat-with", "speak-to" };
		guy.getIndObjCommands().put("talktoguy", new Command(aliases) {
			int talkAmount = 0;
			
			@Override
			public void doAction(Sentence sentence) {
				if ((Boolean)gm.getData("lightsTurnedOn").equals(Boolean.FALSE)) {
					if (talkAmount == 0)
						gm.printLine(" - Hey! Wow! It's simply swell that you're awake now. I've been... Uh... Expecting you!");
					else if (talkAmount == 1)
						gm.printLine(" - Who I am? Uhm... I'm uh... My name is not important.");
					else if (talkAmount == 2)
						gm.printLine(" - You're going to kick my ass if I don't tell you who I am? Uhm... Well... Hmm...");
					else if (talkAmount == 3)
						gm.printLine(" - Hey that's not a very nice thing to say about my mother! Just because you're \"Mr Protagonist\" and everything doesn't mean you can just boss everybody around!");
					else if (talkAmount == 4)
						gm.printLine(" - Oh yeah!? You're going to kick my ass?! Well come at me boy!!!");
					else if (talkAmount == 5)
						gm.printLine(" - You're wondering how to kick my ass? Really!? Huh... Fine. Type \"kick my own ass\" and press enter.");
					else if (talkAmount == 6)
						gm.printLine(" - Hihihihi! He's such a dumba- Oh... Still here? Hey uh... Whaddya say about burying the axes? Waddya say, peace?");
					else if (talkAmount == 7)
						gm.printLine(" - Aight, my man. We's all coolio now. Hey you want me to turn on the lights? I'll do that for you man, but I'm kind of hungry, do you have an apple or something?");
					else if (talkAmount > 7)
						gm.printLine(" - I'll turn on the lights as soon as you give me an apple man. So hungry...");
					
					talkAmount += 1;
				}
				else
				{
					gm.printLine(" - Thanks man :)");
				}
			}
		});
		aliases = new String[] { "give-to" };
		guy.getIndObjCommands().put("givetoguy", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				if (gm.getPlayer().hasItem("apple") && gm.getItem("apple").getNames().contains(sentence.getDirObject().getName())) {
					gm.getItem("apple").destroy();
					gm.setData("lightsTurnedOn", new Boolean(true));
					
					gm.printLine("You give the apple to the guy. He says:");
					gm.printLine(" - Wow, thanks man! Alright, I'll turn on the light!");
					gm.printLine("The lights came on!");
					gm.printLine("");
					room.look();
				}
				else
					gm.printLine("Now why would you do that exactly?");
			}
		});
		
		aliases = new String[] { "listen", "hear" };
		room.getLiteralCommands().put("listen", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				if (!(Boolean)gm.getData("lightsTurnedOn").equals(Boolean.TRUE)) {
					gm.printLine("It's deadly silent, it's so silent you can almost hear your own heart bea- Wait... No, you think you can hear somebody breathing in and out.");
					gm.printLine("There's somebody in the room, maybe you should try talking to him?");
					guy.setVisibility(true);
				} else {
					gm.printLine("You hear the sound of your fingers typing a pointless command.");
				}
			}
		});
		aliases = new String[] { "kick my own ass" };
		room.getLiteralCommands().put("kickmyownass", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				gm.printLine("You went about and kicked your own ass. You died.");
				running = false;
			}
		});
		aliases = new String[] { "kick his ass" };
		room.getLiteralCommands().put("kickhisass", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				if ((Boolean)gm.getData("lightsTurnedOn").equals(Boolean.FALSE) && guy.isVisible()) {
					gm.printLine("Now you got pumped and was all ready to kick his ass and everything. You ran forward to the sound of his voice and then -");
					gm.printLine("BOOM");
					gm.printLine("CRASH");
					gm.printLine("You fell crashing on the floor. There must've been some kind of trapdoor in that room.");
					gm.printLine("You hear the sound of laughter somewhere above you.");
					
					gm.getPlayer().setLocation("cellar");
				} else {
					gm.printLine("Why would you do that?");
				}
			}
		});
		
		gm.addRoom(room);	
		gm.addItem(southernDoor, room.getIdentifier());
		gm.addItem(northernDoor, room.getIdentifier());
		gm.addItem(guy, room.getIdentifier());
	}
	public static Door blackRoom_createSoutherDoor(final Game gm) {
		final Door door = new Door(gm) {
			@Override
			public void look() {
				gm.printLine("It's a door.");
			}
		};
		door.setIdentifier("blackRoom_door");
		door.getNames().add("southern door");
		door.setLeadsTo("white room", "black room");
		
		door.initialize();
		
		return door;
	}
	public static Door blackRoom_createNorthernDoor(final Game gm) {
		final Door door = new Door(gm) {
			@Override
			public void look() {
				gm.printLine("It's a door.");
			}
		};
		door.setIdentifier("blackRoom_door");
		door.getNames().add("northern door");
		door.setLeadsTo("elevator room", "black room");
		
		door.initialize();
		
		return door;
	}

	public static void createCellar(final Game gm) {
		final Door ladder = cellar_createLadder(gm);
		
		Room room = new Room(gm) {
			@Override
			public void onEntry() {}
			
			@Override
			public void look() {
				String line = 	"You are in a cellar of some sorts. It's got that torture-chamber ambience that just makes you want to get out as quickly as possible.\n\n";
				line +=			"On the eastern wall there's some grafiti saying, in large bold letters: \"YOU SUCK!\". \n\n";
				line +=			"There's a ladder here, maybe that's a way out?";
				gm.printLine(line);
			}
		};
		room.initialize();
		room.setIdentifier("cellar");
		
		String[] aliases = new String[] { "go up"};
		room.getLiteralCommands().put("goup", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
				ladder.goThrough();
			}
		});
		
		gm.addRoom(room);
		gm.addItem(ladder, room.getIdentifier());
	}
	public static Door cellar_createLadder(final Game gm) {
		final Door ladder = new Door(gm) {
			@Override
			public void look() {
				gm.printLine("It's a ladder.");
			}
		};
		ladder.setIdentifier("cellar_ladder");
		ladder.getNames().add("ladder");
		ladder.setLeadsTo("cellar", "elevator room");
		
		ladder.initialize();
		
		return ladder;	
	}

	public static void createElevatorRoom(final Game gm) {
		final Door elevator = elevatorRoom_createElevator(gm);
		
		Room room = new Room(gm) {
			@Override
			public void onEntry() {
				gm.printLine("The entrance to the room closed behind you.");
				gm.printLine("");
				gm.printLine("You just got into the elevator room. There's an elevator here, that's it. I'm not even going to tell you the color of the walls.");
				gm.printLine("...");
				gm.printLine("You know what? I'm going to be nice and help you out.");
				gm.printLine("I'm just gonna break it to ya. You pretty much suck at this game. Like... Really badly.");
				gm.printLine("So I'll help you just this once, so you better listen...");
				gm.printLine("");
				gm.printLine("If you go up with this elevator, you'll either be dead or alive within the next 3 minutes.");
				gm.printLine("Staying here isn't much of an option, so you'll just have to prepare yourself the best you can before going up.");
			}
			
			@Override
			public void look() {
				gm.printLine("You're in the elevator room. There's an elevator here, that's it. I'm not even going to tell you the color of the walls.");
			}
		};
		room.initialize();
		room.setIdentifier("elevator room");
		
		gm.addRoom(room);
		gm.addItem(elevator, room.getIdentifier());
	}
	public static Door elevatorRoom_createElevator(final Game gm) {
		final Door elevator = new Door(gm) {
			@Override
			public void look() {
				gm.printLine("It's an elevator, who knows where it leads to.");
			}
		};
		elevator.setIdentifier("elevator");
		elevator.getNames().add("elevator");
		elevator.setLeadsTo("elevator room", "last room");
		
		elevator.initialize();
		
		return elevator;	
	}

	public static void createLastRoom(final Game gm) {
		final Repeater bigBattle = new Repeater(gm) {
			private int counter = 0;
			private boolean phase2 = false;
			
			@Override
			public void doAction() {
				if (phase2) {
					doAction2();
					return;
				}
					
				if (counter == 0) {
					gm.printLine("\"Wow\" is what you're thinking right now.");
					gm.printLine("");
					gm.printLine("You are in a room in which every wall is covered by TV screens, and every single one of them is showing a live-feed of... you.");
					gm.printLine("");
					gm.printLine("In the middle of the room there's an old guy in a white suit. You see him right? Guess who that is...");
					gm.printLine("");
					gm.printLine("That's me.");
				} else if (counter == 1) {
					gm.printLine("Yep. That's how I look like. I'm waving at you right now, can you see it?");
				} else if (counter == 2) {
					gm.printLine("I know you can see me.");
				} else if (counter == 3) {
					gm.printLine("I know everything about you.");
				} else if (counter == 4) {
					gm.printLine("I even know your name.");
				} else if (counter == 5) {
					gm.printLine("I even know why you're in here, why you are playing this silly little game.");
				} else if (counter == 6) {
					gm.printLine("You remember how I said that you'd be either dead or alive within 3 minutes of entering this room?");
				} else if (counter == 7) {
					gm.printLine("I was lying. You're going to die, no matter how prepared you are.");
				} else if (counter == 8) {
					gm.printLine("Did you hear that?");
				} else if (counter == 9) {
					gm.printLine("You're going to die.");
				} else if (counter == 10) {
					gm.printLine("Do you now why you're in here?");
				} else if (counter == 11) {
					gm.printLine("You're in here because you think this is a silly little game.");
				} else if (counter == 12) {
					gm.printLine("You think that I'm some silly little narrator.");
				} else if (counter == 13) {
					gm.printLine("You know who's silly?");
				} else if (counter == 14) {
					gm.printLine("You are. You want to know why?");
				} else if (counter == 15) {
					gm.printLine("It's because you're not the one who's playing the game.");
				} else if (counter == 16) {
					gm.printLine("It's me. \n\nI'm releasing the dogs.");
				} else if (counter == 17) {
					gm.printLine("There's no way to win this. I hope you've realized that.");
					gm.printLine("");
					gm.printLine("The dogs are running towards you. They look hungry.");
				} else if (counter == 18) {
					gm.printLine("You can just lie down on the ground and let it happen.");
					gm.printLine("");
					gm.printLine("The dogs are getting closer. They look hungry.");
				} else if (counter == 19) {
					gm.printLine("You could just lie down on the ground and let it happen.");
					gm.printLine("");
					gm.printLine("They look even hungrier up close.");
				}
				else {
					
					if (gm.getData("finalRoomIsSlippery").equals(Boolean.FALSE)) {
						gm.printLine("Goodbye.");
						gm.printLine("");
						gm.printLine("You died.");
						running = false;
					} else {
						gm.printLine("The dogs slipped and fell down on their stomachs. They try to get up again but they can't.");
						gm.printLine("");
						gm.printLine("Wait... What are you doing? What did you do to my dogs? YOU BASTARD!!!");
						phase2 = true;
						counter = 0;
					}
				}
				
				counter++;
			}
			
			public void doAction2() {
				if (counter == 0) {
					gm.printLine("I'll just have to kill you myself.");
				} else if (counter == 1) {
					gm.printLine("Luckily I have my laser gun handy. You're going to evaporate. Goodbye.");
				} else if (counter == 2) {
					if (gm.getData("mirrorUsed").equals(Boolean.FALSE)) {
						gm.printLine("You died.");
						running = false;
					} else {
						gm.printLine("The laser beam reflected back to the old-man, making him explode into a million pieces.");
						gm.printLine("");
						gm.printLine("You survived. Congratulations. How does it feel?");
						running = false;
					}
				}
				
				counter++;
			}
		};
		
		Room room = new Room(gm) {
			@Override
			public void onEntry() {
				game.getRepeaters().add(bigBattle);
			}
			
			@Override
			public void look() {
				gm.printLine("I've already told you what the room looked like... A bunch of TV:s + old guy in the middle.");
			}
		};
		room.initialize();
		room.setIdentifier("last room");
		gm.setData("finalRoomIsSlippery", new Boolean(false));
		gm.setData("mirrorUsed", new Boolean(false));
		
		String[] aliases = new String[] { "" };
		room.getLiteralCommands().put("wait", new Command(aliases) {
			@Override
			public void doAction(Sentence sentence) {
			}
		});
		
		aliases = new String[] { "help", "help me" };
		room.getLiteralCommands().put("help" , new Command(aliases) {
			int numHelp = 0;
			
			@Override
			public void doAction(Sentence sentence) {
				gm.printLine("I've helped you enough already. Prepare to die.");
			}
		});
		
		gm.addRoom(room);
	}
}
