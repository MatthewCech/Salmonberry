package game;

import java.util.ArrayList;
import java.util.List;

import core.Note;
import core.Pair;

public class World
{
	// Variables
	public final String testID = "test123";
	public final int defaultWidth = 60;
	public final int defaultHeight = 20;
	
	private MapSlice slice;
	public List<Pair<Player, Point>> players;
	
	// Constructor
	public World()
	{
		this.slice = new MapSlice(defaultWidth, defaultHeight);
		this.players = new ArrayList<Pair<Player, Point>>();
		
		players.add(new Pair<Player, Point>(new Player(testID, "@"), new Point(5, 5)));
	}
	
	// Testing input stub for player
	public void inputStub(String inputRaw)
	{
		String input = inputRaw.trim().toLowerCase();
		String id = testID;
		
		Pair<Player, Point> player = null;
		
		for(Pair<Player, Point> p : players)
		{
			if(p.first.getID() == id);
			{
				player = p;
				break;
			}
		}
		
		if(player == null)
		{
			Note.Warn("Could not find player with ID '" + id + "'");
			return;
		}
		
		switch(input)
		{
			case "u": player.second.y -= 1; break;
			case "d": player.second.y += 1; break;
			case "l": player.second.x -= 1; break;
			case "r": player.second.x += 1; break;
		}
	}
	
	// Update
	public boolean update()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		// Construct visual
		MapSlice visual = slice.clone();
		for(Pair<Player, Point> p : players)
		{
			Point loc = p.second;
			visual.set(loc.x, loc.y, p.first.getIcon());
		}
		
		// Write visual to string
		String out = "";
		out += "World state:\n\n";
		out += visual.toString();
		
		return out;
	}
}
