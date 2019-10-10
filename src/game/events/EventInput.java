package game.events;

public class EventInput extends Event
{
	public final String id;
	public final String icon;
	public final String input;
	
	public EventInput(String id, String icon, String input)
	{
		super(id, input);
		
		this.id = id;
		this.icon = icon;
		this.input = input.trim().toLowerCase();
	}
}
