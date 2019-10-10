package game.events;

// All events must contain this information
public abstract class Event
{
	protected String id;   // The id for the client
	protected String data; // The more general / raw event data content
	
	public Event(String clientID, String data)
	{
		this.id = clientID;
		this.data = data;
	}
	
	public String getData()
	{
		return data;
	}
	
	public String getID()
	{
		return id;
	}
}
