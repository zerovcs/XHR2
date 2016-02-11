package si.nlb.client.ui;

import jsinterop.js.File;

import com.google.gwt.dom.client.InputElement;
import com.sencha.gxt.widget.core.client.form.FileUploadField;

public class MyGxtFileUpload extends FileUploadField
{
	public final File getFile()
	{
		return getFile(super.getFileInput());
	}

	public final native File getFile(InputElement element) /*-{
		if(element.files.length == 0) return null;
		return element.files[0];
	}-*/;

	public void setWidth(int i)
	{
		// TODO Auto-generated method stub
		
	}
}
