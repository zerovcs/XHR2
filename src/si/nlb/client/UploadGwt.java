package si.nlb.client;

import si.nlb.client.resources.AppResources;
import si.nlb.client.ui.MyGwtFileUpload;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class UploadGwt 
{
	private String fixedText = "Selected file: ";
	Label label = new Label(fixedText);

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
		browser.addClickHandler(new ClickHandler() 
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				label.setText(fixedText);
				fileUpload.clear();
//				progressBar.setValue(0);
				fileUpload.click();
			}
		});
		
		rootPanel.add(label);
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
		
/*		
		clickMeButton = new Button();
		rootPanel.add(clickMeButton);
		clickMeButton.setText("Upload");
		clickMeButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event) 
			{
//				TestService.Util.getInstance().test(new AsyncCallback<String>() 
//				{
//					@Override
//					public void onSuccess(String result) {
//						Window.alert(result);
//					}
//					
//					@Override
//					public void onFailure(Throwable caught) {
//						Window.alert(caught.getMessage());
//					}
//				});
				
//				MyRequestBuilder builder = new MyRequestBuilder(RequestBuilder.POST, "UploadServlet");
				MyRequestBuilder builder = new MyRequestBuilder(RequestBuilder.POST, "UploadFormDataServlet");
				builder.setProgressEventListener(new ProgressEventListener() 
				{
					@Override
					public void handleEvent(ProgressEvent progressEvent) 
					{
//						GWT.debugger();
						if (progressEvent.getLengthComputable()) 
						{
							progressBar.setMax(progressEvent.getTotal());
							progressBar.setValue(progressEvent.getLoaded());
							GWT.log("Total: " + progressEvent.getTotal() + "   Loaded: " + progressEvent.getLoaded());
						}
					}
				});
//				builder.setHeader("Content-Type", "multipart/form-data; charset=utf-8");
				try 
				{
					Blob blob = fileUpload.getBlob();
					if(blob == null) 
					{
						Window.alert("Select file first");
						return;
					}
					RequestCallback requestCallback = new RequestCallback()
					{
						@Override
						public void onResponseReceived(Request request, Response response) {
							Window.alert(response.getText());

						}
						
						@Override
						public void onError(Request request, Throwable exception) 
						{
							Window.alert(exception.getMessage());
						}
					};
//					builder.send(blob, requestCallback);
					FormData formData = new FormData();
//					formData.append("file-name", blob.getName()); //èe pri spodnjem dodamo getName, potem moramo mna serverju prebrati filename from parf - glej TestFormDataUpload
					formData.append("file", blob, blob.getName());
					builder.send(formData, requestCallback);
					
				}
				catch (RequestException e) 
				{
					Window.alert(e.getMessage());
				}
			}
		});
		*/	
	}
}
