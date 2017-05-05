package vensha.livefeed.gindices;
import vensha.livefeed.Entity;

public class Index extends Entity {
public String country;
public String index;
public double last;
public double high;
public double low;
public String change;
public String percentChange;
public String lastUpdated;

@Override
public String getId() {
	return country+"_"+index+"_"+lastUpdated;
}


}
