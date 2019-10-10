package core;

import game.World;
import server.WebApp;

public class Main
{
	// Variables
	private static WebApp salmonberry;
	private static World world;
	
	
	// App entry point
	public static void main(String[] args)
	{
		// Construct components
		world = new World();
		salmonberry = new WebApp();
		
		// Register events and startup info
		salmonberry.IN_registerOnDeliverIndex(world::toString);
		salmonberry.OUT_registerOnMessage(world::inputStub);
		
		// Run application
		while(true)
		{
			world.update();
			salmonberry.update();
		}
	}
}
