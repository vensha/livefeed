package vensha.livefeed;

import java.util.List;

import vensha.livefeed.destinations.Destination;
import vensha.livefeed.destinations.DestinationFactory;
import vensha.utils.LogManager;

public class Publisher {


public static void publish(String type) {
	List<Entity> list = EntityManager.getInstance().getEntityList(type);
	if (list == null || list.size() == 0 ){
		LogManager.log("WARN:Entity list is empty for entity type=" + type);
		return;
	}
	long start = System.currentTimeMillis();
	Destination destination = DestinationFactory.getDestination(type);
	for (Entity entity:list) {
		destination.send(entity);
	}
	LogManager.log("---");
	long end = System.currentTimeMillis();
	LogManager.log("Published " + list.size() + " records for the type " + type + " in " + (end-start) + " ms");
}

}