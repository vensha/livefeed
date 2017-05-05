package vensha.livefeed.destinations;

import vensha.livefeed.Entity;

public interface  Destination {
public void send(Entity entity);
public void close();


}
