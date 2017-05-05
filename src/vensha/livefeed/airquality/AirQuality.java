package vensha.livefeed.airquality;

import java.util.HashMap;
import java.util.Map;

import vensha.livefeed.Entity;

public class AirQuality extends Entity {
public String station;
public String city;
public String state;
public String lastUpdated;
public Map<String,String> parameters = new HashMap<>();

@Override
public String getId() {
	return station + lastUpdated;
}

public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append(station).append(",");
	sb.append(city).append(",");
	sb.append(state).append(",");
	sb.append(lastUpdated).append(",");
	sb.append(parameters.toString());
	
	return sb.toString();
}

}
