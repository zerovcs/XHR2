package si.nlb.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class ErrorHandling
 */
@WebServlet("/ErrorHandling")
public class ErrorHandling extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE_HTML = "text/html";
	private static final String CONTENT_TYPE_JSON = "text/json;charset=UTF-8";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ErrorHandling() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		//always JSON
        processErrorJSON(request, response);
	}
	
	 private void processErrorHTML(HttpServletRequest request, HttpServletResponse response) throws IOException 
	 {
		 //za test - realno naj se vedno vraèa json, da se ga enostavno parsa
	        // Analyze the servlet exception
	        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
	        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
	        String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
	        if (servletName == null) {
	            servletName = "Unknown";
	        }
	        String requestUri = (String) request
	                .getAttribute("javax.servlet.error.request_uri");
	        if (requestUri == null) {
	            requestUri = "Unknown";
	        }
	         
	        // Set response content type
	          response.setContentType("text/html");
	      
	          PrintWriter out = response.getWriter();
	          out.write("<html><head><title>Exception/Error Details</title></head><body>");
	          if(statusCode != 500){
	              out.write("<h3>Error Details</h3>");
	              out.write("<strong>Status Code</strong>:"+statusCode+"<br>");
	              out.write("<strong>Requested URI</strong>:"+requestUri);
	          }else{
	              out.write("<h3>Exception Details</h3>");
	              out.write("<ul><li>Servlet Name:"+servletName+"</li>");
	              out.write("<li>Exception Name:"+throwable.getClass().getName()+"</li>");
	              out.write("<li>Requested URI:"+requestUri+"</li>");
	              out.write("<li>Exception Message:"+throwable.getMessage()+"</li>");
	              out.write("</ul>");
	          }
	           
	          out.write("<br><br>");
	          out.write("<a href=\"index.html\">Home Page</a>");
	          out.write("</body></html>");
	    }

	 
	 private void processErrorJSON(HttpServletRequest request, HttpServletResponse response) throws IOException 
	 {
		JsonException jsonException = new JsonException();
		// Analyze the servlet exception
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
		String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
		if (requestUri == null)
			requestUri = "Unknown";


		if (statusCode == 403) //sessionExpired - morda lahko v servletu oz filtru (preveriti, èe se ta servlet sploh proži takrat) throw SessioExpired exception in se ga obravnava kot vsak drug exception. Na klientu pa loèujemo glede na exception name. Problem vidim v tem, da se takrat 500 status code vrne, kar simbolizira server exception, ne pa kontroliran exception. Bolje 403 zaenkrat
		{
			jsonException.exceptionName = "com.example.SessionExpiredException";
			jsonException.requestUri = requestUri;
			jsonException.exceptionMessage = errorMessage;
		}
		else if (statusCode != 500)
		{
			jsonException.exceptionName = "/";
			jsonException.requestUri = requestUri;
			jsonException.exceptionMessage = errorMessage;
		}
		else
		{
			if(throwable.getCause() != null) throwable = throwable.getCause();
			jsonException.exceptionName = throwable.getClass().getName();
			jsonException.requestUri = requestUri;
			jsonException.exceptionMessage = throwable.getMessage();
		}
		// Set response content type
		response.setContentType(CONTENT_TYPE_JSON);
		ObjectMapper objectMapper = new ObjectMapper();
		response.getWriter().write(objectMapper.writeValueAsString(jsonException));
	}

	 private static class JsonException
	 {
		 private String exceptionName;
		 private String exceptionMessage;
		 private String requestUri;
		public String getExceptionName()
		{
			return exceptionName;
		}
		public void setExceptionName(String exceptionName)
		{
			this.exceptionName = exceptionName;
		}
		public String getExceptionMessage()
		{
			return exceptionMessage;
		}
		public void setExceptionMessage(String exceptionMessage)
		{
			this.exceptionMessage = exceptionMessage;
		}
		public String getRequestUri()
		{
			return requestUri;
		}
		public void setRequestUri(String requestUri)
		{
			this.requestUri = requestUri;
		}
		 
	 }
}
