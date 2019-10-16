package game.data;

public class Constants
{
	// Designs
	public static final String none = " ";
	public static final String dirt = "."; 
	public static final String grass = ",";
	public static final String water = "~";
	public static final String ocean = "#";
	public static final String house = "&";
	public static final String sand = "-";
	public static final String road = "=";
	
	// World info
	public static final String impassibleTerrain[] = { ocean, water };
	
	// Default variables
	public static final int defaultWidth = 40;
	public static final int defaultHeight = 25;
	public static final Point defaultSpawn = new Point(1, 1);
}
