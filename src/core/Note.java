package core;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Note
{
	private static Integer entryCount = 0;
	
	// Intentionally synchronize the whole thing so we don't have write issues.
	private static void WriteOut(String toAppend)
	{
			try
			{
				FileWriter fileWriter = new FileWriter("log.txt", true);
				PrintWriter printWriter = new PrintWriter(fileWriter);
				
				synchronized(entryCount)
				{
					printWriter.println("[" + String.format("%09d", entryCount) + "]" + toAppend); 
					entryCount += 1;
					printWriter.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	}
	
	private static void Print(String str)
	{
		System.out.println(str);
		WriteOut(str);
	}
	
	public static void Log(Object toWrite)
	{
		String msg = "[Log] " + toWrite.toString();
		Print(msg);
	}
	
	public static void Warn(Object toWrite)
	{
		Print("[Warning] " + toWrite.toString());
	}
	
	public static void Error(Object toWrite)
	{
		String asStr = toWrite.toString();
		Print("[ERROR] " + asStr);
		System.err.println("[ERROR] " + asStr);
	}
}
