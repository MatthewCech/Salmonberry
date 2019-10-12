package game.impl;

import java.util.ArrayList;
import java.util.List;

import core.Note;
import core.Pair;
import game.api.IEntity;
import game.api.IEvent;
import game.api.IWorld;
import game.events.EventCreate;
import game.events.EventInput;
import game.events.QueuedEvent;

public class World implements IWorld
{
	// Variables
	public final int defaultWidth = 40;
	public final int defaultHeight = 25;
	
	// Data
	private MapSlice slice;
	public List<Pair<IEntity, Point>> entities;
	
	// Event queue
	public List<QueuedEvent> eventsQueue;
	public List<Runnable> onUpdateFinished; // Never cleared
	public List<Runnable> onUpdateFinishedOnce; // Always cleared
	
	// Constructor
	public World()
	{
		this.slice = new MapSlice(defaultWidth, defaultHeight);
		this.entities = new ArrayList<Pair<IEntity, Point>>();
		this.eventsQueue = new ArrayList<QueuedEvent>();
		this.onUpdateFinished = new ArrayList<Runnable>();
		this.onUpdateFinishedOnce = new ArrayList<Runnable>();
		
		Note.Log("CONSTRUCTOR");
	}
	
	private Pair<IEntity, Point> getEntityPair(String id)
	{
		Pair<IEntity, Point> player = null;
		
		for(Pair<IEntity, Point> p : entities)
		{
			if(p.first.getID().equalsIgnoreCase(id))
			{
				player = p;
				break;
			}
		}
		
		return player;
	}
	
	public void consumeEventCreate(IEvent event)
	{
		if(event instanceof EventCreate)
		{
			EventCreate data = (EventCreate)event;
			
			Pair<IEntity, Point> pair = getEntityPair(data.getID());
			if(pair == null)
			{
				entities.add(
						new Pair<IEntity, Point>(
							new Player(data.getID(), data.getIcon()), new Point(1,1)));
				
				Note.Log("Created new player with id '" + data.getID() + "' and icon '" + data.getIcon() + "'");
			}
			else
			{
				Note.Log("Recieved requset for existing player with id '" + data.getID() + "' and icon '" + data.getIcon() + "'");
			}
		}
		else
		{
			Note.Error("Consuming the 'Create' event didn't work!");
		}
	}
	
	// Testing input stub for player
	public void consumeEventInput(IEvent event)
	{
		Pair<IEntity, Point> entity = getEntityPair(event.getID());
		
		if(entity == null)
		{
			Note.Log("Could not find entity with ID '" + event.getID());
			return;
		}
		
		QueueEvent(event);
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
			IEvent event = data.getEvent();
			if(event instanceof EventInput)
			{
				Pair<IEntity, Point> player = getEntityPair(event.getID());
				
				switch(((EventInput)event).getData())
				{
					case "u": 
						if(player.second.y > 0)
						{
							player.second.y -= 1;
						}
						break;
					case "d": 
						if(player.second.y < defaultHeight - 1)
						{
							player.second.y += 1;
						}
						break;
					case "l":
						if(player.second.x > 0)
						{
							player.second.x -= 1;
						}
						break;
					case "r":
						if(player.second.x < defaultWidth - 1)
						{
							player.second.x += 1;
						}
						break;
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
		for(Pair<IEntity, Point> p : entities)
		{
			Point loc = p.second;
			visual.set(loc.x, loc.y, p.first.getASCII());
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

	@Override
	public void QueueEvent(IEvent event)
	{
		synchronized(eventsQueue)
		{
			eventsQueue.add(new QueuedEvent(System.currentTimeMillis(), event, getEntityPair(event.getID()).first));
		}
	}
}
