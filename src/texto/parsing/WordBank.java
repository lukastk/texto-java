package texto.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.EnumSet;
import java.util.Set;
import java.util.Vector;

/**
 * Holds a list of all of the used words within the game. It reads them from a specified file and loads them into memory.
 * The wordlist helps the parser parse.
 * @author Lukas
 *
 */
public class WordBank
{
	private Vector<String> pronounList = new Vector<String>();
	private Vector<String> modifierList = new Vector<String>();
	private Vector<String> prepositionList = new Vector<String>();
	private Vector<String> adverbList = new Vector<String>();
	private Vector<String> nameList = new Vector<String>(); //List of names and nouns.

	public Vector<String> getPronounList() { return pronounList; }
	public Vector<String> getModifierList() { return modifierList; }
	public Vector<String> getPrepositionList() { return prepositionList; }
	public Vector<String> getAdverbList() { return adverbList; }
	public Vector<String> getNameList() { return nameList; }
	
	public void initialize(String wordListDir)
	{
		try {
			//Read the pronouns, modifiers, prepositions off a list.
			FileInputStream fs = new FileInputStream(new File(wordListDir)); 
			
			String file = readStream(fs);
			fs.close();
			
			file = file.replace("\r\n", "");
			String[] fileArray = file.split("\\(");
			
			for (String wordList : fileArray)
			{
				if (wordList.equals(""))
					continue;

				Vector<String> words = null;
				if (wordList.startsWith("Pronouns"))
				{
					words = pronounList;
				}
				else if (wordList.startsWith("Modifiers"))
				{
					words = modifierList;
				}
				else if (wordList.startsWith("Prepositions"))
				{
					words = prepositionList;
				}
				else if (wordList.startsWith("Adverbs"))
				{
					words = adverbList;
				}

				for (String word : wordList.split("\\)")[1].split("\\;"))
				{
					if (word == "")
						continue;
					words.add(word);
				}
			}
		} catch (IOException e) {
			System.out.println("Error with reading " + wordListDir);
		}
	}

	public static String readStream(InputStream is) {
	    StringBuilder sb = new StringBuilder(512);
	    try {
	        Reader r = new InputStreamReader(is, "UTF-8");
	        int c = 0;
	        while (c != -1) {
	            c = r.read();
	            sb.append((char) c);
	        }
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	    return sb.toString();
	}
/*	
	public void getItemNames()
	{
		//Goes through Settings.GameWorld.ItemsList (and RoomList) for names and nouns.
		
		for (Item item : GameWorld.Items.Values)
		{
			nameList.AddRange(item.Names);
		}
	}
*/
	public Set<WordTypes> findTypeOfItem(String word)
	{
		//Finds out the type of the word given. Or more precisely, if the word is a "object" type word,
		//in other words a pronoun/noun/name or some thing else. It returns other if it is a "object" word.

		Set<WordTypes> wordType = EnumSet.noneOf(WordTypes.class);
		
		if (modifierList.contains(word))
			wordType.add(WordTypes.Modifier);
		if (prepositionList.contains(word))
			wordType.add(WordTypes.Preposition);
		if (adverbList.contains(word))
			wordType.add(WordTypes.Adverb);

		if (wordType.size() == 0) {
			wordType.add(WordTypes.Other);
			return wordType;
		}
		else
			return wordType;
	}

	public  boolean doesNameExist(String word)
	{
		//Finds if the words is a pronoun/noun/name.

		if (pronounList.contains(word) || nameList.contains(word))
			return true;

		return false;
	}
}