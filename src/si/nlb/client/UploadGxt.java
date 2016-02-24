package si.nlb.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import jsinterop.event.ProgressEvent;
import jsinterop.js.File;
import jsinterop.js.FormData;
import si.nlb.client.ResponseHandler.JfwRequestCallback;
import si.nlb.client.resources.AppResources;
import si.nlb.client.ui.MyGxtFileUpload;

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
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class UploadGxt
{
	private static Logger logger = Logger.getLogger("UploadGxt");
	
	public void init(AppResources resources)
	{
		RootPanel rootPanel = RootPanel.get();
		Label title = new Label("Upload using GXT file uploader");
		title.addStyleName(resources.css().labelItalic());
		rootPanel.add(title);
		SimpleContainer container = new SimpleContainer();
		container.setWidth(300);
		final MyGxtFileUpload gxtFileUpload = new MyGxtFileUpload();
		container.add(gxtFileUpload);
		rootPanel.add(container);

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
					public void handleEvent(Object event) 
					{
						ProgressEvent progressEvent = (ProgressEvent)event;
//						GWT.debugger();
						if (progressEvent.isLengthComputable()) 
						{
							logger.log(Level.INFO, "Total: " + progressEvent.getTotal() + "   Loaded: " + progressEvent.getLoaded() + "   " + progressEvent.getLoaded()/progressEvent.getTotal()*100);
							if(progressEvent.getTotal() != progressEvent.getLoaded())
							{
								gxtProgressBar.updateProgress(progressEvent.getLoaded()/progressEvent.getTotal(), "{0}%");
							}
							else
							{
								gxtProgressBar.updateProgress(100, "V obdelavi");
							}
						}
					}
				});
//				builder.setHeader("Content-Type", "multipart/form-data; charset=utf-8");
				try 
				{
					File file = gxtFileUpload.getFile();
					if(file == null) 
					{
						Window.alert("Select file first");
						return;
					}
					
					RequestCallback requestCallback = ResponseHandler.createRequestCallback(builder, new JfwRequestCallback()
					{
						@Override
						public void onResponse(Request request, Response response)
						{
							gxtProgressBar.updateProgress(101, "Kon\u010dano");
							Window.alert(response.getText());
						}
						@Override
						public void onError(Request request, Response response)
						{
							gxtProgressBar.updateProgress(101, "Napaka");
						}
					});
//					builder.send(blob, requestCallback);
					FormData formData = new FormData();
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
