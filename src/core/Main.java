package core;
import java.io.IOException;

public class Main
{
	static App salmonberry;
	public static void main(String[] args)
	{
		try
		{
			salmonberry = new App();
		}
		catch (IOException e)
		{
			Note.Error("Couldn't start server:\n" + e);
		}
	}
}
