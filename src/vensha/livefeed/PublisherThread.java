package vensha.livefeed;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import vensha.utils.LogManager;

@SuppressWarnings("rawtypes")
public class PublisherThread extends Thread {

Set<String> entityIds_ = new HashSet<String>();
Properties cfg_;

public PublisherThread(Set<Class> ingestors, Properties props) {
	cfg_ = props;
	for (Class ingestor : ingestors) {
		AbstractDataIngestor di;
		try {
			di = (AbstractDataIngestor) ingestor.newInstance();
			entityIds_.add(di.id_);
		} catch (Exception e) {
			LogManager.log(e);
			throw new RuntimeException(e);
		} 
	
	}

}

@Override
public void run() {
	final int publishFreqInMinutes = Integer.parseInt(cfg_.getProperty("publish.interval.minutes", "10"));

	while (true) {
		try {
			for (String entityId : entityIds_) {
				LogManager.log("Publishing data for " + entityId + "...");
				Publisher.publish(entityId);
			}
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MINUTE, publishFreqInMinutes);
			LogManager.log("PublisherThread suspended. It will wake up next at " + c.getTime());
			Thread.sleep(publishFreqInMinutes * 60 * 1000);

		} catch (Exception e) {
			LogManager.log(e);
		}
	}

}

}
