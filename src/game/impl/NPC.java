package game.impl;

import java.util.UUID;

import core.Note;
import game.api.IEntity;
import game.api.IWorld;
import game.events.EventInput;

public class NPC implements IEntity
{
	private String uniqueID;
	private String icon;
	private long lastWanderTime; // in ms
	private long wanderDelay;    // in ms
	private boolean isWandering;
	private IWorld currentWorld;
	
	public static final String defaultIcon = "%";
	public static final String confusedIcon = "0";
	
	public NPC(IWorld targetWorld)
	{
		this.uniqueID = defaultIcon + UUID.randomUUID().toString();
		this.icon = defaultIcon;
		this.lastWanderTime = System.currentTimeMillis();
		this.wanderDelay = (int)(Math.random() * 5000) + 250;
		this.currentWorld = targetWorld;
		this.isWandering = true;
		
		Note.Log("NPC Created with wander delay of " + this.wanderDelay);
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

	public void setIcon(String newIcon)
	{
		this.icon = newIcon;
	}
	
	public void setIsWandering(boolean isWandering)
	{
		this.isWandering = isWandering;
	}
	
	public boolean getIsWandering()
	{
		return isWandering;
	}
	
	@Override
	public boolean updateEntity()
	{
		long current = System.currentTimeMillis();
		if(lastWanderTime + wanderDelay < current && isWandering)
		{
			lastWanderTime = current;
			currentWorld.queueEvent(wander());
		}
		
		return true;
	}
	
	
}
