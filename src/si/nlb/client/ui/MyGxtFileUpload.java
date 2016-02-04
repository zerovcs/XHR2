package si.nlb.client.ui;

import com.google.gwt.dom.client.InputElement;
import com.sencha.gxt.widget.core.client.form.FileUploadField;

import jsinterop.JsFile;

public class MyGxtFileUpload extends FileUploadField
{
	public final JsFile getFile()
	{
		return getFile(super.getFileInput());
	}

	public final native JsFile getFile(InputElement element) /*-{
		if(element.files.length == 0) return null;
		return element.files[0];
	}-*/;
}
