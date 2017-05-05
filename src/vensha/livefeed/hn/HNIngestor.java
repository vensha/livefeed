package vensha.livefeed.hn;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import vensha.livefeed.AbstractDataIngestor;
import vensha.livefeed.Entity;
import vensha.livefeed.EntityManager;
import vensha.livefeed.utils.LogManager;
import vensha.livefeed.utils.NetUtil;

public class HNIngestor extends AbstractDataIngestor {
private static final String HN_MAX_ID_URL = "https://hacker-news.firebaseio.com/v0/maxitem.json?print=pretty";
private static final String HN_ITEM_BASE_URL = "https://hacker-news.firebaseio.com/v0/item/" ; //14172070.json?print=pretty
private static final int maxLookbackCount=10;

public HNIngestor() {
	super(EntityManager.HN_ITEMS);
}

@Override
public List<Entity> ingest(String metaFile, boolean useFileMode) throws Exception {
	Properties meta = new Properties();
	meta.load(new FileInputStream(metaFile));
	String maxIdStr = NetUtil.fetchWebPage(HN_MAX_ID_URL);
	LogManager.log("Max item id on hacker news=" + maxIdStr);
	int currentHnMaxId = Integer.parseInt(maxIdStr.trim());
	
	String watermarkStr = meta.getProperty("hn.max.id", "NOT_SET");
	int startId=0;
	if (watermarkStr.equals("NOT_SET")) {
		startId = currentHnMaxId - maxLookbackCount;
	}else{
		startId = Integer.parseInt(watermarkStr);
	}
	List<Entity> list = doIngest(startId,currentHnMaxId);
	meta.put("hn.max.id", ""+currentHnMaxId);
	meta.store(new FileOutputStream(metaFile), "Updated programatically");
	return list;
	
}

private List<Entity> doIngest(int startId, int endId) throws Exception {
	Gson gson = new Gson();
	LogManager.log("Ingesting hacker news items from id " + startId + " to " + endId);
	List<Entity> list = new ArrayList<>();
	for (int i=startId;i<=endId;i++) {
		LogManager.log("HackerNews: Processing item " + i);
		HNItem item = new HNItem();
		String response = NetUtil.fetchWebPage(HN_ITEM_BASE_URL + i + ".json?print=pretty");
		Type type = new TypeToken<Map<String, Object>>(){}.getType();
		Map<String, Object> map = gson.fromJson(response,type);
		item.item = map;
		list.add(item);
	}
	return list;
}

public static void main(String[] args) throws Exception {
	HNIngestor ingestor = new HNIngestor();
	ingestor.ingest("/tmp/test.properties", false);
}

}
