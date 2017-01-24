import java.util.Arrays;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
/**
 * Another test program I made when dabbling around with the API
 * @author ashi
 *
 */
public class Subscriber {
	public void message(PubNub pubnub, PNMessageResult message) {
    	System.out.println("Message received by object : "+pubnub.getInstanceId()+" " + message.getMessage().getAsString());
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PNConfiguration pnConfiguration = new PNConfiguration();
		pnConfiguration.setPublishKey("pub-c-e4a6db26-6717-4b8f-8b0f-0441c4a5265a");
	    pnConfiguration.setSubscribeKey("sub-c-e77a90ba-ddaa-11e6-ac93-02ee2ddab7fe");
	    PubNub pubnub = new PubNub(pnConfiguration);
	    PubNub pubnub2 = new PubNub(pnConfiguration);
	    pubnub.publish()
	    .message(Arrays.asList("hello", "there"))
	    .channel("my_channel")
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
	    
	    pubnub2.subscribe().channels(Arrays.asList("my_channel")).execute();
        
	}

}
