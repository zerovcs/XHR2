package si.nlb.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import jsinterop.JsFile;
import jsinterop.JsFormData;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.event.ProgressEvent;
import si.nlb.client.resources.AppResources;
import si.nlb.client.ui.MyGwtFileUpload;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xhr.client.XMLHttpRequestUpload.ProgressEventListener;

public class UploadGwt 
{
	private String fixedText = "Selected file: ";
	Label label = new Label(fixedText);
	private ProgressBar progressBar;
	private static Logger logger = Logger.getLogger("UploadGwt");
	
	public void init(AppResources resources)
	{
		RootPanel rootPanel = RootPanel.get();
		Label title = new Label("Upload using GWT file uploader");
		title.addStyleName(resources.css().labelItalic());
		rootPanel.add(title);
		final MyGwtFileUpload fileUpload = new MyGwtFileUpload();
		fileUpload.getElement().getStyle().setDisplay(Display.NONE);
		rootPanel.add(fileUpload);
		Button browser = new Button("Browse...");
		rootPanel.add(browser);
		rootPanel.add(label);
		HTMLPanel div = new HTMLPanel("<progress id='progressBar' max='100' value='0'></progress><span id='progresslabel'>0%</span>");
		rootPanel.add(div);
		progressBar = (ProgressBar)DOM.getElementById("progressBar");		
		final Element span = DOM.getElementById("progresslabel");
		span.addClassName(resources.css().progresslabel());
		logger.log(Level.INFO, "span: " + (-span.getOffsetWidth()/2));
		logger.log(Level.INFO, "bar: " + (-progressBar.getOffsetWidth()/2));
		Scheduler.get().scheduleDeferred(new ScheduledCommand() 
		{
			@Override
			public void execute() 
			{
				span.getStyle().setLeft(-span.getOffsetWidth()/2-progressBar.getOffsetWidth()/2, Unit.PX);
			}
		});
		
		browser.addClickHandler(new ClickHandler() 
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				label.setText(fixedText);
				fileUpload.clear();
				progressBar.setValue(0);
				span.setInnerHTML("0%");
				span.getStyle().setLeft(-span.getOffsetWidth()/2-progressBar.getOffsetWidth()/2, Unit.PX);
				fileUpload.click();
			}
		});
		
		fileUpload.addChangeHandler(new ChangeHandler() 
		{
			@Override
			public void onChange(ChangeEvent event) 
			{
				if(fileUpload.getFile() != null)
				{
					label.setText(fixedText + fileUpload.getFile().getName());
				}
				else
				{
					label.setText(fixedText);
				}
			}
		});
		
		
		Button uploadBtn = new Button();
		rootPanel.add(uploadBtn);
		uploadBtn.setText("Upload");
		uploadBtn.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
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
							progressBar.setMax(progressEvent.getTotal());
							progressBar.setValue(progressEvent.getLoaded());
							logger.log(Level.INFO, "Total: " + progressEvent.getTotal() + "   Loaded: " + progressEvent.getLoaded());
							span.setInnerHTML(progressEvent.getLoaded()/progressEvent.getTotal() * 100 + "%");
							span.getStyle().setLeft(-span.getOffsetWidth()/2-progressBar.getOffsetWidth()/2, Unit.PX);
							//TODO check when upload finished to set "working" until response is returned
						}
					}
				});
				try 
				{
					JsFile file = fileUpload.getFile();
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
							//TODO handle response like in ReportFactory
							//TODO set 
							Window.alert(response.getText());

						}
						
						@Override
						public void onError(Request request, Throwable exception) 
						{
							//TODO handle like in ReportFactory
							Window.alert(exception.getMessage());
						}

					};
					JsFormData formData = new JsFormData();
//					formData.append("file-name", blob.getName()); //èe pri spodnjem dodamo getName, potem moramo na serverju prebrati filename from part - glej TestFormDataUpload
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
	
	@JsType(isNative=true, namespace=JsPackage.GLOBAL, name="Progress")
	public interface ProgressBar {
		@JsProperty public void setValue(double value);
	    @JsProperty public void setMax(double max);
	    @JsProperty public double getOffsetWidth();
	}
}
