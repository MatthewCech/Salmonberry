package server;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import core.Note;
import fi.iki.elonen.NanoHTTPD;
import game.events.Event;
import game.events.EventCreate;
import game.events.EventInput;
import jdk.nashorn.internal.runtime.JSONFunctions;

public class WebApp extends NanoHTTPD
{
	// Variables
	public static final int port = 80;
	private Map<String, String> pages;
	private DirectoryMonitor monitor;
	private Map<String, Function<IHTTPSession, Response>> paths;
	
	// Event related
	private Supplier<String> onDeliverIndex;     // Input
	private List<Consumer<Event>> onEventInput;  // Output
	private List<Consumer<Event>> onEventCreate; // Output
	
	// Constructor
	public WebApp()
	{
		super(port);
	
		this.onDeliverIndex = null;
		this.paths = new HashMap<String, Function<IHTTPSession, Response>>();
		this.onEventInput = new ArrayList<Consumer<Event>>();
		this.onEventCreate = new ArrayList<Consumer<Event>>();
		
		try
		{
			start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		}
		catch(IOException e)
		{
			Note.Error("Failed to start server!\n" + e);
			return;
		}
		
		Note.Log("Running at http://localhost:" + port + "/\n");
		
		// Start up file manager
		this.monitor = new DirectoryMonitor(Paths.get(DirectoryMonitor.defaultDir));
		this.pages = monitor.getFileContents();
	}
	
	
	  /////////////////////
	 // Input Functions //
	/////////////////////
	
	// All called and concatinated when homepage is out
	public void IN_registerOnDeliverIndex(Supplier<String> callback)
	{
		onDeliverIndex = callback;
	}
	
	// All called and concatinated when homepage is out
	public void OUT_registerOnEventInput(Consumer<Event> callback)
	{
		onEventInput.add(callback);
	}
	
	// All called and concatinated when homepage is out
	public void OUT_registerOnEventCreate(Consumer<Event> callback)
	{
		onEventCreate.add(callback);
	}
	
	
	  //////////////////////////////////
	 // General Management Functions //
	//////////////////////////////////
	
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
		String uri = session.getUri();
		Note.Log("URI Request: " + uri);
		
		if(paths.containsKey(uri))
		{
			return paths.get(uri).apply(session);
		}
		else
		{
			return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", "{}");
		}
	}
	
	// Hook up the various endpoints of the server so it can direct messages. 
	// Note that paths are very literal - there's no leeway with the slashes,
	// so even if /path is bound to, /path/ is not automatically bound to as well. 
	public void bindPaths()
	{
		paths.put("/", this::path_);
		paths.put("/create", this::path_create);
	}
	
	// In the event of failure, standardize the failure message.
	private Response newFailResponse(String message)
	{
		String res = "{ status:\"fail\", message:" + JSONFunctions.quote(message) + "}"; 
		return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", res); 
	}
	
	
	  ///////////////////
	 // Path Handling //
	///////////////////
	
	// at a path of "/create"
	private Response path_create(IHTTPSession session)
	{
		Map<String, List<String>> params = session.getParameters();
		if(params.get("id") != null)
		{
			String res = "{ status: " + JSONFunctions.quote("ok") + "}";
			String id = params.get("id").get(0);
			
			for(Consumer<Event> consumer : onEventCreate)
			{
				consumer.accept(new EventCreate(id));
			}
			
			return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", res);
		}
		else
		{
			return newFailResponse("'id' should be specified as a request parameter.");
		}
	}
	
	// at a path of "/"
	private Response path_(IHTTPSession session)
	{
		String defautPage = "index.html";
	
		// Form output after handling all inputs and things
		String out = "";
		if(onDeliverIndex != null)
		{
			out += onDeliverIndex.get();
		}
				
		// Get input parameter for now just for testing
		Map<String, List<String>> params = session.getParameters();
		if(params.get("input") != null)
		{
			for(Consumer<Event> consumer : onEventInput)
			{
				String input = params.get("input").get(0);
				String id = params.get("id").get(0);
				String icon = "" + id.toUpperCase().charAt(0);
				
				Note.Log("Input found, was: '" + input + "'");
				consumer.accept(new EventInput(id, icon, input));
			}
		}
		
		// Return homepage
		return newFixedLengthResponse(pages.get(defautPage).replace("{{content}}", out));
	}
}
