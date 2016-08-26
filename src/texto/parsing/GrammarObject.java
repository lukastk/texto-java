package texto.parsing;

import java.util.Vector;

public class GrammarObject
{
	private String name = "";
	private Vector<String> adjectives = new Vector<String>();
	
	public String getName() { return name; }
	public void setName(String _name) { name = _name; }
	
	public Vector<String> getAdjectives() { return adjectives; }
}