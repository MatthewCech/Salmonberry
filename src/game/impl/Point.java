package game.impl;

public class Point
{
	public int x;
	public int y;
	
	public Point()
	{
		this.x = 0;
		this.y = 0;
	}
	
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Point clone()
	{
		return new Point(this.x, this.y);
	}
	
	// Min is always 0 for both directions.
	public static Point random(int xMax, int yMax)
	{
		int x = (int)(Math.random() * xMax);
		int y = (int)(Math.random() * yMax);
		
		return new Point(x, y);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof Point)
		{
			if(((Point) other).x == this.x && ((Point) other).y == this.y)
			{
				return true;
			}
		}
		
		return false;
	}
}
