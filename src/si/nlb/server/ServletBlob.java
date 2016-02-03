package si.nlb.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ServletBlob
 */
@WebServlet("/ServletBlob")
public class ServletBlob extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletBlob() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try
		{
			String command = request.getParameter("command");
			System.out.println("DoPost in ServletBlob invoked ..." + command);
			if(command != null && command.equals("error"))
			{
//				Since browser understand only HTML, when our application throw exception, servlet container processes the exception and generate a HTML response. 
//				This logic is specific to servlet container, Glassfish logira v consolo na serverju ter vrne 200, vse ok.
//				Servlet API provides support for custom Exception and Error Handler servlets that we can configure in deployment descriptor, the whole purpose of these servlets 
//				are to handle the Exception or Error raised by application and send HTML response that is useful for the user.
//				Tole ne dela v Glassfish3 - moram bi redirect na servlet definiran v web.xml, pa ne gre. 404 error pa se proži
				throw new NullPointerException("Exception occurred...");
//				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden...");
//				return;
			}
			response.setContentType("application/pdf");
			//potrebno še tole dodati, da se lahko na klientu parsa filename in upošteva pri prikazu.
			response.addHeader("Content-Disposition", "attachment; filename=book.pdf");
//			response.setContentLength(baos.size());
			if(command != null && command.equals("timeout"))
			{
				System.out.println("Going to sleep for a while");
				Thread.sleep(5000);
			}
			Files.copy(Paths.get("D:/knjige/Java-Performance.pdf"), response.getOutputStream());
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			throw new ServletException("Exception occurred...", e);
		}
	}

}
