package game;

public class World
{
	// Variables
	public final int defaultWidth = 60;
	public final int defaultHeight = 20;
	
	private MapSlice slice;
	
	
	// Constructor
	public World()
	{
		slice = new MapSlice(defaultWidth, defaultHeight);
	}
	
	// Testing input stub for player
	public void inputStub(String input)
	{
		
	}
	
	// Update
	public boolean update()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		String out = "";
		
		out += "World state:\n\n";
		out += slice.toString();
		
		return out;
	}
}
