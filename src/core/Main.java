package core;

import game.World;
import server.WebApp;

public class Main
{
	// Variables
	private static WebApp webapp;
	private static World world;
	
	
	// App entry point
	public static void main(String[] args)
	{
		// Construct components
		world = new World();
		webapp = new WebApp();
		
		// Register events and startup info
		webapp.bindPaths();
		webapp.IN_registerOnDeliverIndex(world::toString);
		webapp.OUT_registerOnEventInput(world::consumeEventInput);
		webapp.OUT_registerOnEventCreate(world::consumeEventCreate);
		
		// Run application
		while(true)
		{
			world.update();
			webapp.update();
		}
	}
}
