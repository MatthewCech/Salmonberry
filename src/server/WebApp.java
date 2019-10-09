package server;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import core.Note;
import fi.iki.elonen.NanoHTTPD;

public class WebApp extends NanoHTTPD
{
	// Variables
	private Map<String, String> pages;
	private DirectoryMonitor monitor;
	
	// Constructor
	public WebApp()
	{
		super(8080);
	
		try
		{
			start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		}
		catch(IOException e)
		{
			Note.Error("Failed to start server!\n" + e);
			return;
		}
		
		Note.Log("Running at http://localhost:8080/\n");
		
		// Start up file manager
		monitor = new DirectoryMonitor(Paths.get(DirectoryMonitor.defaultDir));
		pages = monitor.getFileContents();
		
	}
	
	// The primary application loop for general functions. Returns true
	// if it's operating normally, false if not.
	public boolean update()
	{
		try
		{
			if(monitor.update())
			{
				pages = monitor.getFileContents();
				pages.forEach((key, value) -> { Note.Log("File '" + key + "' found, has " + value.length() + " chars in it."); });
			}
		}
		catch(Exception e)
		{
			Note.Error("Error during webapp updating!\n" + e);
			return false;
		}
		
		return true;
	}
	
	@Override
	// This function is called when an HTTP request is recieved!
	public Response serve(IHTTPSession session)
	{
		Map<String, List<String>> params = session.getParameters();
		String defautPage = "index.html";
		
		if(params.isEmpty() && pages.containsKey(defautPage))
		{
			return newFixedLengthResponse(pages.get(defautPage));
		}
		
		return newFixedLengthResponse("{}");
	}
}
