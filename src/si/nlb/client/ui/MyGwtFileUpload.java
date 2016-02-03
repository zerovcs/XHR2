package si.nlb.client.ui;


import jsinterop.JsBlob;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FileUpload;

public class MyGwtFileUpload extends FileUpload 
{
	//TODO spremeni v File
	public final JsBlob getBlob()
	{
		return getBlob(getElement());
	}
	
	public final native JsBlob getBlob(Element element) /*-{
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
	
	//morda dodati še getFileList, ki vraèa FileList[] JsType
}