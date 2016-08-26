package texto.parsing;

import java.util.Arrays;
import java.util.Vector;

public class Parser
{
	//TODO: sometimes adverbs can be adjectives.
	//TODO: Names like "James Douglas".
	
	private WordBank wordBank;

	public Parser(WordBank wordBank) {
		this.wordBank = wordBank;
	}
	
	public Sentence parse(String stringSentence)
	{
		stringSentence = stringSentence.toLowerCase();
		//"say \"x\" to him"
		String[] sentenceArray;
		
		//How many " occurs within stringSentence
		int quoteCount = 0;
		for (int i = 0; i < stringSentence.length(); i++)
			if (stringSentence.charAt(i) == '\"')
				quoteCount++;
		
		if (quoteCount == 2)
		{
			String quote = stringSentence.substring(stringSentence.indexOf('\"'), stringSentence.lastIndexOf('\"') - stringSentence.indexOf('\"') + 1);
			quote = quote.replace("\"", "");
			String sentencePart1 = stringSentence.substring(0, stringSentence.indexOf('\"'));
			String sentencePart2 = stringSentence.substring(stringSentence.lastIndexOf('\"') + 1);
			Vector<String> sentenceVector = new Vector<String>();
			sentenceVector.addAll(Arrays.asList(sentencePart1.split(" ")));
			sentenceVector.add(quote);
			sentenceVector.addAll(Arrays.asList(sentencePart2.split(" ")));
			
			for (int i = 0; i < sentenceVector.size(); i++)
				if (sentenceVector.get(i).equals("") || sentenceVector.get(i).equals(" "))
					sentenceVector.remove(i);
			sentenceArray = (String[]) sentenceVector.toArray();
		}
		else
			sentenceArray = stringSentence.split(" ");

		Sentence sentence = new Sentence();
		//String[] sentenceArray = StringSentence.Split(new[] {' '});

		Vector<Integer> takenWords = new Vector<Integer>();

		sentence.setVerb(sentenceArray[0]);
		takenWords.add(0);
		
		sentence = findObjects(sentenceArray, takenWords, sentence);
		sentence = findModifier(sentenceArray, takenWords, sentence);
		sentence = findAdverbs(sentenceArray, takenWords, sentence);

		return sentence;
	}

	public Sentence findObjects(String[] sentenceArray, Vector<Integer> takenWords, Sentence sentence)
	{
		//Find verb modifier
		for (int i = 1; i < sentenceArray.length; i++)
		{
			if (takenWords.contains(i))
				continue;

			if (wordBank.findTypeOfItem(sentenceArray[i]).contains(WordTypes.Modifier))
			{
				sentence.setVerbModifier(sentenceArray[i]);

				takenWords.add(i);

				for (int n = i + 1; n < sentenceArray.length; n++)
				{
					if (takenWords.contains(n))
						continue;

					if (sentenceArray[n].equals("the") || sentenceArray[n].equals("and"))
					{
						takenWords.add(n);
						continue;
					}

					if (n == sentenceArray.length - 1 || !wordBank.findTypeOfItem(sentenceArray[n + 1]).contains(WordTypes.Other))
					{
						sentence.getVerbModifierObject().setName(sentenceArray[n]);
						takenWords.add(n);
						break;
					}

					sentence.getVerbModifierObject().getAdjectives().add(sentenceArray[n]);
					takenWords.add(n);
				}
			}

		}

		return sentence;
	}

	public Sentence findModifier(String[] sentenceArray, Vector<Integer> takenWords, Sentence sentence)
	{
		//Find preposition and the indirect object after it.
		for (int i = 1; i < sentenceArray.length; i++)
		{
			if (takenWords.contains(i))
				continue;

			if (wordBank.findTypeOfItem(sentenceArray[i]).contains(WordTypes.Preposition))
			{
				takenWords.add(i);
				sentence.setPreposition(sentenceArray[i]);

				for (int n = i + 1; n < sentenceArray.length; n++)
				{
					if (takenWords.contains(n))
						continue;

					if (sentenceArray[n].equals("the") || sentenceArray[n].equals("and"))
					{
						takenWords.add(n);
						continue;	
					}

					if (n == sentenceArray.length - 1 || !wordBank.findTypeOfItem(sentenceArray[n + 1]).contains(WordTypes.Other))
					{
						sentence.getIndObject().setName(sentenceArray[n].toLowerCase());
						takenWords.add(n);
						break;
					}

					sentence.getIndObject().getAdjectives().add(sentenceArray[n]);
					takenWords.add(n);
				}
			}
		}

		//The final object should be the direct object.
		GrammarObject dirObj = new GrammarObject();
		for (int n = 0; n < sentenceArray.length; n++)
		{
			if (takenWords.contains(n))
				continue;

			if (sentenceArray[n].equals("the") || sentenceArray[n].equals("and"))
			{
				takenWords.add(n);
				continue;
			}

			if (n == sentenceArray.length - 1 || !wordBank.findTypeOfItem(sentenceArray[n + 1]).contains(WordTypes.Other) ||
				sentenceArray[n + 1].equals("the"))
			{
				dirObj.setName(sentenceArray[n]);
				takenWords.add(n);
				break;
			}

			dirObj.getAdjectives().add(sentenceArray[n]);
			takenWords.add(n);
		}

		//If there was no preposition, and also therefore no indirect object before it, but still the verb
		//can take a indirect object, the indirect object could be directly after the verb.
		//As in: "Give him the object". Which would mean that dirObj is actually the indirect object.
		//So now we have to check (if there was no indirect object detected.) if there is another object
		//to be discovered, if there is, that object is the direct, and dirObj the indirect.
		if (sentence.getIndObject().getName().equals(null))
		{
			for (int n = 0; n < sentenceArray.length; n++)
			{
				if (takenWords.contains(n))
					continue;

				if (sentenceArray[n].equals("the") || sentenceArray[n].equals("and"))
				{
					takenWords.add(n);
					continue;
				}

				if (n == sentenceArray.length - 1 || !wordBank.findTypeOfItem(sentenceArray[n + 1]).contains(WordTypes.Other) ||
					sentenceArray[n + 1].equals("the"))
				{
					sentence.getDirObject().setName(sentenceArray[n]);
					takenWords.add(n);
					break;
				}

				sentence.getDirObject().getAdjectives().add(sentenceArray[n]);
				takenWords.add(n);
			}

			if (!sentence.getDirObject().getName().equals(null))
				sentence.setIndObject(dirObj);
			else
				sentence.setDirObject(dirObj);
		}
		else
			sentence.setDirObject(dirObj);

		return sentence;
	}

	public Sentence findAdverbs(String[] sentenceArray, Vector<Integer> takenWords, Sentence sentence)
	{
		for (int i = 1; i < sentenceArray.length; i++)
		{
			if (takenWords.contains(i))
				continue;
			sentence.getAdverbs().add(sentenceArray[i]);
		}

		return sentence;
	}
}

