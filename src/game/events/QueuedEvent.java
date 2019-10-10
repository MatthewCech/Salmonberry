package game.events;

import game.Player;

// All the info about an event
public class QueuedEvent
{
	private long time;
	private Event event;
	private Player player;
	
	public QueuedEvent(long time, Event event, Player player)
	{
		this.time = time;
		this.event = event;
		this.player = player;
	}
	
	public long getTime()
	{
		return time;
	}
	
	public Event getEvent()
	{
		return event;
	}
	
	public String getPlayerID()
	{
		return player.getID();
	}
}
