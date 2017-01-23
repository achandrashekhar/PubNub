

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

//import org.apache.commons.lang3.StringEscapeUtils;

/** 
 * A servlet that handles user Login. doGet() method displays an HTML form with a button and
 * two textfields: one for the username, one for the password.
 * doPost() processes the form: if the username is not taken, it adds user info to the database.
 *
 */
@SuppressWarnings("serial")
public class LoginServlet extends BaseServlet {
	
	// DatabaseHandler interacts with the MySQL database
	private static final HandleDB dbhandler = HandleDB.getInstance();


	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession();
		String uname = (String) session.getAttribute("username");
		if(uname!=null){
			String url = "/placeOrder";
			url = response.encodeRedirectURL(url);
			response.sendRedirect(url);
		}
		prepareResponse("Logging in ", response,request);

		PrintWriter out = response.getWriter();
		
		// error will not be null if we were forwarded her from the post method where something went wrong
		String error = request.getParameter("error");
		if(error != null) {
			String errorMessage = getStatusMessage(error);
			out.println("<p style=\"color: red;\">" + errorMessage + "</p>");
		}
		
		 /*  first, get and initialize an engine  */
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        /*  next, get the Template  */
        Template t = ve.getTemplate( "HTML_PAGES/LoginForm2.html" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
  
        t.merge( context, out );

		//displayForm(out); 
		finishResponse(response,request);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Register New User", response, request);

		// Get data from the textfields of the html form
		String newuser = request.getParameter("user");
		String newpass = request.getParameter("pass");
		// sanitize user input to avoid XSS attacks:
//		newuser = StringEscapeUtils.escapeHtml4(newuser);
//		newpass = StringEscapeUtils.escapeHtml4(newpass);
//		
		// add user's info to the database 
		Status status = dbhandler.loginUser(newuser, newpass);
		
		if(status == Status.OK) { // registration was successful
			response.getWriter().println("Login is Successful.");
			HttpSession session = request.getSession();
			session.setAttribute("username", newuser);
		
			String url = "/placeOrder";
			url = response.encodeRedirectURL(url);
			response.sendRedirect(url); // send a get request  (redirect to the same path)
			//response.encodeRedirectURL("/hotels");
			
		}
		else { // if something went wrong
			String url = "/login?error=" + status.name();
			url = response.encodeRedirectURL(url);
			response.sendRedirect(url); // send a get request  (redirect to the same path)
		}
	}

	
}