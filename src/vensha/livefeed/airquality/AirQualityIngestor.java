package vensha.livefeed.airquality;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import vensha.livefeed.AbstractDataIngestor;
import vensha.livefeed.Entity;
import vensha.livefeed.EntityManager;
import vensha.livefeed.utils.LogManager;

public class AirQualityIngestor extends AbstractDataIngestor {
private static final String BASE_URL = "http://www.cpcb.gov.in/CAAQM/frmCurrentDataNew.aspx?"; // StationName=Victoria&StateId=29&CityId=300";

public AirQualityIngestor() {
	super(EntityManager.INDIA_AIR_QUALITY);
}

@Override
public List<Entity> ingest(String stationList, boolean useFileMode) throws Exception {
	if (useFileMode) {
		LogManager.log("[WARN] File mode is not supported for AirQuality Data Ingestor. Using live mode");
	}
	List<Entity> list = new ArrayList<>();
	String[] stations = stationList.split(",");
	for (int i = 0; i < stations.length; i++) {
		try {
			list.add(parseAirQuality(stations[i]));
		} catch (Exception e) {
			LogManager.log(e);
		}
	}
	return list;
}

public static Entity parseAirQuality(String stationId) throws IOException {
	String url = BASE_URL + stationId.trim();
	LogManager.log("AirQuality url:" + url);
	Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
	Element table = doc.select("table").get(1);
	Iterator<Element> trs = table.select("tr[style~=margin]").iterator();

	AirQuality aq = createAirQuality(stationId);
	while (trs.hasNext()) {
		Elements tds = trs.next().select("td");

		// We get time for every param separetly, over here we are overwriting
		// it ,so we set the time as last one
		aq.parameters.put(tds.get(0).text(), tds.get(3).text() + " " + tds.get(4).text());
		aq.lastUpdated = tds.get(1).text() + " " + tds.get(2).text();
	}
	System.out.println(aq);
	return aq;
}

// StationName=BTM&StateId=13&CityId=136
private static AirQuality createAirQuality(String stationId) throws UnsupportedEncodingException {
	AirQuality aq = new AirQuality();
	String[] sinfo = stationId.split("&");
	aq.station = URLDecoder.decode(sinfo[0].split("=")[1], "UTF-8");
	aq.state = URLDecoder.decode(sinfo[1].split("=")[1], "UTF-8");
	aq.city = URLDecoder.decode(sinfo[2].split("=")[1], "UTF-8");

	return aq;
}

}
