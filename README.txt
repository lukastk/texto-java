An attempt to explain the way Texto works:


Indirect- and direct object commands:
	In Texto, sentences that the player enters can be relatively complex. For example, sentences such as:
		- throw the key to the man through the window.
		- put in money
		- use key with door
		- take the ball

	This is how the first example would be parsed:
		Verb: throw
		Direct object: key
		Preposition: to
		Indirect object: man
		Verb-modifier: through
		Verb-modifier object: window

	But sentences of this complexity are unnessecary to this game, so let's look at the second example:
		Verb: put
		Preposition: in 
		Indirect object: money

	This is an "indirect command". Because the main object an indirect object within the sentence.

	So what happens after the user enters "put in money"?

	First the sentence is parsed of course. Then, it searches for an item with the name "money" within the players visible range. If it finds one, it checks if the item has an indirect object command called "put-in".

	It is the same with the 3rd example. The game would search for an item with the name "door", and see if it has an indirect object command with the name "use-with".

	The 4th example is different, becuse in that case there's only a direct object in the sentence. In that case the game would search for an item with the name "ball", and see if it has a direct object command called "take".

Literal commands:
	There is a third type of command, a "literal" command.

	Examples:
		- go north
		- look

	When a player enters a new entry, before parsing the entry, the game checks if the entry corresponds to any of these literal commands. The literal commands are all stored in the current room that the player is in, default commands that are always there are commands like: "look", "go north", "north", "n" etc. etc.
