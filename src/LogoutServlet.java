

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** 
 * Logout Servlet, will log the user out
 *
 */
@SuppressWarnings("serial")
public class LogoutServlet extends BaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		HttpSession session = request.getSession();
		session.invalidate();
		
		
		String url = "/login";
		url = response.encodeRedirectURL(url);
		response.sendRedirect(url); 
	}
}