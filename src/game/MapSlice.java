package game;

import java.util.ArrayList;
import java.util.List;

import core.Note;

// This is a given 2D piece of a map. A map may lead to or reference
// other maps - you can think of this as a single level of a multi-level
// dungeon
public class MapSlice
{
	// Variables
	public static final String empty = "."; 
	
	private int width;
	private int height;
	private List<List<String>> map;
	
	// Constructor
	public MapSlice(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.map = new ArrayList<>();
		
		for(int y = 0; y < height; ++y)
		{
			List<String> item = new ArrayList<String>();
			
			for(int x = 0; x < width; ++x)
			{
				item.add(empty);
			}
			
			map.add(item);
		}
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public String get(int x, int y)
	{
		if(!verify(x, y))
			return null;
		
		return map.get(y).get(x);
	}
	
	@Override
	public String toString()
	{
		String out = "";

		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				out += get(x, y);
			}
			
			out += "\n";
		}
		
		return out;
	}
	
	public void set(int x, int y, String newValue)
	{
		if(!verify(x, y))
			return;
	
		map.get(y).set(x, newValue);
	}
	
	// Utility function to determine if something is in or out of range
	private boolean verify(int x, int y)
	{
		if(x < 0 || x > width - 1)
		{
			Note.Warn("Attempting to access X location outside of map: " + x);
			return false;
		}
		
		if(y < 0 || y > height - 1)
		{
			Note.Warn("Attempting to access Y location outside of map: " + y);
			return false;
		}
		
		return true;
	}
}
