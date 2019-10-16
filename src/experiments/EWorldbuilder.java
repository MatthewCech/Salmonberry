package experiments;

import java.util.ArrayList;
import java.util.List;

import game.api.IMapSlice;
import game.data.Definitions;
import game.data.Point;
import game.data.SalmonRandom;
import game.impl.MapSlice;

public class EWorldbuilder
{
	public IMapSlice slice;
	
	public EWorldbuilder()
	{
		slice = new MapSlice(Definitions.defaultWidth, Definitions.defaultHeight);
		generateLumpyIsland(slice, new SalmonRandom());
		System.out.println(slice.toString());
	}

	public static void generateLumpyIsland(IMapSlice map, SalmonRandom rand)
	{
		// Acquire info
		int height = map.getHeight();
		int width = map.getWidth();
		String water = Definitions.water;
		
		// Fill with defaults
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				String toAdd = water;
				map.set(x, y, toAdd);
			}
		}
		
		// Get hubs
		List<Point> hubs = new ArrayList<Point>();
		hubs.add(new Point(1, 1));
		hubs.add(Point.random(width, height, rand));
		hubs.add(Point.random(width, height, rand));
		@SuppressWarnings("unused") Point side = Point.random(width, height, rand); // Omitted secondary city
		hubs.add(Point.random(width, height, rand));
		
		for(Point loc : hubs)
		{
			map.set(loc.x, loc.y, Definitions.house);
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
				
				if(current == Definitions.house)
				{
					if(left != Definitions.house) map.set(x - 1, y, Definitions.grass);
					if(right != Definitions.house) map.set(x + 1, y, Definitions.grass);
					if(up != Definitions.house) map.set(x, y - 1, Definitions.grass);
					if(down != Definitions.house) map.set(x, y + 1, Definitions.grass);
				}
			}
		}
		
		growIconWithChance(map, Definitions.grass, Definitions.grass, Definitions.house, .8f, rand);
		growIconWithChance(map, Definitions.grass, Definitions.grass, Definitions.house, .7f, rand);
		growIconWithChance(map, Definitions.grass, Definitions.grass, Definitions.house, .6f, rand);
		growIconWithChance(map, Definitions.grass, Definitions.grass, Definitions.house, .5f, rand);
		growIconWithChance(map, Definitions.grass, Definitions.grass, Definitions.house, .4f, rand);
		growIconWithChance(map, Definitions.grass, Definitions.grass, Definitions.house, .3f, rand);
		growIconWithChance(map, Definitions.grass, Definitions.grass, Definitions.house, .2f, rand);
		growIconWithChance(map, Definitions.grass, Definitions.grass, Definitions.house, .1f, rand);
		growIconWithChance(map, Definitions.grass, Definitions.grass, Definitions.house, .1f, rand);
		growIconWithChance(map, Definitions.grass, Definitions.grass, Definitions.house, .1f, rand);
		growIconWithChance(map, Definitions.grass, Definitions.grass, Definitions.house, .1f, rand);
		
		growIconWithChance(map, Definitions.grass, Definitions.dirt, Definitions.house, .2f, rand);
		
		borderWithChance(map, new String[] {Definitions.grass, Definitions.dirt }, Definitions.water, Definitions.sand, .9f, rand);
		
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
				
				map.set(current.x, current.y, Definitions.road);
			}
		}
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
				
				if(!current.contains(toTarget))
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
		
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				map.set(x, y, temp.get(x, y));
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
					if(left != exclude && rand.nextDouble() < chance)
						temp.set(x - 1, y, growth);
					
					if(right != exclude && rand.nextDouble() < chance)
						temp.set(x + 1, y, growth);
					
					if(up != exclude && rand.nextDouble() < chance)
						temp.set(x, y - 1, growth);
					
					if(down != exclude && rand.nextDouble() < chance)
						temp.set(x, y + 1, growth);
				}
			}
		}
		
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				map.set(x, y, temp.get(x, y));
			}
		}
	}
	
	private static double randAdjust = 0;
	public static void generateGrassField(IMapSlice map, SalmonRandom rand)
	{
		int height = map.getHeight();
		int width = map.getWidth();
		
		String dirt = Definitions.dirt;
		String grass = Definitions.grass;
		
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
