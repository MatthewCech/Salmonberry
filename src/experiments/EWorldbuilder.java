package experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LoggingMXBean;

import core.Note;
import game.api.IMapSlice;
import game.data.Constants;
import game.data.Point;
import game.data.SalmonRandom;
import game.impl.MapSlice;

public class EWorldbuilder
{
	public IMapSlice slice;
	
	public EWorldbuilder()
	{
		slice = new MapSlice(Constants.defaultWidth, Constants.defaultHeight);
		generateLumpyIsland(slice, new SalmonRandom());
		System.out.println(slice.toString());
	}

	public static void generateLumpyIsland(IMapSlice map, SalmonRandom rand)
	{
		Note.Log("----------");
		Note.Log("Starting map generation");
		
		// Acquire info
		int height = map.getHeight();
		int width = map.getWidth();
		String water = Constants.water;
		
		// Fill with defaults
		Note.Log("Cleaning slate...");
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				String toAdd = water;
				map.set(x, y, toAdd);
			}
		}
		
		
		// Get hubs
		Note.Log("Placing hubs...");
		List<Point> hubs = new ArrayList<Point>();
		hubs.add(new Point(1, 1));
		hubs.add(Point.random(width, height, rand));
		hubs.add(Point.random(width, height, rand));
		@SuppressWarnings("unused") Point side = Point.random(width, height, rand); // Omitted secondary city
		hubs.add(Point.random(width, height, rand));
		
		for(Point loc : hubs)
		{
			map.set(loc.x, loc.y, Constants.house);
		}
		
		// Fill with defaults
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				String current = map.get(x, y);
				String left = map.get(x - 1, y);
				String right = map.get(x + 1, y);
				String up = map.get(x, y - 1);
				String down = map.get(x, y + 1);
				
				if(current == Constants.house)
				{
					if(left != Constants.house) map.set(x - 1, y, Constants.grass);
					if(right != Constants.house) map.set(x + 1, y, Constants.grass);
					if(up != Constants.house) map.set(x, y - 1, Constants.grass);
					if(down != Constants.house) map.set(x, y + 1, Constants.grass);
				}
			}
		}
		
		
		// Build islands and grass
		Note.Log("Growing islands...");			
		growIconWithChance(map, Constants.grass, Constants.grass, Constants.house, .8f, rand);
		growIconWithChance(map, Constants.grass, Constants.grass, Constants.house, .7f, rand);
		growIconWithChance(map, Constants.grass, Constants.grass, Constants.house, .6f, rand);
		growIconWithChance(map, Constants.grass, Constants.grass, Constants.house, .5f, rand);
		growIconWithChance(map, Constants.grass, Constants.grass, Constants.house, .4f, rand);
		growIconWithChance(map, Constants.grass, Constants.grass, Constants.house, .3f, rand);
		growIconWithChance(map, Constants.grass, Constants.grass, Constants.house, .2f, rand);
		growIconWithChance(map, Constants.grass, Constants.grass, Constants.house, .1f, rand);
		growIconWithChance(map, Constants.grass, Constants.grass, Constants.house, .1f, rand);
		growIconWithChance(map, Constants.grass, Constants.grass, Constants.house, .1f, rand);
		growIconWithChance(map, Constants.grass, Constants.grass, Constants.house, .1f, rand);
		
		// Build dirt
		growIconWithChance(map, Constants.grass, Constants.dirt, Constants.house, .2f, rand);
		
		// Build sand
		borderWithChance(map, new String[] {Constants.grass, Constants.dirt }, Constants.water, Constants.sand, .9f, rand);
		
		// Build oceans
		placeIfUniformSurroundings(map, 4, Constants.water, Constants.ocean);
		
		// Build roads
		Note.Log("Adding roads..");
		for(int i = 0; i < hubs.size() - 1; ++i)
		{
			Point current = hubs.get(i).clone();
			Point target = hubs.get(i + 1);
			
			while(true)
			{
				if(current.y > target.y)
					current.y -= 1;
				else if (current.y < target.y)
					current.y += 1;
				else if (current.x < target.x)
					current.x += 1;
				else if (current.x > target.x)
					current.x -= 1;
				
				if(current.equals(target))
					break;
				
				map.set(current.x, current.y, Constants.road);
			}
		}
		
		Note.Log("Finished map generation!");
		Note.Log("----------");
	}
	
	private static void placeIfUniformSurroundings(IMapSlice map, int dist, String target, String replacement)
	{
		IMapSlice temp = map.clone();
		int height = map.getHeight();
		int width = map.getWidth();
		
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				String current = map.get(x, y);
				
				if(!current.contentEquals(target))
					continue;
				
				boolean isOcean = true;
				for(int i = 1; i < dist; ++i)
				{
					String left = map.get(x - i, y);
					String right = map.get(x + i, y);
					String up = map.get(x, y - i);
					String down = map.get(x, y + i);
					
					if(left != null) { isOcean &= left.contentEquals(target); }
					if(right != null) { isOcean &= right.contentEquals(target); }
					if(up != null) { isOcean &= up.contentEquals(target); }
					if(down != null) { isOcean &= down.contentEquals(target); }
				}
				
				if(isOcean)
				{
					temp.set(x, y, replacement);
				}
			}
		}
		
		setMapTo(map, temp);
	}
	
	private static void borderWithChance(IMapSlice map, String[] triggers, String toTarget, String toPlace, double chance, SalmonRandom rand)
	{
		IMapSlice temp = map.clone();
		int height = map.getHeight();
		int width = map.getWidth();
		
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				String current = map.get(x, y);
				
				if(!current.contentEquals(toTarget))
					continue;
				
				String left = map.get(x - 1, y);
				String right = map.get(x + 1, y);
				String up = map.get(x, y - 1);
				String down = map.get(x, y + 1);
				
				for(String target : triggers)
				{
					if(left != null && left.contentEquals(target) 
					|| right != null && right.contentEquals(target) 
					|| up != null && up.contentEquals(target)
					|| down != null && down.contentEquals(target))
					{
						if(rand.nextDouble() < chance)
						{
							temp.set(x, y, toPlace);
						}
					}
				}
			}
		}
		
		setMapTo(map, temp);
	}
	
	private static void setMapTo(IMapSlice target, IMapSlice source)
	{
		int height = target.getHeight();
		int width = target.getWidth();
		
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				target.set(x, y, source.get(x, y));
			}
		}
	}
	
	private static void growIconWithChance(IMapSlice map, String target, String growth, String exclude, double chance, SalmonRandom rand)
	{
		IMapSlice temp = map.clone();
		int height = map.getHeight();
		int width = map.getWidth();
		
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				String current = map.get(x, y);
				String left = map.get(x - 1, y);
				String right = map.get(x + 1, y);
				String up = map.get(x, y - 1);
				String down = map.get(x, y + 1);
				
				if(current == target)
				{
					if(left != null && left != exclude && rand.nextDouble() < chance)
						temp.set(x - 1, y, growth);
					
					if(right != null && right != exclude && rand.nextDouble() < chance)
						temp.set(x + 1, y, growth);
					
					if(up != null && up != exclude && rand.nextDouble() < chance)
						temp.set(x, y - 1, growth);
					
					if(down != null && down != exclude && rand.nextDouble() < chance)
						temp.set(x, y + 1, growth);
				}
			}
		}
		
		setMapTo(map, temp);
	}
	
	private static double randAdjust = 0;
	public static void generateGrassField(IMapSlice map, SalmonRandom rand)
	{
		int height = map.getHeight();
		int width = map.getWidth();
		
		String dirt = Constants.dirt;
		String grass = Constants.grass;
		
		//IMapSlice = new
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				String toAdd = null;
				
				randAdjust *= .9f;
				if(rand.nextDouble() + randAdjust > .92f)
				{
					toAdd = grass;
					if(randAdjust < 0.05f)
					{
						randAdjust = .6f;
					}
				}
				else
				{
					toAdd = dirt;
				}
				
				map.set(x, y, toAdd);
			}
		}
	}
}
