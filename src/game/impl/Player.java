package game.impl;

import game.api.IEntity;

public class Player implements IEntity
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
	
	@Override
	public String getID()
	{
		return uniqueID;
	}
	
	@Override
	public String getASCII()
	{
		return icon;
	}

	@Override
	public boolean updateEntity()
	{
		return true;
	}
}
