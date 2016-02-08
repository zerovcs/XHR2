package si.nlb.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class UploadFormDataServlet
 */
@WebServlet("/UploadFormDataServlet")
@MultipartConfig
public class UploadFormDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int BUFFER_SIZE = 4096;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadFormDataServlet() {
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
		System.out.println("Do post inside UploadFormDataServlet invoked ...");
		Part filePart = request.getPart("file");
		String fileName = getFileName(filePart);
		if(fileName == null) fileName = request.getParameter("file-name");
		if(fileName == null) throw new IOException("No file name ...");
	    InputStream in = filePart.getInputStream();
	    byte[] buffer = new byte[BUFFER_SIZE];
	    FileOutputStream out = new FileOutputStream("E:\\eclipse\\workspace\\" + fileName);
//	    FileOutputStream out = new FileOutputStream("D:\\development\\eclipse_4.3.2\\workspace\\html5-upload\\" + fileName);
	    
	    try
	    {
			while (true) 
			{
				int byteCount = in.read(buffer);
				if (byteCount == -1) {
					break;
				}
				out.write(buffer, 0, byteCount);
//				try {
//					Thread.sleep(10); //simulacija velikega file  - tu ne pride do izraza, oèitno pri multipart formi se na klientu prenese še predno vse prebere in part input stream.
//					// drugaèe je pri raw body podatkih 
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				};
				
			}
		}
	    finally 
	    {
			if (in != null) {
				in.close();
			}
			if(out != null)
				out.close();
		}
		response.getWriter().write("Hello from UploadFormDataServlet");
	}

	
	public static String getFileName(Part filePart)
	{
	    String header = filePart.getHeader("content-disposition");
	    if(header == null)
	        return null;
	    for(String headerPart : header.split(";"))
	    {
	        if(headerPart.trim().startsWith("filename"))
	        {
	            return headerPart.substring(headerPart.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}

}
