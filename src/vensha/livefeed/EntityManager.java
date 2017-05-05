package vensha.livefeed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import vensha.utils.LogManager;

public class EntityManager {

private static EntityManager instance_ = new EntityManager();

private Map<String,List<Entity>> entityCache_ = new HashMap<>();
public static final String RLY_FEED = "rlyfeed";
public static final String GLOBAL_INDICES = "gindices";
public static final String GLOBAL_CURRENCIES = "gcurrencies";
public static final String INDIA_AIR_QUALITY = "airquality";
public static final String HN_ITEMS = "hackernews";

private static boolean atMostOnce_ = false;



private EntityManager() {
	
}

public static void init(Properties cfg) {
    atMostOnce_ = cfg.getProperty("use.atMostOnce.symantics", "false").equals("true");
	if (atMostOnce_) {
		LogManager.log("Using 'AtMostOnce' symantics to push messages");
	}
	
}

public static EntityManager getInstance() {
	return instance_;
}

public synchronized void setEntityList(String type, List<Entity> updatedList) {
    entityCache_.put(type, updatedList);
}

public synchronized List<Entity> getEntityList(String type) {
		List<Entity> list = entityCache_.get(type);
		if (list == null) return new ArrayList<Entity>();
		
		List<Entity> copy = new ArrayList<Entity>(entityCache_.get(type)); //Return a copy of it.
		if (atMostOnce_) {
			setEntityList(type, new ArrayList<Entity>());
			LogManager.log("Cleared the queue after publising");
		}
		return copy;
}

}
