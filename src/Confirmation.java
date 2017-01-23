

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
import org.json.simple.JSONObject;

import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

//import org.apache.commons.lang3.StringEscapeUtils;

/** 
 * A servlet that handles orders. doGet() method displays an HTML form with a button and
 * textfileds: number of iPhones
 * doPost() processes the form: it publishes to the Channel and updates the database
 *
 */
@SuppressWarnings("serial")
public class Confirmation extends BaseServlet {
	

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession();
		String uname = (String) session.getAttribute("username");
		
		prepareResponse("Order Confirmation ", response,request);

		PrintWriter out = response.getWriter();
		
		 /*  first, get and initialize an engine  */
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        /*  next, get the Template  */
        Template t = ve.getTemplate( "HTML_PAGES/OrderConfirmation.html" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
  
        t.merge( context, out ); 
		finishResponse(response,request);
	}

	

	
}