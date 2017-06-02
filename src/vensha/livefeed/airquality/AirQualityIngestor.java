package vensha.livefeed.airquality;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
private static Map<String, String> stateMap_ = new HashMap<>();
private static Map<String, String> cityMap_ = new HashMap<>();

static {
	stateMap_.put("1", "Andhra Pradesh");
	stateMap_.put("2", "Arunachal Pradesh");
	stateMap_.put("3", "Assam");
	stateMap_.put("4", "Bihar");
	stateMap_.put("5", "Chhattisgarh");
	stateMap_.put("6", "Delhi");
	stateMap_.put("7", "Goa");
	stateMap_.put("8", "Gujarat");
	stateMap_.put("9", "Haryana");
	stateMap_.put("10", "Himachal Pradesh");
	stateMap_.put("11", "Jammu and Kashmir");
	stateMap_.put("12", "Jharkhand");
	stateMap_.put("13", "Karnataka");
	stateMap_.put("14", "Kerala");
	stateMap_.put("15", "Madya Pradesh");
	stateMap_.put("16", "Maharashtra");
	stateMap_.put("17", "Manipur");
	stateMap_.put("18", "Meghalaya");
	stateMap_.put("19", "Mizoram");
	stateMap_.put("20", "Nagaland");
	stateMap_.put("21", "Orissa");
	stateMap_.put("22", "Punjab");
	stateMap_.put("23", "Rajasthan");
	stateMap_.put("24", "Sikkim");
	stateMap_.put("25", "Tamil Nadu");
	stateMap_.put("26", "Tripura");
	stateMap_.put("27", "Uttaranchal");
	stateMap_.put("28", "Uttar Pradesh");
	stateMap_.put("29", "West Bengal");
	stateMap_.put("30", "Telangana");
	
	cityMap_.put("7", "Hyderabad");
	cityMap_.put("9", "Visakhapatnam");
	cityMap_.put("21","Tirumala");
	cityMap_.put("54","Muzaffarpur");
	cityMap_.put("70","Patna");
	cityMap_.put("75", "Gaya");
	cityMap_.put("85", "Delhi");
	cityMap_.put("136", "Bangalore");
	cityMap_.put("178", "Kanpur");
	cityMap_.put("188" , "Amritsar");
	cityMap_.put("194","Ludhiana");
	cityMap_.put("212", "Jodhpur");
	cityMap_.put("223", "Jaipur");
	cityMap_.put("253", "Agra");
	cityMap_.put("256", "Lucknow");
	cityMap_.put("278", "Kanpur");
	cityMap_.put("270","Varanasi");
	cityMap_.put("300", "Calcutta");
	cityMap_.put("307", "Nashik");
	cityMap_.put("308", "Aurangabad");
	cityMap_.put("309", "Thane");
	cityMap_.put("310","Mumbai");
	cityMap_.put("312", "Pune");
	cityMap_.put("314", "Solapur");
	cityMap_.put("327", "Nagpur");
	cityMap_.put("329","Chandrapur");
    cityMap_.put("337", "Ahmedabad");
	cityMap_.put("348","Panchkula");
	cityMap_.put("360", " Rohtak");
	cityMap_.put("364","Gurgaon");
	cityMap_.put("365","Faridabad");
	cityMap_.put("546", "Chennai");
	cityMap_.put("548","Howrah");
	cityMap_.put("549", "Haldia");
	cityMap_.put("552","Durgapur");
	cityMap_.put("553", "Mandi Gobindgarh");
	cityMap_.put("554", "Ghaziabad");
	cityMap_.put("555", "Noida");
	cityMap_.put("556", "Jorapokhar");
	cityMap_.put("557", "Vijayawara");
	
	
}

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
	Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
	Element table = doc.select("table").get(1);
	Iterator<Element> trs = table.select("tr[style~=margin]").iterator();

	AirQuality aq = createAirQuality(stationId);
	while (trs.hasNext()) {
		Elements tds = trs.next().select("td");

		// We get time for every param separately, overwriting it here so that we set last time
		Map<String,String> param = new HashMap<>();
		param.put("Measure", tds.get(3).text());
		param.put("Units", tds.get(4).text());
		aq.parameters.put(tds.get(0).text(), param);
		aq.lastUpdated = tds.get(1).text() + " " + tds.get(2).text();
	}
	return aq;
}

// StationName=BTM&StateId=13&CityId=136
private static AirQuality createAirQuality(String stationId) throws UnsupportedEncodingException {
	AirQuality aq = new AirQuality();
	String[] sinfo = stationId.split("&");
	aq.station = URLDecoder.decode(sinfo[0].split("=")[1], "UTF-8");
	aq.state = stateMap_.get(URLDecoder.decode(sinfo[1].split("=")[1], "UTF-8"));
	if (aq.state==null) System.out.println("state not found:" + stationId);
	aq.city = cityMap_.get(URLDecoder.decode(sinfo[2].split("=")[1].trim(), "UTF-8"));
	if (aq.city==null) System.out.println("city not found:" + stationId);
	return aq;
}

}
