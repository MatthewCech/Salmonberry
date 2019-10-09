package util;

public class Note
{
	public static void Log(Object toWrite)
	{
		System.out.println("[Log] " + toWrite.toString());
	}
	
	public static void Warn(Object toWrite)
	{
		System.out.println("[Warning] " + toWrite.toString());
	}
	
	public static void Error(Object toWrite)
	{
		String asStr = toWrite.toString();
		System.out.println("[ERROR] " + asStr);
		System.err.println("[ERROR] " + asStr);
	}
}
