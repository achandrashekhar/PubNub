

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
 * A servlet that renders the order confirmation page
 */
@SuppressWarnings("serial")
public class Confirmation extends BaseServlet {
	
	// DatabaseHandler interacts with the MySQL database
	private static final HandleDB dbhandler = HandleDB.getInstance();
	private static final PubNub pubnub = PubNubInstance.getInstance();
	private static final String ORDER_CHANNEL = "OrderChannel";

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