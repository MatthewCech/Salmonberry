package game.events;

public class EventCreate extends Event
{
	public final String icon;

	public EventCreate(String id)
	{
		super(id, id);
		
		this.icon = "" + id.trim().toUpperCase().charAt(0);
	}
}
