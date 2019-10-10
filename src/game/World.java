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
	
	// Data
	private MapSlice slice;
	public List<Pair<Player, Point>> players;
	
	// Event queue
	public List<EventData> eventsQueue;
	
	// Constructor
	public World()
	{
		this.slice = new MapSlice(defaultWidth, defaultHeight);
		this.players = new ArrayList<Pair<Player, Point>>();
		this.eventsQueue = new ArrayList<EventData>();
		
		players.add(new Pair<Player, Point>(new Player(testID, "@"), new Point(5, 5)));
	}
	
	private Pair<Player, Point> getPlayerPair(String id)
	{
		Pair<Player, Point> player = null;
		
		for(Pair<Player, Point> p : players)
		{
			if(p.first.getID() == id);
			{
				player = p;
				break;
			}
		}
		
		return player;
	}
	
	// Testing input stub for player
	public void inputStub(String inputRaw)
	{
		long time = System.currentTimeMillis();
		String input = inputRaw.trim().toLowerCase();
		String id = testID;
		Pair<Player, Point> player = getPlayerPair(id);
		
		if(player == null)
		{
			Note.Warn("Could not find player with ID '" + id + "'");
			return;
		}
		
		synchronized(eventsQueue)
		{
			eventsQueue.add(new EventData(time, input, player.first));
		}
	}
	
	// Update
	public boolean update()
	{
		
		// First, pop off all events quickly so the queue can be filled elsewhere.
		List<EventData> toProcess = new ArrayList<EventData>();
		
		synchronized(eventsQueue)
		{
			for(EventData data : eventsQueue)
			{
				toProcess.add(data);
			}
			
			eventsQueue.clear();
		}
		
		if(toProcess.size() > 0)
		{
			Note.Log("Found some events to process: " + toProcess.size());
		}
		
		// In whatever order we've chosen (we could sort by time, for example) process input events for the world.
		
		for(EventData data : toProcess)
		{
			Pair<Player, Point> player = getPlayerPair(data.getPlayerID());
			switch(data.getData())
			{
				case "u": player.second.y -= 1; break;
				case "d": player.second.y += 1; break;
				case "l": player.second.x -= 1; break;
				case "r": player.second.x += 1; break;
			}
		}
		
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
