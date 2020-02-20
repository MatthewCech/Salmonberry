package core;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note
{
	private static Integer entryCount = 0;
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	// Intentionally synchronize the whole thing so we don't have write issues.
	private static void WriteOut(String toAppend)
	{
			try
			{
				FileWriter fileWriter = new FileWriter("log.txt", true);
				PrintWriter printWriter = new PrintWriter(fileWriter);
				
				synchronized(entryCount)
				{
					String dateText = formatter.format(new Date());
					printWriter.println("[" + dateText + "][" + String.format("%09d", entryCount) + "]" + toAppend); 
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
