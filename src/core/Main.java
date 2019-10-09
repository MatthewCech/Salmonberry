package core;

import core.server.WebApp;

public class Main
{
	static WebApp salmonberry;
	
	public static void main(String[] args)
	{
		try
		{
			salmonberry = new WebApp();
		}
		catch (Exception e)
		{
			Note.Error("Server level exception - Terminating server\n" + e);
		}
	}
}
