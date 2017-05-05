package vensha.livefeed.destinations;

import vensha.livefeed.Entity;

public class EchoDestination implements Destination {

@Override
public void close() {
}

@Override
public void send(Entity entity) {
	if (entity != null) System.out.print(entity.getId());
}



}
