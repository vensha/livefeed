package vensha.livefeed.destinations;
import com.satori.rtm.Ack;
import com.satori.rtm.RtmClient;
import com.satori.rtm.RtmClientBuilder;
import com.satori.rtm.auth.RoleSecretAuthProvider;
import com.satori.rtm.model.Pdu;
import com.satori.rtm.model.PublishReply;

import vensha.livefeed.Entity;
import vensha.utils.LogManager;

public class  SatoriDestination implements Destination {

private RtmClient client_;
private String channel_ = null;

public SatoriDestination(String endpoint, String appKey, String role, String roleSecretKey, String channel) {
	client_ = new RtmClientBuilder(endpoint, appKey)
	        .setAuthProvider(new RoleSecretAuthProvider(role, roleSecretKey))
	        .build();
	
	channel_ = channel;
	client_.start();
}

@Override
public void send(Entity entity) {
	try {
		Pdu<PublishReply> reply;
		reply = client_.publish(channel_, entity, Ack.YES).get();
		//LogManager.log(reply.toString());
		//client_.publish(channel_, entity, Ack.NO);
	} catch (Exception e) {
		LogManager.log(e);
	} 
	 
}

public void close() {
	client_.shutdown();
}




}
