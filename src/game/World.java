package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import core.Note;
import core.Pair;
import game.events.Event;
import game.events.EventCreate;
import game.events.EventInput;
import game.events.QueuedEvent;

public class World
{
	// Variables
	public final int defaultWidth = 60;
	public final int defaultHeight = 20;
	
	// Data
	private MapSlice slice;
	public List<Pair<Player, Point>> players;
	
	// Event queue
	public List<QueuedEvent> eventsQueue;
	public List<Runnable> onUpdateFinished; // Never cleared
	public List<Runnable> onUpdateFinishedOnce; // Always cleared
	
	// Constructor
	public World()
	{
		this.slice = new MapSlice(defaultWidth, defaultHeight);
		this.players = new ArrayList<Pair<Player, Point>>();
		this.eventsQueue = new ArrayList<QueuedEvent>();
		this.onUpdateFinished = new ArrayList<Runnable>();
		this.onUpdateFinishedOnce = new ArrayList<Runnable>();
		
		Note.Log("CONSTRUCTOR");
	}
	
	private Pair<Player, Point> getPlayerPair(String id)
	{
		Pair<Player, Point> player = null;
		
		for(Pair<Player, Point> p : players)
		{
			Note.Log("ID checking: " + p.first.getID());
			if(p.first.getID().equalsIgnoreCase(id))
			{
				Note.Log("Found " + id + "" + players.size());
				player = p;
				break;
			}
		}
		
		return player;
	}
	
	public void consumeEventCreate(Event event)
	{
		if(event instanceof EventCreate)
		{
			EventCreate data = (EventCreate)event;
			
			players.add(
				new Pair<Player, Point>(
					new Player(data.getID(), data.icon),
					new Point(1,1)));
		}
		else
		{
			Note.Error("Consuming the 'Create' event didn't work!");
		}
	}
	
	// Testing input stub for player
	public void consumeEventInput(Event event)
	{
		long time = System.currentTimeMillis();
		Pair<Player, Point> player = getPlayerPair(event.getID());
		
		if(player == null)
		{
			Note.Log("Could not find player with ID '" + event.getID() + "' - making player");
			

			return;
		}
		
		synchronized(eventsQueue)
		{
			eventsQueue.add(new QueuedEvent(time, event, player.first));
		}
	}
	
	// Happens right before the end of the update function, but before single-upate
	// events. These are not discarded.
	public void registerOnUpdateFinished(Runnable targetFunction)
	{
		if(targetFunction != null)
		{
			this.onUpdateFinished.add(targetFunction);
		}
	}
	
	// This happens at the very very end, AFTER RECURRING EVENTS.
	public void registerOnUpdateFinishedOnce(Runnable targetFunction)
	{
		if(targetFunction != null)
		{
			this.onUpdateFinishedOnce.add(targetFunction);
		}
	}
	
	// Updates the world, and returns if it was successful or not.
	// First, pop off all events quickly so the queue can be filled elsewhere.
	// Then, in whatever order we've chosen (we could sort by time, for example) process events for the world.
	public boolean update()
	{
		List<QueuedEvent> toProcess = new ArrayList<QueuedEvent>();
		
		synchronized(eventsQueue)
		{
			for(QueuedEvent data : eventsQueue)
			{
				toProcess.add(data);
			}
			
			eventsQueue.clear();
		}
		
		if(toProcess.size() > 0)
		{
			Note.Log("Found some events to process: " + toProcess.size());
		}
		
		for(QueuedEvent data : toProcess)
		{
			Event event = data.getEvent();
			if(event instanceof EventInput)
			{
				Pair<Player, Point> player = getPlayerPair(event.getID());
				
				switch(((EventInput)event).input)
				{
					case "u": player.second.y -= 1; break;
					case "d": player.second.y += 1; break;
					case "l": player.second.x -= 1; break;
					case "r": player.second.x += 1; break;
				}
			}
		}
		
		// Fire off events as necessary.
		for(Runnable r : onUpdateFinished)
		{
			r.run();
		}
		
		for(Runnable r : onUpdateFinishedOnce)
		{
			r.run();
		}
		onUpdateFinishedOnce.clear();
		
		return true;
	}
	
	// Constructs the world as an ASCII location
	public String getWorldAsASCII()
	{	
		MapSlice visual = slice.clone();
		for(Pair<Player, Point> p : players)
		{
			Point loc = p.second;
			visual.set(loc.x, loc.y, p.first.getIcon());
		}
		
		return visual.toString();
	}
	
	@Override
	public String toString()
	{
		// Write visual to string
		String out = "";
		out += "World state:\n\n";
		out += getWorldAsASCII();
		
		return out;
	}
}
