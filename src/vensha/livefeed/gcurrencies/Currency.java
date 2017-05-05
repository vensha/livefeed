package vensha.livefeed.gcurrencies;
import vensha.livefeed.Entity;

public class Currency extends Entity {
public String pair;
public double bid;
public double ask;
public double open;
public double high;
public double low;
public String change;
public String percentChange;
public String lastUpdated;

//perf
public String day_perf;
public String week_pref;
public String month_perf;
public String ytd_perf;
public String year_perf;
public String three_year_perf;

//Tech
public String hour_indicator;
public String day_indicator;
public String month_indicator;

@Override
public String getId() {
	return pair + "_" + lastUpdated;
}

@Override
public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append(pair).append(",");
	sb.append(bid).append(",");
	sb.append(ask).append(",");
	sb.append(open).append(",");
	sb.append(high).append(",");
	sb.append(low).append(",");
	sb.append(change).append(",");
	sb.append(percentChange).append(",");
	sb.append(lastUpdated).append(",");
	sb.append(day_perf).append(",");
	sb.append(week_pref).append(",");
	sb.append(month_perf).append(",");
	sb.append(ytd_perf).append(",");
	sb.append(year_perf).append(",");
	sb.append(three_year_perf).append(",");
	sb.append(hour_indicator).append(",");
	sb.append(day_indicator).append(",");
	sb.append(month_indicator).append(",");
	
	return sb.toString();
}


}
