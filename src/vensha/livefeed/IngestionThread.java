package vensha.livefeed;

import java.util.Calendar;
import java.util.Properties;

import vensha.livefeed.utils.LogManager;

public class IngestionThread extends Thread {

String[] ingestors_ = null;
Properties cfg_;

public IngestionThread(String[] ingestors, Properties props) {
	ingestors_ = ingestors;
	cfg_ = props;

}

@Override
public void run() {
	final boolean useFileMode = cfg_.getProperty("use.fileMode", "false").equals("true");
	final int ingestFreqInMinutes = Integer.parseInt(cfg_.getProperty("ingest.interval.minutes", "60"));
	if (useFileMode){
		LogManager.log("Using file mode for data ingestion - data is simulated and not live");
	}else{
		LogManager.log("Using live mode for data ingestion");
	}

	while (true) {
		try {
			for (String ingestor : ingestors_) {
				AbstractDataIngestor di = (AbstractDataIngestor) Class.forName(ingestor).newInstance();
				String resources = cfg_.getProperty(di.id_ + ".resources");
				if (resources != null) {
					LogManager.log("Ingesting new data for " + di.id_ + " ...");
					di.doIngest(resources, useFileMode);
				}
			}

			Calendar c = Calendar.getInstance();
			c.add(Calendar.MINUTE, ingestFreqInMinutes);
			LogManager.log("IngestionThread suspended. It will wake up next at " + c.getTime());
			Thread.sleep(ingestFreqInMinutes * 60 * 1000);

		} catch (Exception e) {
			LogManager.log(e);
		}
	}

}

}
