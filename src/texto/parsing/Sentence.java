package texto.parsing;

import java.util.Vector;

public class Sentence
{
	private String verb = "";
	private Vector<String> adverbs = new Vector<String>();
	private String preposition = "";
	private GrammarObject dirObject = new GrammarObject();
	private GrammarObject indObject = new GrammarObject();
	private String verbModifier = "";
	private GrammarObject verbModifierObject = new GrammarObject();
	
	public String getVerb() { return verb; }
	public void setVerb(String _verb) { verb = _verb; }
	public Vector<String> getAdverbs() { return adverbs; }
	public String getPreposition() { return preposition; }
	public void setPreposition(String _preposition) { preposition = _preposition; }
	public GrammarObject getDirObject() { return dirObject; }
	public void setDirObject(GrammarObject dirObject) { this.dirObject = dirObject; }
	public GrammarObject getIndObject() {return indObject;}
	public void setIndObject(GrammarObject indObject) { this.indObject = indObject; }
	public String getVerbModifier() { return verbModifier; }
	public void setVerbModifier(String verbModifier) { this.verbModifier = verbModifier; }
	public GrammarObject getVerbModifierObject() { return verbModifierObject; }
	public void setVerbModifierObject(GrammarObject verbModifierObject) { this.verbModifierObject = verbModifierObject; }

}