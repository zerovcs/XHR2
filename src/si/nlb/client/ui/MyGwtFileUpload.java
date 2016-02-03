package si.nlb.client.ui;


import jsinterop.JsBlob;
import jsinterop.JsFile;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FileUpload;

public class MyGwtFileUpload extends FileUpload 
{
	//TODO spremeni v File
	public final JsFile getFile()
	{
		return getFile(getElement());
	}
	
	public final native JsFile getFile(Element element) /*-{
		if(element.files.length == 0) return null;
	    return element.files[0];
	  }-*/;
	
	public void clear() 
	{
		clear(getElement());
	}
	public final native void clear(Element element) /*-{
		if(element == null) return;
	    return element.value = '';
	  }-*/;
	
	//morda dodati öe getFileList, ki vra√®a FileList[] JsType
}