package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import core.Note;
import fi.iki.elonen.NanoHTTPD;
import game.api.IEvent;
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
	private boolean isOnDevMachine;
	
	// Don't speak of this
	private Supplier<Object> superSecretLinkToForceCallRemoteUpdateFunction; 
	
	// Event related
	private Supplier<String> onEndpointStateEnvironment; // Input
	private Supplier<String> onEndpointStateEntities;    // Input
	private List<Consumer<IEvent>> onEventInput;  // Output
	private List<Consumer<IEvent>> onEventCreate; // Output
	
	// Constructor
	public WebApp()
	{
		super(port);
	
		this.paths = new HashMap<String, Function<IHTTPSession, Response>>();
		this.onEventInput = new ArrayList<Consumer<IEvent>>();
		this.onEventCreate = new ArrayList<Consumer<IEvent>>();
		
		// This setup is using eclipse 
		this.isOnDevMachine = new File("./.project").exists() || new File("./.classpath").exists();
		
		try
		{
			start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		}
		catch(IOException e)
		{
			Note.Error("Failed to start server!\n" + e);
			return;
		}
		
		// Note which format we're running in
		Note.Log("Running at http://127.0.0.1:" + port + "/\n\t" + (isOnDevMachine ? "(dev mode)" : "(release mode)") + "\n");
		
		// Start up file manager
		this.monitor = new DirectoryMonitor(Paths.get(DirectoryMonitor.defaultDir));
		this.pages = monitor.getFileContents();
	}
	
	
	  ///////////////////////
	 // Message Functions //
	///////////////////////
	
	// Binds an event that allows something to recieve a callback only once when updated.
	public void BIND_theUpdateFunction(Supplier<Object> onUpdateOnce)
	{
		this.superSecretLinkToForceCallRemoteUpdateFunction = onUpdateOnce;
	}
	
	// All called and concatinated when homepage is out
	public void IN_registerOnEndpointStateEnvironment(Supplier<String> callback)
	{
		onEndpointStateEnvironment = callback;
	}
	
	// All called and concatinated when homepage is out
	public void IN_registerOnEndpointStateEntities(Supplier<String> callback)
	{
		onEndpointStateEntities = callback;
	}
	
	// All called and concatinated when homepage is out
	public void OUT_registerOnEventInput(Consumer<IEvent> callback)
	{
		onEventInput.add(callback);
	}
	
	// All called and concatinated when homepage is out
	public void OUT_registerOnEventCreate(Consumer<IEvent> callback)
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
		
		if(paths.containsKey(uri))
		{
			return paths.get(uri).apply(session);
		}
		else
		{
			Note.Warn(session.getRemoteIpAddress() + " requested nonexistent URI: " + uri);
			return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "404 Not Found");
		}
	}
	
	// Hook up the various endpoints of the server so it can direct messages. 
	// Note that paths are very literal - there's no leeway with the slashes,
	// so even if /path is bound to, /path/ is not automatically bound to as well. 
	public void bindPaths()
	{
		paths.put("/", this::path_);
		paths.put("/create", this::path_create);
		paths.put("/state", this::path_state);
		paths.put("/input", this::path_input);
	}
	
	// In the event of failure, standardize the failure message.
	private Response newFailResponse(String message)
	{
		String res = "{ \"status\":\"fail\", \"data\":" + JSONFunctions.quote(message) + "}"; 
		return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", res); 
	}
	
	// In the event of failure, standardize the failure message.
	private Response newSuccessResponse()
	{
		return newSuccessResponse("");
	}
	private Response newSuccessResponse(String data)
	{
		String res = "{ \"status\":\"ok\", \"data\":" + JSONFunctions.quote(data) + "}"; 
		return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", res); 
	}
	
	  ///////////////////
	 // Path Handling //
	///////////////////

	// at a path of "/"
	private Response path_(IHTTPSession session)
	{
		String defautPage = "index.html";
		
		String content = "";
		if(isOnDevMachine)
		{
			content = pages.get(defautPage);
		}
		else
		{
			try
			{
				content = new String(WebApp.class.getResourceAsStream("/resources/index.html").readAllBytes());
			}
			catch (IOException e)
			{
				Note.Error("Failed to load content from resources: " + e.toString());
			}
		}
		
		if(isOnDevMachine)
		{
			content = content.replace("{{TARGET_ADDR}}", "127.0.0.1");
		}
		else
		{
			content = content.replace("{{TARGET_ADDR}}", "salmonberry.info");
		}
		
		// Return homepage
		return newFixedLengthResponse(content);
	}
	
	// at a path of "/create"
	private Response path_create(IHTTPSession session)
	{
		Map<String, List<String>> params = session.getParameters();
		if(params.get("id") != null)
		{
			String id = params.get("id").get(0);
			
			for(Consumer<IEvent> consumer : onEventCreate)
			{
				consumer.accept(new EventCreate(id));
			}
			
			return newSuccessResponse();
		}
		else
		{
			return newFailResponse("'id' should be specified as a request parameter.");
		}
	}
	
	// at a path of "/state"
	private Response path_state(IHTTPSession session)
	{
		Map<String, List<String>> params = session.getParameters();
		if(params.get("layer") != null && params.get("id") != null)
		{
			// Nothing is done with the id param at this point. It could be eventually, tho.
			String layer = params.get("layer").get(0);
			String id = params.get("id").get(0);
			
			if(layer.equalsIgnoreCase("environment"))
			{
				return newSuccessResponse(onEndpointStateEnvironment.get());
			}
			else if (layer.equals("entities"))
			{
				return newSuccessResponse(onEndpointStateEntities.get());
			}
			else
			{
				return newFailResponse("The correct parameters were provided, but no layer of type '" + layer + "' exists!");
			}
			
		}
		
		// Nothing is done with the id param at this point
		return newFailResponse("The parameters 'layer' and 'id' must be specified!");
	}
	
	// at a path of "/input"
	private Response path_input(IHTTPSession session)
	{
		// Searches for and requires an input to be sent. 
		Map<String, List<String>> params = session.getParameters();
		if(params.get("input") != null && params.get("id") != null)
		{
			for(Consumer<IEvent> consumer : onEventInput)
			{
				String input = params.get("input").get(0);
				String id = params.get("id").get(0);
				
				consumer.accept(new EventInput(id, input));
			}
		}
		else
		{
			return newFailResponse("Both the 'id' and 'input' parameters are required.");
		}
		
		// For now, force-update. This absolutely will be problematic in the future.
		superSecretLinkToForceCallRemoteUpdateFunction.get();
		
		// Return world
		return newSuccessResponse();
	}
}
