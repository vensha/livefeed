package vensha.livefeed.destinations;

import vensha.livefeed.Entity;
import vensha.livefeed.utils.LogManager;

public class EchoDestination implements Destination {

@Override
public void close() {
}

@Override
public void send(Entity entity) {
	if (entity != null) {
		//System.out.print(entity.getId());
		LogManager.log("P:" + entity.getId());
	}
}



}
