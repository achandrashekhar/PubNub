import java.util.Arrays;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
/**
 * This Class is the second subscriber to the order channel and will write to the DataBase as soon as it receives
 * the message 
 * @author ashi
 *
 */
public class DataBaseUpdate {
	public static void main(String args[]) {
		PNConfiguration pnConfiguration = new PNConfiguration();
		pnConfiguration.setSubscribeKey("sub-c-e77a90ba-ddaa-11e6-ac93-02ee2ddab7fe");
		pnConfiguration.setPublishKey("pub-c-e4a6db26-6717-4b8f-8b0f-0441c4a5265a");  
		PubNub pubnubDataBaseUpdater = new PubNub(pnConfiguration);
		
		pubnubDataBaseUpdater.addListener(new SubscribeCallback() {
			
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
				System.out.println(arg1.getMessage().getAsJsonObject()); //retrieve msg from the channel
				jsonObjectemailNotifier = arg1.getMessage().getAsJsonObject();
				JsonElement jsonCustName = jsonObjectemailNotifier.get("custName");
				String custName = jsonCustName.toString();
				JsonElement jsonemailId = jsonObjectemailNotifier.get("email");
				String email = jsonemailId.toString();
				JsonElement jsonaddress = jsonObjectemailNotifier.get("address");
				String address = jsonaddress.toString();
				JsonElement jsonquantity = jsonObjectemailNotifier.get("quantity");
				String quantity = jsonquantity.toString();
				System.out.println("Got these values in the email Notifier"+email+"and quantity as "+quantity);
				HandleDB dbhandler = HandleDB.getInstance();
				dbhandler.writeToDataBase(custName, email, address, quantity);
			}
		});
		
		pubnubDataBaseUpdater.subscribe().channels(Arrays.asList("OrderChannel")).execute();
	}

}
