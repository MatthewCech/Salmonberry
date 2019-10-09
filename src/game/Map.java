package game;

import java.util.ArrayList;
import java.util.List;

public class Map
{
	private int count;
	private List<MapSlice> slices;
	
	public Map(int numSlices, int width, int height)
	{
		this.count = numSlices;
		this.slices = new ArrayList<MapSlice>();
		
		initialize(width, height);
	}
	
	private void initialize(int width, int height)
	{
		for(int i = 0; i < count; ++i)
		{
			slices.add(new MapSlice(width, height));
		}
	}
}
