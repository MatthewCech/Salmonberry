package core;

import server.WebApp;

public class Main
{
	private static WebApp salmonberry;
	
	public static void main(String[] args)
	{
		salmonberry = new WebApp();
		
		while(true)
		{
			salmonberry.update();
		}
	}
}
