

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

//import org.apache.commons.lang3.StringEscapeUtils;

/** 
 * A servlet that handles user registration. doGet() method displays an HTML form with a button and
 * two textfields: one for the username, one for the password.
 * doPost() processes the form: if the username is not taken, it adds user info to the database.
 *
 */
@SuppressWarnings("serial")
public class RegistrationServlet extends BaseServlet {
	
	// DatabaseHandler interacts with the MySQL database
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();


	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		prepareResponse("Register New User", response,request);

		PrintWriter out = response.getWriter();
		
		// error will not be null if we were forwarded her from the post method where something went wrong
		String error = request.getParameter("error");
		if(error != null) {
			String errorMessage = getStatusMessage(error);
			out.println("<p style=\"color: red;\">" + errorMessage + "</p>");
		}

	//	displayForm(out); 
		
		/*  first, get and initialize an engine  */
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        /*  next, get the Template  */
        Template t = ve.getTemplate( "HTML_PAGES/registration.html" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
  
        t.merge( context, out );
		finishResponse(response,request);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Register New User", response,request);

		// Get data from the textfields of the html form
		String newuser = request.getParameter("user");
		String newpass = request.getParameter("pass");
		// sanitize user input to avoid XSS attacks:
//		newuser = StringEscapeUtils.escapeHtml4(newuser);
//		newpass = StringEscapeUtils.escapeHtml4(newpass);
		
		// add user's info to the database 
		  String USERNAME_PATTERN ="^[A-Z][a-z0-9].*[*#$%^&@!]$";
		  Pattern pattern = Pattern.compile(USERNAME_PATTERN);
		  Matcher matcher = pattern.matcher(newpass);
		  if(!matcher.find())
		  {
			 
			  String url = "/registrationError";
				url = response.encodeRedirectURL(url);
				response.sendRedirect(url);
			  
		  }
		Status status = dbhandler.registerUser(newuser, newpass);

		if(status == Status.OK) { // registration was successful
			response.getWriter().println("Registered! Database updated.");
		}
		else { // if something went wrong
			String url = "/register?error=" + status.name();
			url = response.encodeRedirectURL(url);
			response.sendRedirect(url); // send a get request  (redirect to the same path)
		}
	}

	/** Writes and HTML form that shows two textfields and a button to the PrintWriter */
	private void displayForm(PrintWriter out) {
		assert out != null;

		out.println("<form action=\"/register\" method=\"post\">"); // the form will be processed by POST
		out.println("<table border=\"0\">");
		out.println("\t<tr>");
		out.println("\t\t<td>Usename:</td>");
		out.println("\t\t<td><input type=\"text\" name=\"user\" size=\"30\"></td>");
		out.println("\t</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Password:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"pass\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<p><input type=\"submit\" value=\"Register\"></p>");
		out.println("</form>");
	}
}