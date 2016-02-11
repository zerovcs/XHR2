package si.nlb.client.ui;


import jsinterop.js.File;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FileUpload;

public class MyGwtFileUpload extends FileUpload 
{
	public final File getFile()
	{
		return getFile(getElement());
	}
	
	public final native File getFile(Element element) /*-{
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