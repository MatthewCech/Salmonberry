package core;

public class Main
{
	static App salmonberry;
	
	public static void main(String[] args)
	{
		try
		{
			salmonberry = new App();
		}
		catch (Exception e)
		{
			Note.Error("Server level exception - Terminating server\n" + e);
		}
	}
}
