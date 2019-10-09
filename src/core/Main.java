package core;

import game.World;
import server.WebApp;

public class Main
{
	// Variables
	private static WebApp salmonberry;
	private static World framework;
	
	
	// App entry point
	public static void main(String[] args)
	{
		// Construct components
		framework = new World();
		salmonberry = new WebApp();
		
		// Register events and startup info
		salmonberry.IN_registerOnDeliverIndex(framework::toString);
		salmonberry.OUT_registerOnMessage(framework::inputStub);
		
		// Run application
		while(true)
		{
			salmonberry.update();
			framework.update();
		}
	}
}
