package vensha.livefeed;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import vensha.utils.LogManager;

public abstract class AbstractDataIngestor implements DataIngestor {
protected String id_ = null;
protected boolean useFileMode_;
private static Cache<String, Entity> cache_ = null;

static {
	int expiry = Integer.parseInt(Utils.getConfig().getProperty("cache.expiry.minutes", "1440"));
	cache_ = CacheBuilder.newBuilder().expireAfterWrite(expiry, TimeUnit.MINUTES).build();
}

public AbstractDataIngestor(String id) {
	id_ = id;
}


public void doIngest(String resources, boolean useFileMode) throws Exception {
	long start  = System.currentTimeMillis();
	List<Entity> entityList = ingest(resources, useFileMode);
	EntityManager.getInstance().setEntityList(id_, entityList);
	long end  = System.currentTimeMillis();
	LogManager.log("Ingested " + entityList.size() + " records for type:" + id_ + " in " + (end-start) + " ms");
}

public boolean isDuplicate(Entity e) {
	if (cache_.getIfPresent(e.getId()) != null) {
		return true;
	}else{
	   cache_.put(e.getId(), e);
	   return false;
	}
	
}

}
