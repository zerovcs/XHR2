package si.nlb.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import jsinterop.JsFile;
import jsinterop.JsFormData;
import jsinterop.event.ProgressEvent;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xhr.client.XMLHttpRequestUpload.ProgressEventListener;
import com.sencha.gxt.widget.core.client.ProgressBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import si.nlb.client.resources.AppResources;
import si.nlb.client.ui.MyGxtFileUpload;

public class UploadGxt
{
	private static Logger logger = Logger.getLogger("UploadGxt");
	
	public void init(AppResources resources)
	{
		RootPanel rootPanel = RootPanel.get();
		Label title = new Label("Upload using GXT file uploader");
		title.addStyleName(resources.css().labelItalic());
		rootPanel.add(title);
		final MyGxtFileUpload gxtFileUpload = new MyGxtFileUpload();
		gxtFileUpload.setWidth(200);
		rootPanel.add(gxtFileUpload);

		final ProgressBar gxtProgressBar = new ProgressBar();
		rootPanel.add(gxtProgressBar);
		TextButton gxtButton = new TextButton("Upload");
		rootPanel.add(gxtButton);
		gxtFileUpload.addChangeHandler(new ChangeHandler() 
		{
			@Override
			public void onChange(ChangeEvent event) 
			{
				gxtProgressBar.reset();
			}
		});
		gxtButton.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "UploadFormDataServlet");
				builder.setProgressEventListener(new ProgressEventListener() 
				{
					@Override
					public void handleEvent(ProgressEvent progressEvent) 
					{
//						GWT.debugger();
						if (progressEvent.isLengthComputable()) 
						{
							gxtProgressBar.updateProgress(progressEvent.getLoaded()/progressEvent.getTotal(), "{0}%");
							logger.log(Level.INFO, "Total: " + progressEvent.getTotal() + "   Loaded: " + progressEvent.getLoaded() + "   " + progressEvent.getLoaded()/progressEvent.getTotal()*100);
						}
					}
				});
//				builder.setHeader("Content-Type", "multipart/form-data; charset=utf-8");
				try 
				{
					JsFile file = gxtFileUpload.getFile();
					if(file == null) 
					{
						Window.alert("Select file first");
						return;
					}
					RequestCallback requestCallback = new RequestCallback()
					{
						@Override
						public void onResponseReceived(Request request, Response response) 
						{
							//TODO handle like in ReportFactory
							Window.alert(response.getText());
						}
						
						@Override
						public void onError(Request request, Throwable exception) 
						{
							//TODO handle like in ReportFactory
							Window.alert(exception.getMessage());
						}
					};
//					builder.send(blob, requestCallback);
					JsFormData formData = new JsFormData();
//					formData.append("file-name", blob.getName()); //èe pri spodnjem dodamo getName, potem moramo mna serverju prebrati filename from parf - glej TestFormDataUpload
					formData.append("file", file, file.getName());
					builder.sendFormData(formData, requestCallback);
					
				}
				catch (RequestException e) 
				{
					//TODO handle like in ReportFactory
					Window.alert(e.getMessage());
				}
			}
		});
	}

}
