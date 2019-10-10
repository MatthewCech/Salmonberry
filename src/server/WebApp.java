package server;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import core.Note;
import fi.iki.elonen.NanoHTTPD;
import game.events.Event;
import game.events.EventInput;

public class WebApp extends NanoHTTPD
{
	// Variables
	private Map<String, String> pages;
	private DirectoryMonitor monitor;
	private List<Supplier<String>> onDeliverIndex;
	private List<Consumer<Event>> onMessage;
	
	// Constructor
	public WebApp()
	{
		super(8080);
	
		this.onDeliverIndex = new ArrayList<Supplier<String>>();
		this.onMessage = new ArrayList<Consumer<Event>>();
		
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
		this.monitor = new DirectoryMonitor(Paths.get(DirectoryMonitor.defaultDir));
		this.pages = monitor.getFileContents();
	}
	
	// INPUT FUNC
	// All called and concatinated when homepage is out
	public void IN_registerOnDeliverIndex(Supplier<String> callback)
	{
		onDeliverIndex.add(callback);
	}
	
	// All called and concatinated when homepage is out
	public void OUT_registerOnMessage(Consumer<Event> callback)
	{
		onMessage.add(callback);
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
		
		// Get input parameter for now just for testing
		if(params.get("input") != null)
		{
			for(Consumer<Event> consumer : onMessage)
			{
				String input = params.get("input").get(0);
				String id = params.get("id").get(0);
				String icon = "" + id.toUpperCase().charAt(0);
				
				Note.Log("Input found, was: '" + input + "'");
				consumer.accept(new EventInput(id, icon, input));
			}
		}
		
		// Form output after handling all inputs and things
		String out = "";
		for(Supplier<String> supplier : onDeliverIndex)
		{
			out += supplier.get();
		}
		
		return newFixedLengthResponse(pages.get(defautPage).replace("{{content}}", out));
		
		
		//return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", "{}");
	}
}
