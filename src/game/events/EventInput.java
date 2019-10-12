package game.events;

import game.api.IEvent;

public class EventInput implements IEvent
{
	private final String input;
	private final String id;
	
	public EventInput(String id, String input)
	{
		this.id = id;
		this.input = input.trim().toLowerCase();
	}

	@Override
	public String getID()
	{
		return id;
	}

	@Override
	public String getData()
	{
		return input;
	}
}
