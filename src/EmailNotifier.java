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
				jsonObjectemailNotifier = arg1.getMessage().getAsJsonObject();
				JsonElement jsonCustName = jsonObjectemailNotifier.get("custName");
				String custName = jsonCustName.toString();
				JsonElement jsonaddress = jsonObjectemailNotifier.get("address");
				String address = jsonaddress.toString();
				JsonElement jsonemailId = jsonObjectemailNotifier.get("email");
				String id = jsonemailId.toString();
				JsonElement jsonquantity = jsonObjectemailNotifier.get("quantity");
				String quantity = jsonquantity.toString();
				System.out.println("Got these values in the email Notifier"+id+"and quantity as "+quantity);
				EmailSender emailSender = new EmailSender(id,"Your Order for an iPhone has been placed ","Hello"+custName+","+ "\n\nyou have placed an order of "+quantity+" iPhone(s). you will recieve it shortyly at the following address you have provided:\n"+address+"\n\nCheers!\nP.S.This Real Time email is the courtesy of PubNub!");
			}
		});
		
		pubnub.subscribe().channels(Arrays.asList("OrderChannel")).execute();
	}

}
