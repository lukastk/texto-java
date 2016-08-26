package texto;

/**
 * A basic interface for any kind of text output/input wrapper.
 * @author Lukas
 *
 */
public interface TextManager {
	public void print(String text);
	public void printLine(String text);
	public void clear();
	public String getInput();
	public boolean isReady();
}
