package core;

import game.impl.World;
import server.WebApp;

public class Main
{
	// Variables
	private static WebApp webapp;
	private static World world;
	
	// App entry point
	public static void main(String[] args)
	{
		FullApp(args);
		//ExperimentalApp(args);
	}
	
	public static void FullApp(String[] args)
	{
		// Construct components
		world = new World();
		webapp = new WebApp();
		
		// Register events and startup info for webapp
		webapp.bindPaths();
		webapp.IN_registerOnEndpointStateEnvironment(world::getEnvironmentAsASCII);
		webapp.IN_registerOnEndpointStateEntities(world::getEntitiesAsASCII);
		webapp.OUT_registerOnEventInput(world::consumeEventInput);
		webapp.OUT_registerOnEventCreate(world::consumeEventCreate);
		webapp.BIND_theUpdateFunction(world::update);
		
		// Run application
		while(true)
		{
			world.update();
			webapp.update();
			
			sleep(1);
		}
	}
	
	private static void sleep(long milliseconds)
	{
		try
		{
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException e)
		{
			Note.Error("Issue during scheduled sleep! " + e);
		}
	}
	
	public static void ExperimentalApp(String[] args)
	{
		new experiments.EWorldbuilder();
	}
}
