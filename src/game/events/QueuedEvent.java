package game.events;

import game.api.IEntity;
import game.api.IEvent;

// All the info about an event
public class QueuedEvent
{
	private long time;
	private IEvent event;
	private IEntity entity;
	
	public QueuedEvent(long time, IEvent event, IEntity entity)
	{
		this.time = time;
		this.event = event;
		this.entity = entity;
	}
	
	public long getTime()
	{
		return time;
	}
	
	public IEvent getEvent()
	{
		return event;
	}
	
	public String getEntityID()
	{
		return entity.getID();
	}
}
