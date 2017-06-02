package vensha.livefeed;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import vensha.livefeed.utils.LogManager;

public abstract class AbstractDataIngestor implements DataIngestor {
protected String id_ = null;
protected boolean useFileMode_;
private static Cache<String, Entity> cache_ = null;
List<Entity> entityList_ = null;

static {
	int expiry = 1440;
	cache_ = CacheBuilder.newBuilder().expireAfterWrite(expiry, TimeUnit.MINUTES).build();
}

public AbstractDataIngestor(String id) {
	id_ = id;
}


public List<Entity> doIngest(String resources, boolean useFileMode) throws Exception {
	entityList_ = new ArrayList<>();
	long start  = System.currentTimeMillis();
	LogManager.log("Starting ingestion for " + id_);
	List<Entity> entityList = ingest(resources, useFileMode);
	//EntityManager.getInstance().setEntityList(id_, entityList);
	long end  = System.currentTimeMillis();
	LogManager.log("Ingested " + entityList.size() + " records for type:" + id_ + " in " + (end-start) + " ms");
	return entityList;
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
