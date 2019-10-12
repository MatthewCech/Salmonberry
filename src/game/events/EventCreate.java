package game.events;

import game.api.IEvent;

public class EventCreate implements IEvent
{
	private final String id;
	private final String icon;

	public EventCreate(String id)
	{
		this.id = id;
		this.icon = "" + id.trim().toUpperCase().charAt(0);
	}

	@Override
	public String getID()
	{
		return id;
	}

	@Override
	public String getData()
	{
		return id;
	}
	
	public String getIcon()
	{
		return icon;
	}
}
