package texto;

import java.util.*;

/*
 * A wrapper for System.out.
 */
public class ConsoleManager implements TextManager {
	Scanner reader;
	
	public ConsoleManager() {
		reader = new Scanner(System.in);
	}
	
	@Override
	public void print(String text) {
		System.out.print(text);
	}

	@Override
	public void printLine(String text) {
		System.out.println(text);
	}

	@Override
	public void clear() {
		// Not implemented
	}

	public String getInput() {
        System.out.print("> ");     // print prompt
        return reader.nextLine();
	}

	@Override
	public boolean isReady() {
		return true;
	}
}
