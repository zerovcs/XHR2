package si.nlb.client;

import static si.nlb.client.jquery.JQueryUtil.$;

import java.util.logging.Level;
import java.util.logging.Logger;

import jsinterop.JsFile;
import jsinterop.JsFormData;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.event.ProgressEvent;
import si.nlb.client.ResponseHandler.JfwRequestCallback;
import si.nlb.client.jquery.JQuery;
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
//		progressBar = (ProgressBar)DOM.getElementById("progressBar");
//		let's try jquery
		JQuery jQuery = $("#progressBar");
		if(jQuery.getLength() != 0) // to be sure that exists
		{
			progressBar = (ProgressBar)jQuery.get(0);
		}
//		final Element span = DOM.getElementById("progresslabel");
		//try JQuery
		final Element span = (Element) $("#progresslabel").get(0);
		span.addClassName(resources.css().progresslabel());
		Scheduler.get().scheduleDeferred(new ScheduledCommand() 
		{
			@Override
			public void execute() 
			{
//				progressBar = (ProgressBar)$("#progressBar");
				span.getStyle().setLeft(-span.getOffsetWidth()/2-progressBar.getOffsetWidth()/2, Unit.PX);
				span.getStyle().setBottom(progressBar.getOffsetHeight()/2-span.getOffsetHeight()/2, Unit.PX);
				logger.log(Level.INFO, "span width: " + (-span.getOffsetWidth()/2));
				logger.log(Level.INFO, "bar width: " + (-progressBar.getOffsetWidth()/2));
				logger.log(Level.INFO, "span height: " + (span.getOffsetHeight()/2));
				logger.log(Level.INFO, "bar height: " + (progressBar.getOffsetHeight()/2));
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
							if(progressEvent.getTotal() != progressEvent.getLoaded())
							{
								span.setInnerHTML((int)(progressEvent.getLoaded()/progressEvent.getTotal() * 100) + "%");
							}
							else
							{
								span.setInnerHTML("V obdelavi");
							}
							span.getStyle().setLeft(-span.getOffsetWidth()/2-progressBar.getOffsetWidth()/2, Unit.PX);
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
					RequestCallback callback = ResponseHandler.createRequestCallback(builder, new JfwRequestCallback() 
					{
						@Override
						public void onResponse(Request request, Response response) 
						{
							Window.alert(response.getText());
							span.setInnerHTML("Kon\u010dano");
							span.getStyle().setLeft(-span.getOffsetWidth()/2-progressBar.getOffsetWidth()/2, Unit.PX);							
							
						}
						@Override
						public void onError(Request request, Response response) 
						{
							span.setInnerHTML("Napaka");
							span.getStyle().setLeft(-span.getOffsetWidth()/2-progressBar.getOffsetWidth()/2, Unit.PX);							
						}
					});
					JsFormData formData = new JsFormData();
//					formData.append("file-name", blob.getName()); //èe pri spodnjem dodamo getName, potem moramo na serverju prebrati filename from part - glej TestFormDataUpload
					formData.append("file", file, file.getName());
					builder.sendFormData(formData, callback);
					
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
	public interface ProgressBar extends JsElement{
		@JsProperty public void setValue(double value);
	    @JsProperty public void setMax(double max);
	    @JsProperty public double getOffsetWidth();
	    @JsProperty public double getOffsetHeight();
	}
	
//	@JsType(isNative = true, namespace=JsPackage.GLOBAL)
//	public interface NodeList {
//
//	    /**
//	     * Returns the number of nodes in the NodeList.
//	     *
//	     * @return The number of nodes in the NodeList.
//	     */
//	    @JsProperty
//	    int getLength();
//
//	    /**
//	     * Returns an item in the list by its index, or null if the index is out-of-bounds; can be used as an
//	     * alternative to simply accessing nodeList[index] (which instead returns undefined when index is
//	     * out-of-bounds).
//	     *
//	     * @param index The index to get the the item for
//	     * @return The item for the given index or null
//	     */
//	    <T extends Node> T item(int index);
//	}
	
	@JsType(isNative = true, name="Element", namespace=JsPackage.GLOBAL)
	public interface JsElement extends Node {

	    /**
	     * Getter for innerHTML attribute
	     *
	     * @return The markup of the element's content.
	     */
	    @JsProperty
	    public String getInnerHTML();

	    /**
	     * Setter for innerHTML attribute
	     *
	     * @param s The markup of the element's content.
	     */
	    @JsProperty
	    public void setInnerHTML(String s);


	    /**
	     * Adds a new attribute or changes the value of an existing attribute on the specified element.
	     *
	     * @param name The name of the attribute as a string.
	     * @param value The desired new value of the attribute.
	     */
	    public void setAttribute(String name, Object value);

	    /**
	     * Returns the value of a specified attribute on the element. If the given attribute does not exist, the
	     * value returned will either be null or ""
	     *
	     * @param name The name of the attribute whose value you want to get.
	     * @return The value of a specified attribute on the element or null
	     */
	    public String getAttribute(String name);

	    /**
	     * Method returns whether the current element has the specified attribute
	     *
	     * @param name A string representing the name of the attribute.
	     * @return Indicates whether the current element has the specified attribute
	     */
	    public boolean hasAttribute(String name);

	    /**
	     * Removes an attribute from the specified element.
	     *
	     * @param name The name of the attribute to be removed from element.
	     */
	    public void removeAttribute(String name);

	}	
	
	@JsType(isNative = true, namespace=JsPackage.GLOBAL)
	public interface Node {

	    /**
	     * Getter for childNodes attribute
	     *
	     * @return Returns a live NodeList containing all the children of this node. NodeList being live means
	     *         that if the children of the Node change, the NodeList object is automatically updated.
	     */
//	    @JsProperty
//	    NodeList getChildNodes();

	    /**
	     * Getter for the firstChild attribute
	     *
	     * @return Returns a Node representing the first direct child node of the node, or null if the node has no
	     *         child.
	     */
	    @JsProperty
	    Node getFirstChild();

	    /**
	     * Getter for the lastChild attribute
	     *
	     * @return Returns a Node representing the last direct child node of the node, or null if the node has no
	     *         child.
	     */
	    @JsProperty
	    Node getLastChild();

	    /**
	     * Getter for the parentNode attribute
	     *
	     * @return Returns a Node that is the parent of this node. If there is no such node, like if this node is
	     *         the top of the tree or if doesn't participate in a tree, this property returns null.
	     */
	    @JsProperty
	    Node getParentNode();

	    /**
	     * Getter for the parentElement attribute
	     *
	     * @return Returns an Element that is the parent of this node. If the node has no parent, or if that
	     *         parent is not an Element, this property returns null.
	     */
	    @JsProperty
	    Element getParentElement();

	    /**
	     * Getter for textContent property.<br>
	     * <b>Differences to innerText:</b><br>
	     * Internet Explorer introduced element.innerText. The intention is similar but with the following
	     * differences:<br>
	     * While textContent gets the content of all elements, including &lt;script&gt; and &lt;style&gt;
	     * elements, the IE-specific property innerText does not.<br>
	     * innerText is aware of style and will not return the text of hidden elements, whereas textContent will.
	     * <br>
	     * As innerText is aware of CSS styling, it will trigger a reflow, whereas textContent will not.<br>
	     * Unlike textContent, altering innerText in Internet Explorer (up to version 11 inclusive) not just
	     * removes child nodes from the element, but also permanently destroys all descendant text nodes (so it is
	     * impossible to insert the nodes again into any other element or into the same element anymore).
	     *
	     * @return The textContent property represents the text content of a node and its descendants. It returns
	     *         null if the element is a document, a document type, or a notation.
	     * @see #getInnerText()
	     */
	    @JsProperty
	    String getTextContent();

	    /**
	     * @param s The textContent property represents the text content of a node and its descendants.
	     */
	    @JsProperty
	    void setTextContent(String textContent);

	    /**
	     * Getter for innerText property.<br>
	     * <p>
	     * It is a nonstandard property that represents the text content of a node and its descendants. As a
	     * getter, it approximates the text the user would get if they highlighted the contents of the element
	     * with the cursor and then copied to the clipboard.
	     * </p>
	     *
	     * @return Approximation of the text the user would get if they highlighted the contents of the element
	     *         with the cursor and then copied to the clipboard.
	     * @see #getTextContent()
	     */
	    @JsProperty
	    String getInnerText();

	    /**
	     * Setter for innerText property.<br>
	     * It is a nonstandard property that represents the text content of a node and its descendants.
	     *
	     * @param innerText The innerText value to set
	     */
	    @JsProperty
	    void setInnerText(String innerText);

	    /**
	     * Returns whether the element has any child nodes
	     *
	     * @return Indicates if the element has any child nodes, or not.
	     */
	    boolean hasChildNodes();

	    /**
	     * Insert a Node as the last child node of this element.
	     *
	     * @param child The child to be inserted
	     */
	    void appendChild(Object child);

	    /**
	     * Removes a child node from the current element, which must be a child of the current node.
	     *
	     * @param child The child to remove
	     */
	    void removeChild(Object child);

	    /**
	     * Replaces one child Node of the current one with the second one given in parameter.
	     *
	     * @param newChild The new node to replace oldChild. If it already exists in the DOM, it is first removed.
	     * @param oldChild The existing child to be replaced.
	     * @return The replaced node. This is the same node as oldChild
	     */
	    Node replaceChild(Node newChild, Node oldChild);

	    /**
	     * Inserts the node newChild before the existing child node refChild. If refChild is null, insert newChild
	     * at the end of the list of children.
	     * If newChild is a DocumentFragment object, all of its children are inserted, in the same order, before
	     * refChild. If the newChild is already in the tree, it is first removed.
	     *
	     * @param newChild The node to insert.
	     * @param refChild The reference node, i.e., the node before which the new node must be inserted.
	     * @return The node being inserted
	     */
	    Node insertBefore(Node newChild, Node refChild);

	}	
}
