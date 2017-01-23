

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
public class PlaceOrder extends BaseServlet {
	
	// DatabaseHandler interacts with the MySQL database
	private static final HandleDB dbhandler = HandleDB.getInstance();
	private static final PubNub pubnub = PubNubInstance.getInstance();
	private static final String ORDER_CHANNEL = "OrderChannel";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession();
		String uname = (String) session.getAttribute("username");
		
		prepareResponse("Prepare order ", response,request);

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
        Template t = ve.getTemplate( "HTML_PAGES/PlaceOrderForm.html" );
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
		String newuser = request.getParameter("email");
		String custName = request.getParameter("custName");
		String address = request.getParameter("address");
		int selectedItem = 0;
		if(request.getParameter("exampleSelect1")!=null)
		{
		   selectedItem=Integer.parseInt(request.getParameter("exampleSelect1"));
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("email", newuser);
		jsonObject.put("quantity", selectedItem);
		jsonObject.put("custName", custName);
		jsonObject.put("address", address);
		//Publish the form information to the channel ORDER_CHANNEL
		pubnub.publish().channel(ORDER_CHANNEL).message(jsonObject)
		    .shouldStore(true)
		    .usePOST(true)
		    .async(new PNCallback<PNPublishResult>() {
				@Override
				public void onResponse(PNPublishResult result, PNStatus status) {
					// TODO Auto-generated method stub
					if (status.isError()) {
		                // something bad happened.
		                System.out.println("error happened while publishing: " + status.toString());
		            } else {
		                System.out.println("publish worked! timetoken: " + result.getTimetoken());
		            }
				}
				});
		
		String url = "/confirmation";
		url = response.encodeRedirectURL(url);
		response.sendRedirect(url);
		
		// sanitize user input to avoid XSS attacks:
//		newuser = StringEscapeUtils.escapeHtml4(newuser);
//		newpass = StringEscapeUtils.escapeHtml4(newpass);
////		
//		// add user's info to the database 
//		Status status = dbhandler.recordOrder(newuser, selectedItem);
//		
//		if(status == Status.OK) { // registration was successful
//			response.getWriter().println("Login is Successful.");
//			HttpSession session = request.getSession();
//			session.setAttribute("username", newuser);
//		
//			String url = "/placeorder";
//			url = response.encodeRedirectURL(url);
//			response.sendRedirect(url); // send a get request  (redirect to the same path)
//			//response.encodeRedirectURL("/hotels");
//			
//		}
//		else { // if something went wrong
//			String url = "/login?error=" + status.name();
//			url = response.encodeRedirectURL(url);
//			response.sendRedirect(url); // send a get request  (redirect to the same path)
//		}
	}

	
}