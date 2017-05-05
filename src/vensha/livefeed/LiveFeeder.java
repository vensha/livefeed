package vensha.livefeed;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import vensha.livefeed.airquality.AirQualityIngestor;
import vensha.livefeed.destinations.DestinationFactory;
import vensha.livefeed.gcurrencies.CurrencyIngestor;
import vensha.livefeed.gindices.GlobalIndexIngestor;
import vensha.livefeed.hn.HNIngestor;
import vensha.livefeed.rly.RlyDataIngestor;
import vensha.livefeed.utils.LogManager;

@SuppressWarnings("unused")
public class LiveFeeder {
private Properties props_ = null;

@SuppressWarnings("rawtypes")
private Set<Class> ingestors_ = new HashSet<>();

public LiveFeeder(String cfgFile) throws Exception {
	props_ = new Properties();
	try {
		props_.load(new FileInputStream(cfgFile));
		Utils.setConfig(props_);
	} catch (Exception e) {
		LogManager.log(e);
	}
	if (props_.getProperty("log.type", "console").equalsIgnoreCase("console")) {
		LogManager.setCurrentLogger(LogManager.getLogger());
	} else {
		LogManager.setCurrentLogger(LogManager.getFileLogger("LiveFeed", "livefeed.log", true));
	}
	DestinationFactory.init(props_);
	EntityManager.init(props_);
	LogManager.log("LiveFeed 1.0 - vneldurg April,2017.");
}


@SuppressWarnings("rawtypes")
public void start(Class ... ingestors) {
	for (Class ingestor : ingestors) {
        ingestors_.add(ingestor);
    }
	IngestionThread it = new IngestionThread(ingestors_, props_);
	it.start();
	
	PublisherThread pt = new PublisherThread(ingestors_, props_);
	pt.start();
}


public static void main(String[] args) throws Exception {
	if (args.length != 1) {
		System.out.println("java vneldurg.livefeed.LiveFeeder <cfg file>");
		System.exit(0);
	}
	LiveFeeder feed = new LiveFeeder(args[0]);
	feed.start(HNIngestor.class, AirQualityIngestor.class);
	
}

}
