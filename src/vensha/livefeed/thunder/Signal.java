package vensha.livefeed.thunder;

import vensha.livefeed.Entity;

public class Signal extends Entity {
public String stationId;
public String city;
public String country;
public String comment;
public String status;
public String lastSignal;
public String numberOfSignalsInLastHour;

@Override
public String getId() {
	return stationId + lastSignal;
}

}
