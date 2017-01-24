import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
/**
 * This class will return a PubNub instance with all theredentials
 * I didn't want to keep making an instance again and again so used the singleton pattern!
 * @author ashi
 *
 */
public class PubNubInstance {
	private static PubNub pubnub;
    public static PubNub getInstance(){
    	if (pubnub == null) {
    		initializePubNub();
    	}
    	return pubnub;
    }
	private static void initializePubNub() {
		PNConfiguration pnConfiguration = new PNConfiguration();
		pnConfiguration.setSubscribeKey("sub-c-e77a90ba-ddaa-11e6-ac93-02ee2ddab7fe");
		pnConfiguration.setPublishKey("pub-c-e4a6db26-6717-4b8f-8b0f-0441c4a5265a");  
		pubnub = new PubNub(pnConfiguration);
	}

}
