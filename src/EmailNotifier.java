import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

/**
 * This class is a subscriber to the OrderChannel and as soon as a user places an order, this subscriber will recieve the details
 * and send out the email instantly
 * @author ashi
 *
 */
public class EmailNotifier {
	public static void main(String args[]) {
		PNConfiguration pnConfiguration = new PNConfiguration();
		pnConfiguration.setSubscribeKey("sub-c-e77a90ba-ddaa-11e6-ac93-02ee2ddab7fe");
		pnConfiguration.setPublishKey("pub-c-e4a6db26-6717-4b8f-8b0f-0441c4a5265a");  
		PubNub pubnub = new PubNub(pnConfiguration);
		
		pubnub.addListener(new SubscribeCallback() {
			
			@Override
			public void status(PubNub arg0, PNStatus arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void presence(PubNub arg0, PNPresenceEventResult arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void message(PubNub arg0, PNMessageResult arg1) {
				JsonObject jsonObjectemailNotifier = new JsonObject();
				System.out.println(arg1.getMessage().getAsJsonObject());
				jsonObjectemailNotifier = arg1.getMessage().getAsJsonObject(); //retrieve the message from the orderChannel
				JsonElement jsonemailId = jsonObjectemailNotifier.get("email");
				String id = jsonemailId.toString();
				JsonElement jsoncustName = jsonObjectemailNotifier.get("custName");
				String custName = jsoncustName.toString().replaceAll("\"", "");
				JsonElement jsonaddress = jsonObjectemailNotifier.get("address");
				String address = jsonaddress.toString().replaceAll("\"", "");
				JsonElement jsonquantity = jsonObjectemailNotifier.get("quantity");
				String quantity = jsonquantity.toString();
				System.out.println("Got these values in the email Notifier"+id+"and quantity as "+quantity);
				EmailSender emailSender = new EmailSender(id,"Your Order for an iPhone has been placed ","Hello "+custName+"," +"\n\nYou have placed an order of "+quantity+" iPhone(s) you will recieve it at the following address-\n"+address+"\n\nCheers!\nPS. This Real Time email is the courtesy of PubNub!");
			}
		});
		
		pubnub.subscribe().channels(Arrays.asList("OrderChannel")).execute();
	}

}
