package si.nlb.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import jsinterop.JsObject;
import jsinterop.annotations.JsMethod;

public class JsonUtil 
{
	private static Logger logger = Logger.getLogger("JsonUtil");
	
	//Namenoma ta metoda in ne native kot public, da lahko še kar vkomponiramo npr. exception handling
	public static <T extends JsObject> T parseJson(String json)
	{
		try
		{
			logger.log(Level.INFO, "Parsing JSON");
			return parse(json);
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, "JsonUtil: Exception while parsin JSON: ", e);
		}
		return null;
	}
	
	@JsMethod(namespace="JSON")
	static native <T extends JsObject> T parse(String json);
	
	private JsonUtil() {
	}
}