package si.nlb.client.jquery;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;

public class JQueryUtil
{

	private JQueryUtil() {}

    @JsMethod(namespace = JsPackage.GLOBAL, name = "$")
    public static native JQuery $(String selector);

    //TODO morda še kakšno $(...) metodo, èe bo potreba
}
