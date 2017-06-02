package vensha.livefeed.thunder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import vensha.livefeed.AbstractDataIngestor;
import vensha.livefeed.Entity;
import vensha.livefeed.EntityManager;
import vensha.livefeed.utils.LogManager;
import vensha.livefeed.utils.NetUtil;

public class ThunderIngestor extends AbstractDataIngestor {
private static final String HN_MAX_ID_URL = "https://hacker-news.firebaseio.com/v0/maxitem.json?print=pretty";
private static final String HN_ITEM_BASE_URL = "https://hacker-news.firebaseio.com/v0/item/"; // 14172070.json?print=pretty
private static final int maxLookbackCount = 10;

public ThunderIngestor() {
	super(EntityManager.THUNDER_LIGHTENINING);
}

@Override
public List<Entity> ingest(String metaFile, boolean useFileMode) throws Exception {
	Properties meta = new Properties();
	try {
		meta.load(new FileInputStream(metaFile));
	} catch (Exception e) {
		LogManager.log("No meta file found. Assuming first run");
	}

	String watermarkStr = meta.getProperty("thunder.watermark.timestamp", "1970-01-01 00:00");

	List<Entity> list = doIngest(watermarkStr);
	System.out.println("s="+list.size());
	Signal signal = (Signal)list.get(0);
	meta.put("thunder.watermark.timestamp",signal.lastSignal);
	meta.store(new FileOutputStream(metaFile), "Updated programatically");
	return list;

}

private List<Entity> doIngest(String watermark) throws Exception {
	Document doc = Jsoup.connect(
			"http://en.blitzortung.org/station_list.php?&region_country=-&stations_users=0&sort_column=last_signal&sort_order=desc&limit_rows=2000")
			.timeout(120 * 1000).get();
	Iterator<Element> rows = doc.select("table").get(2).select("tr").iterator();
	for (int i = 1; i <= 6; i++) {
		rows.next();
	}
    List<Entity> list = new ArrayList<>();
    boolean keepGoing=true;
	while (rows.hasNext() && keepGoing) {
		Elements columns = rows.next().select("td");
		if (columns.size() >= 8) {
			Signal signal = new Signal();
			signal.stationId = columns.get(1).text();
			signal.city = columns.get(2).text();
			signal.comment = columns.get(3).text();
			signal.country = columns.get(4).text();
			signal.status = columns.get(5).text();
			signal.lastSignal = columns.get(6).text();
			signal.numberOfSignalsInLastHour = columns.get(7).text();
			if (signal.lastSignal.compareTo(watermark) > 0) {
			   list.add(signal);
			}else{
				keepGoing = false;
			}
		}
	}

	return list;
}

public static void main(String[] args) throws Exception {
	ThunderIngestor ingestor = new ThunderIngestor();
	ingestor.ingest("/tmp/thunder.ini", false);
}

}
