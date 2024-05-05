package core;

public class Utils
{
	// Comply with https://www.json.org/json-en.html.
	// This can be sped up with table or switch that uses a string builder.
	public static String BasicEscapeJSON(String input)
	{
		String toEscape = input;

		toEscape = toEscape.replace("\\", "\\\\");
		toEscape = toEscape.replace("\"", "\\\"");
		
		toEscape = toEscape.replace("\n", "\\n");
		toEscape = toEscape.replace("\r", "\\r");
		toEscape = toEscape.replace("\t", "\\t");
		
		toEscape = toEscape.replace("\f", "\\f");
		toEscape = toEscape.replace("\b", "\\b");
		
		// Buffer
		toEscape = "\"" + toEscape + "\"";
		
		return toEscape;
	}
}
