package game;

public class Player
{
	private String uniqueID;
	private String icon;
	
	public Player(String uniqueID)
	{
		this(uniqueID, "@");
	}
	
	public Player(String uniqueID, String icon)
	{
		this.uniqueID = uniqueID;
		this.icon = icon;
	}
	
	public String getIcon()
	{
		return icon;
	}
	
	public String getID()
	{
		return uniqueID;
	}
}
