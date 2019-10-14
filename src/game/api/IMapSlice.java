package game.api;

import game.impl.MapSlice;

public interface IMapSlice
{
	public MapSlice clone();

	public int getWidth();
	public int getHeight();
	
	public String get(int x, int y);
	public void set(int x, int y, String newValue);
	
	public String toString();
}
