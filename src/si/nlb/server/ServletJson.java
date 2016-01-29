package si.nlb.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class ServletJson
 */
@WebServlet("/ServletJson") //ne dela v embedded jetty, ki je del SDM. Èe imamo web.xml potem tudi tole v glassfish 3.1 ne dela, oèitno kombinacija configa in deklerativnsoti ne dela.
public class ServletJson extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletJson() {
        super();
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
		System.out.println("doPost in ServletJson invoked");
		Test test = new Test();
		test.x = "X value";
		test.y = 1200;
		ObjectMapper om = new ObjectMapper();
//		om.writeValueAsString(test);
		response.getWriter().write(om.writeValueAsString(test));
	}

	
	static class Test
	{
		private String x;
		private int y;
		public String getX()
		{
			return x;
		}
		public void setX(String x)
		{
			this.x = x;
		}
		public int getY()
		{
			return y;
		}
		public void setY(int y)
		{
			this.y = y;
		}
		
	}
}
