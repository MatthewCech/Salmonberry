package core;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD
{
	// Variables
	private Map<String, String> pages;
	private DirectoryMonitor monitor;
	
	// Constructor
	public App() throws IOException
	{
		super(8080);
		
		// Start up server
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		
		Note.Log("Running at http://localhost:8080/\n");
		
		// Start up file manager
		monitor = new DirectoryMonitor(Paths.get(DirectoryMonitor.defaultDir));
		pages = monitor.getFileContents();
		
		// Start up main program loop
		run();
	}
	
	// The primary application loop for general functions
	public void run()
	{
		while(true)
		{
			if(monitor.update())
			{
				pages = monitor.getFileContents();
				pages.forEach((key, value) -> { Note.Log("File '" + key + "' found, has " + value.length() + " chars in it."); });
			}
		}
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
