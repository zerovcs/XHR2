package si.nlb.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.js.Blob;
import jsinterop.js.JsObject;
import si.nlb.client.ResponseHandler.JfwRequestCallback;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.xhr.client.XMLHttpRequest.ResponseType;

public class ReportHandler
{
	private static final String CONTENT_TYPE_JSON = "text/json";
	private static  Logger logger = Logger.getLogger("ReportFactory");
	
	private ReportHandler()
	{
	}

	//TODO kt argument se bo dodala serializiran GWT-RPC String in posredoval send metodi
	public static void createReport(String command)
	{
		logger.log(Level.INFO, "Downloading ...");
		String url = "ServletBlob" + (command == null ? "" : "?command=" + command);
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setResponseType(ResponseType.Blob);
		ResponseHandler.createRequestCallback(requestBuilder, new JfwRequestCallback()
		{
			@Override
			public void onResponse(Request request, Response response)
			{
				logger.log(Level.INFO, "Done");
				Blob blob = response.getResponseBlob();
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
	
	
	@JsProperty(namespace=JsPackage.GLOBAL, name="navigator")
	public static native Navigator getNavigator();
	
	@JsType(isNative=true, namespace=JsPackage.GLOBAL)
	public static class Navigator extends JsObject
	{
		public native String msSaveOrOpenBlob(Blob blob, String filename);
	}
	
	@JsMethod(namespace="URL")
	static native JsObject createObjectURL(Blob blob);

	@JsMethod(namespace="URL")
	static native void revokeObjectURL(JsObject url);

	@JsMethod(namespace="window")
	static native void open(JsObject url);
}
