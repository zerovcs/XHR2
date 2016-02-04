package si.nlb.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import jsinterop.JsBlob;
import jsinterop.JsObject;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.xhr.client.XMLHttpRequest.ResponseType;

public class ReportFactory
{
	private static final String CONTENT_TYPE_JSON = "text/json";
	private static  Logger logger = Logger.getLogger("ReportFactory");
	
	private ReportFactory()
	{
	}

	//TODO kt argument se bo dodala serializiran GWT-RPC String in posredoval send metodi
	public static void createReport(String command)
	{
		logger.log(Level.INFO, "Downloading ...");
		String url = "ServletBlob" + (command == null ? "" : "?command=" + command);
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setResponseType(ResponseType.Blob);
		requestBuilder.setCallback(new RequestCallback()
		{
			@Override
			public void onResponseReceived(Request request, Response response)
			{
				if(response.getStatusCode() == 200)
				{
					logger.log(Level.INFO, "Done");
					JsBlob blob = response.getResponseBlob();
//					Window.alert("Blob type: " + blob.getType() + "   size: " + blob.getSize());
					String filename = "";
					String disposition = response.getHeader("Content-Disposition");
					if (disposition != null && disposition.indexOf("attachment") != -1) 
					{
						filename = disposition.substring(disposition.indexOf("filename=") + "filename=".length());
					}
					Navigator navigator = getNavigator();
					if(navigator != null && JsObject.getPrototypeOf(navigator).hasOwnProperty("msSaveOrOpenBlob"))
					{
						navigator.msSaveOrOpenBlob(blob, filename);
					}
					else
					{
						JsObject url = createObjectURL(blob);
						open(url);
						revokeObjectURL(url);
					}
				}
				else
				{
					//verjetno bi bilo najbolje, èe bi preverjal, èe je 403 -> seja je potekla in v svoji loèeni metodi parsal kot JSON.
					//Malo lepše je potrebno error handling implementirati - za prototip je ok
					final JsBlob blob = response.getResponseBlob();
					if(blob.getSize() == 0) //some kind of exception on server that is not handled by ErrorHandler
					{
						logger.log(Level.SEVERE, "ReportFactory: Error: " + response.getStatusText());
						Window.alert("Error: " + response.getStatusText());
					}
					else
					{
						final FileReader reader = new FileReader();
						reader.setOnloadend(new EventListener()
						{
							@Override
							public void handleEvent(Event event)
							{
								if(blob.getType().indexOf(CONTENT_TYPE_JSON) != -1)
								{
									ExcObject eo = JsonUtil.parse(reader.getResult());
									if(eo != null)
									{
										logger.log(Level.SEVERE, "ReportFactory: " + eo.getExceptionName() + ": " + eo.getExceptionMessage());
										if(eo.getExceptionName().contains("SessionExpiredException")) //TODO tole ni najbolj elegantno, razmisliti kako, morda pogledati kako rešuje RPC deserializacija, ko se \\EX pošilja
										{
											Window.alert("Seja je potekla, sledi relogin ...");
										}
										else
										{
											Window.alert(eo.getExceptionName() + ": " + eo.getExceptionMessage());
										}
									}
								}
								else //èe je npr. html potrebno parsati, samo html se ne vraèa s serverja, razen, èe je kaj default pa z errorHandling nismo "ujeli"
								{
									logger.log(Level.SEVERE, "ReportFactory: Error: " + reader.getResult());
									Window.alert(reader.getResult());
								}
							}
						});
						reader.readAsText(blob);
					}
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception)
			{
				//TODO ali je potrebno tu še kaj postoriti?
				logger.log(Level.SEVERE, "ReportFactory: Error", exception);
				Window.alert(exception.getMessage());
			}
		});
		try
		{
			requestBuilder.send();
		}
		catch (RequestException e)
		{
			logger.log(Level.SEVERE, "Exception occured while creating report.", e);
			Window.alert(e.getMessage());
		}
	}
	
	@JsType(isNative=true, namespace=JsPackage.GLOBAL)
	public static class FileReader
	{
		@JsProperty public native String getResult();
		@JsProperty public native void setOnloadend(EventListener listener);
		//TODO še ostale event listener-je. Events bi moral specifircirati in vsi extends Event, ki bi verjetno moral iti v SDK GWT ali pa v vsaj svoj JAR, podobno kot bo Elemental 2.0
		public native void readAsText(JsBlob blob);
	}
	
	@JsFunction
	interface EventListener
	{
		public void handleEvent(Event event);
	}
	
	@JsType(isNative=true, namespace=JsPackage.GLOBAL)
	interface Event { }
	
	@JsType(isNative = true, name="Object", namespace=JsPackage.GLOBAL)
	public static class ExcObject extends JsObject
	{
		@JsProperty public native String getExceptionName();
		@JsProperty public native String getExceptionMessage();
		@JsProperty public native String getRequestUri();
	}
	
	@JsProperty(namespace=JsPackage.GLOBAL, name="navigator")
	public static native Navigator getNavigator();
	
	@JsType(isNative=true, namespace=JsPackage.GLOBAL)
	public static class Navigator extends JsObject
	{
		public native String msSaveOrOpenBlob(JsBlob blob, String filename);
	}
	
	@JsMethod(namespace="URL")
	static native JsObject createObjectURL(JsBlob blob);

	@JsMethod(namespace="URL")
	static native void revokeObjectURL(JsObject url);

	@JsMethod(namespace="window")
	static native void open(JsObject url);
}
