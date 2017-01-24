

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

/**
 * This is where you start the server!
 * All the endpoints on the website have been added here
 * @author ashi
 *
 */
public class ApplicationServer {
	private static int PORT = 8080;
	

	public static void main(String[] args) {
		Server server = new Server(PORT);		
		ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.addServlet(RegistrationServlet.class, "/register");
		servletContextHandler.addServlet(LoginServlet.class,"/login");
		servletContextHandler.addServlet(LoginServlet.class,"/");
		servletContextHandler.addServlet(PlaceOrder.class,"/placeOrder");
		servletContextHandler.addServlet(Confirmation.class,"/confirmation");
		servletContextHandler.addServlet(LogoutServlet.class,"/logout");
		server.setHandler(servletContextHandler);
		try {
			server.start();
			server.join();

		} catch (Exception ex) {
			System.out.println("An exception occurred while running the server. ");
			System.exit(-1);
		}
	}
}