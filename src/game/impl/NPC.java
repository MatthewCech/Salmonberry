package game.impl;

import java.util.UUID;

import game.api.IEntity;
import game.events.EventInput;

public class NPC implements IEntity
{
	private String uniqueID;
	private String icon;
	private long lastWanderTime; // in ms
	private long wanderDelay;    // in ms
	
	public NPC()
	{
		this.uniqueID = "n" + UUID.randomUUID().toString();
		this.icon = "n";
		this.lastWanderTime = System.currentTimeMillis();
		this.wanderDelay = (int)(Math.random() * 2000) + 500;
	}
	
	public EventInput wander()
	{
		int dir = (int)(Math.random() * 4);
		String input = null; 
		switch(dir)
		{
			case 0: input = "U"; break;
			case 1: input = "D"; break;
			case 2: input = "L"; break;
			case 3: input = "R"; break;
		}
		
		return new EventInput(uniqueID, input);
	}
	
	@Override
	public String getID()
	{
		return uniqueID;
	}
	
	@Override
	public String getASCII()
	{
		return icon;
	}

	@Override
	public boolean updateEntity()
	{
		long current = System.currentTimeMillis();
		if(lastWanderTime + wanderDelay < current)
		{
			lastWanderTime = current;
			wander();
		}
		
		return true;
	}
	
	
}
