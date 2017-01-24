import java.util.Arrays;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
/**
 * Just a test program I made when I was initially playing around with the API
 * @author ashi
 *
 */
public class HelloWorld{
private static final String AWESOME_CHANNEL = "awesomeChannel!";

public static void main(String[] args) {
    PNConfiguration pnConfiguration = new PNConfiguration();
    pnConfiguration.setSubscribeKey("sub-c-e77a90ba-ddaa-11e6-ac93-02ee2ddab7fe");
    pnConfiguration.setPublishKey("pub-c-e4a6db26-6717-4b8f-8b0f-0441c4a5265a");
     
    PubNub pubnub = new PubNub(pnConfiguration);
    PubNub pubnub2 = new PubNub(pnConfiguration);
 
    pubnub.addListener(new SubscribeCallback() {
        @Override
        public void status(PubNub pubnub, PNStatus status) {
            if (status.getCategory() == PNStatusCategory.PNConnectedCategory){
                pubnub.publish().channel(AWESOME_CHANNEL).message("this is a message").async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        // handle publish response
                    }
                });
                try {
					pubnub.publish().channel(AWESOME_CHANNEL).message("NEw message").sync();
				} catch (PubNubException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
     
        @Override
        public void message(PubNub pubnub, PNMessageResult message) {
        	System.out.println("Message received by object : "+pubnub.getInstanceId()+" " + message.getMessage().getAsString());
        
        }
     
        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {
     
        }
    });
   
    pubnub.subscribe().channels(Arrays.asList(AWESOME_CHANNEL)).execute();
   // pubnub2.subscribe().channels(Arrays.asList(AWESOME_CHANNEL)).execute();
    
    
}
}