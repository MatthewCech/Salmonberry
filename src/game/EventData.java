package game;

// All the info about an event
public class EventData
{
	private long time;
	private String data;
	private Player player;
	
	public EventData(long time, String data, Player player)
	{
		this.time = time;
		this.data = data;
		this.player = player;
	}
	
	public long getTime()
	{
		return time;
	}
	
	public String getData()
	{
		return data;
	}
	
	public String getPlayerID()
	{
		return player.getID();
	}
}
