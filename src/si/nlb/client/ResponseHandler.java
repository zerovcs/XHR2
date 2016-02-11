package si.nlb.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import jsinterop.Utils;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.event.Event;
import jsinterop.event.EventListener;
import jsinterop.js.Blob;
import jsinterop.js.FileReader;
import jsinterop.js.JsObject;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.xhr.client.XMLHttpRequest.ResponseType;

public class ResponseHandler 
{
	private static  Logger logger = Logger.getLogger("ResponseHandler");
	private static final String CONTENT_TYPE_JSON = "text/json";
	
	protected static RequestCallback createRequestCallback(final RequestBuilder requestBuilder, final JfwRequestCallback requestCallback)
	{
		RequestCallback callback = null;
		requestBuilder.setCallback(callback = new com.google.gwt.http.client.RequestCallback() 
		{
			@Override
			public void onResponseReceived(final Request request, final Response response)
			{
				if(response.getStatusCode() == 200)
				{
					requestCallback.onResponse(request, response);
				}
				else
				{
					//error has to be in JSON format - it only depends if it is wrapped as blob or not. Blob appears when downloading, otherwise pure json
					if(requestBuilder.getResponseType() == ResponseType.Blob)
					{
						//verjetno bi bilo najbolje, èe bi preverjal, èe je 403 -> seja je potekla in v svoji loèeni metodi parsal kot JSON.
						//Malo lepše je potrebno error handling implementirati - za prototip je ok
						final Blob blob = response.getResponseBlob();
						final FileReader reader = new FileReader();
						reader.setOnloadend(new EventListener()
						{
							@Override
							public void handleEvent(Event event)
							{
								if(blob.getType().indexOf(CONTENT_TYPE_JSON) != -1)
								{
									ExcObject eo = Utils.parseJson(reader.getResult());
									if(eo != null)
									{
										logger.log(Level.SEVERE, "ResponseHandler: " + eo.getExceptionName() + ": " + eo.getExceptionMessage());
										if(eo.getExceptionName().contains("SessionExpiredException")) //TODO tole ni najbolj elegantno, razmisliti kako, morda pogledati kako rešuje RPC deserializacija, ko se \\EX pošilja
										{
											Window.alert("Seja je potekla, sledi relogin ...");
										}
										else
										{
											requestCallback.onError(request, response);
											Window.alert(eo.getExceptionName() + ": " + eo.getExceptionMessage());
										}
									}
									else
									{
										logger.log(Level.SEVERE, "ResponseHandler: Error: " + reader.getResult());
										Window.alert(reader.getResult());
										requestCallback.onError(request, response);
									}
								}
								else //èe je npr. html potrebno parsati, samo html se ne vraèa s serverja, razen, èe je kaj default pa z errorHandling nismo "ujeli" -to se ne bi smelo nikoli prožiti
								{
									logger.log(Level.SEVERE, "ResponseHandler: Error: " + reader.getResult());
									Window.alert(reader.getResult());
									requestCallback.onError(request, response);
								}
							}
						});
						reader.readAsText(blob);
					}
					else //json
					{
						ExcObject eo = Utils.parseJson(response.getText());
						if(eo != null) 
						{
							logger.log(Level.SEVERE, "ResponseHandler: " + eo.getExceptionName() + ": " + eo.getExceptionMessage());
							if(eo.getExceptionName().contains("SessionExpiredException")) //TODO tole ni najbolj elegantno, razmisliti kako, morda pogledati kako rešuje RPC deserializacija, ko se \\EX pošilja
							{
								Window.alert("Seja je potekla, sledi relogin ...");
							}
							else
							{
								requestCallback.onError(request, response);
								Window.alert(eo.getExceptionName() + ": " + eo.getExceptionMessage());
							}
						}
						else //not even json
						{
							logger.log(Level.SEVERE, "ResponseHandler: Error: " + response.getText());
							Window.alert(response.getText());
							requestCallback.onError(request, response);
						}
					}
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception)
			{
				//TODO ali je potrebno tu še kaj postoriti?
				logger.log(Level.SEVERE, "ResponseHandler: Error", exception);
				Window.alert(exception.getMessage());
				requestCallback.onError(request, null);
			}
		});		
		return callback;
	}
	
	public static abstract class JfwRequestCallback
	{
		public abstract void onResponse(Request request, Response response);
		/**
		 * 
		 * @param request
		 * @param response could be null.
		 */
		public void onError(Request request, Response response)
		{
			
		}
	}
	
	@JsType(isNative = true, name="Object", namespace=JsPackage.GLOBAL)
	public static class ExcObject extends JsObject
	{
		@JsProperty public native String getExceptionName();
		@JsProperty public native String getExceptionMessage();
		@JsProperty public native String getRequestUri();
	}
	
}
