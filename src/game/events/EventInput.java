package game.events;

public class EventInput extends Event
{
	public final String input;
	
	public EventInput(String id, String input)
	{
		super(id, input);
		
		this.input = input.trim().toLowerCase();
	}
}
