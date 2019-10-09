package core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD
{
	public App() throws IOException
	{
		super(8080);
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		System.out.println("\nRunning at http://localhost:8080/\n");
	}
	
	@Override
	public Response serve(IHTTPSession session)
	{
		Map<String, List<String>> params = session.getParameters();
		
		if(params.isEmpty())
		{
			return newFixedLengthResponse(
				"<!DOCTYPE html>\n" + 
				"<html>\n" + 
				"	<head>\n" + 
				"		<title>Salmonberry</title>\n" + 
				"	</head>\n" + 
				"	<body>\n" + 
				"		<h2>Salmonberry</h2>" + 
				"	</body>\n" + 
				"</html>"
			);
		}
		
		return newFixedLengthResponse("");
	}
}
