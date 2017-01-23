

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;


public class ApplicationServer {
	private static int PORT = 8080;
	

	public static void main(String[] args) {
		
		
		
		Server server = new Server(PORT);

		ServletHandler handler = new ServletHandler();
		//server.setHandler(handler);

//		handler.addServletWithMapping(RegistrationServlet.class, "/register");
//		handler.addServletWithMapping(LoginServlet.class,"/login");
//		handler.addServletWithMapping(DisplayHotelsServlet.class,"/hotels");
//		handler.addServletWithMapping(ReviewServlet.class,"/reviews");
		// other servlets can be added as needed such as LoginServlet etc.
		
		ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.addServlet(RegistrationServlet.class, "/register");
		servletContextHandler.addServlet(LoginServlet.class,"/login");
		servletContextHandler.addServlet(LoginServlet.class,"/");
		servletContextHandler.addServlet(PlaceOrder.class,"/placeOrder");
		servletContextHandler.addServlet(Confirmation.class,"/confirmation");
//		servletContextHandler.addServlet(LogoutServlet.class,"/logout");
//		servletContextHandler.addServlet(HotelInfo.class,"/hotelInfo");
//		servletContextHandler.addServlet(RegisterErrorServlet.class,"/registrationError");
//		servletContextHandler.addServlet(SerchByCityStateCountryServlet.class,"/searchByCityStateCountry");
//		servletContextHandler.addServlet(SavedHotelsServlet.class,"/savedhotels");
//		servletContextHandler.addServlet(DisplaySavedHotelsServlet.class,"/showsavedhotels");
//		servletContextHandler.addServlet(touristAttractionListServlet.class,"/attractions");
//		servletContextHandler.addServlet(SortReviewsBy.class,"/sortreviewsby");
//		servletContextHandler.addServlet(ClearSavedHotelsAndLinks.class,"/clearhotellist");
//		servletContextHandler.addServlet(SaveVisitedExpediaLinks.class,"/savehotellink");
//		servletContextHandler.addServlet(DisplaySavedHotelsFromNAvBar.class,"/savedhotelsfromnavbar");
//		
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