package si.nlb.client.jquery;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.html.Node;

@JsType(isNative=true, namespace=JsPackage.GLOBAL)
public interface JQuery // extends NodeList
{
    @JsProperty public int getLength();
    
    public Node get(int index);
    
    /**
     * Adds the specified class(es) to each element in the set of matched elements.
     *
     * @param style One or more space-separated classes to be added to the class attribute of each matched
     *            element.
     * @return jQuery element
     */
    public JQuery addClass(String style);
    
    public void click(JsFunction fn);
    
    //TODO add all other methods. See:
    //https://github.com/dpreindl/gwt-jsinterop-library/tree/master/jsinterop-jquery/src/main/java/jsinterop/jQuery
    //https://github.com/workingflows/gwt-jquery/blob/master/src/main/java/com/workingflows/js/jquery/client/api/JQueryElement.java
}
