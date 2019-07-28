package ru.denis.dota2.servlets;
import java.io.IOException;
    import java.io.PrintWriter;
     
    import javax.servlet.ServletException;
    import javax.servlet.http.HttpServlet;
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;


public class RefreshServlet extends HttpServlet {
	private static final long serialVersionUID = -906206073392994077L;
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
	}
    	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    	processRequest(request, response);
    }
    	
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
    		processRequest(request, response);
	}
    	
}