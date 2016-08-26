package texto;

import texto.parsing.Sentence;

/*
 * Serves as the base class for any action that are supposed be performed as a response to a specific user entry.
 */
public abstract class Command {
	private String[] aliases;
	
	/*
	 * An array of all of the "aliases" of this command. It's basically a list of all of the verbs that are assoicated with this command.
	 * For example: an alias of the form "take", corresponds to any user input where there is a verb "take" and the sentence does not
	 * have an indirect object.
	 * 
	 * Another example: an alias of the form "give-to", corresponds to any user input where there is a verb "give" and the sentence has an indirect object
	 * preceded by the preposition "to".
	 */
	public String[] getAliases() {
		return aliases;
	}
	
	public Command(String[] aliases) {
		this.aliases = aliases;
	}
	
	public abstract void doAction(Sentence sentence);
}
