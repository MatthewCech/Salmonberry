package core;

import core.server.WebApp;

public class Main
{
	static WebApp salmonberry;
	
	public static void main(String[] args)
	{
		salmonberry = new WebApp();
		
		while(true)
		{
			salmonberry.update();
		}
	}
}
