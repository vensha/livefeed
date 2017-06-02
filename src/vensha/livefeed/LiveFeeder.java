package vensha.livefeed;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import vensha.livefeed.airquality.AirQualityIngestor;
import vensha.livefeed.destinations.DestinationFactory;
import vensha.livefeed.gcurrencies.CurrencyIngestor;
import vensha.livefeed.gindices.GlobalIndexIngestor;
import vensha.livefeed.hn.HNIngestor;
import vensha.livefeed.rly.RlyDataIngestor;
import vensha.livefeed.utils.LogManager;
import vensha.livefeed.utils.Logger;

@SuppressWarnings("unused")
public class LiveFeeder {
private Properties props_ = null;
private ScheduledExecutorService scheduler_;


public LiveFeeder(String cfgFile) throws Exception {
	props_ = new Properties();
	try {
		props_.load(new FileInputStream(cfgFile));
	} catch (Exception e) {
		LogManager.log(e);
	}
	if (props_.getProperty("log.type", "console").equalsIgnoreCase("console")) {
		LogManager.setCurrentLogger(LogManager.getLogger());
	} else {
		Logger logger = LogManager.getFileLogger("LiveFeed", props_.getProperty("logfile"), true);
		LogManager.setCurrentLogger(logger);
		logger.setShowTimestamps(true);
	}
	DestinationFactory.init(props_);
	LogManager.log("LiveFeed 1.2 - vneldurg April,2017.");
}

public void start() {
	String[] ingestors = props_.getProperty("ingestors").split(",");
	scheduler_ = Executors.newScheduledThreadPool(ingestors.length);
	
	for (int i=0;i<ingestors.length;i++) {
		try {
			FeedTask task = new FeedTask(ingestors[i], props_);
			int pollInterval = Integer.parseInt(props_.getProperty(ingestors[i] + ".poll.freqInMinutes", "10"));
			LogManager.log("[Schedule] " + ingestors[i] + " is scheduled to run every " + pollInterval + " minutes");
			ScheduledFuture future = scheduler_.scheduleWithFixedDelay(task, 0, pollInterval, TimeUnit.MINUTES);
			task.setScheduledFuture(future);
		} catch (Exception e) {
			LogManager.log(e);
		} 
	}
	
}


public static void main(String[] args) throws Exception {
	if (args.length != 1) {
		System.out.println("java vensha.livefeed.LiveFeeder <cfg file>");
		System.exit(0);
	}
	LiveFeeder feed = new LiveFeeder(args[0]);
	feed.start();
}

}
