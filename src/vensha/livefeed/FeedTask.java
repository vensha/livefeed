package vensha.livefeed;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import vensha.livefeed.destinations.Destination;
import vensha.livefeed.destinations.DestinationFactory;
import vensha.livefeed.utils.LogManager;

public class FeedTask implements Runnable {
private AbstractDataIngestor ingestor_;
private Properties props_;
private String id_;
private ScheduledFuture future_;

public FeedTask(String id, Properties props) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	id_ = id;
	props_ = props;
	ingestor_ = (AbstractDataIngestor) Class.forName(props_.getProperty(id_ + ".class")).newInstance();
}
public void setScheduledFuture(ScheduledFuture future) {
	future_ = future;
}

@Override
public void run() {
	
	try {
		List<Entity> entityList = ingestor_.doIngest(props_.getProperty(id_ + ".resources"), props_.getProperty(id_ + ".useFileMode", "false").equals("true"));
		Destination destination = DestinationFactory.getDestination(id_);
		int pcount=0;
		long start = System.currentTimeMillis();
		for (Entity entity:entityList) {
			destination.send(entity);
			pcount++;
		}
		destination.close();
		LogManager.log("Published " + pcount + " entities of type " + id_ + " in " + (System.currentTimeMillis()-start) + " ms" );
	} catch (Exception e) {
		LogManager.log(e);
	} finally {
		 
	}
}

public LocalDateTime getNextRuntime() {
	return LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() + future_.getDelay(TimeUnit.MILLISECONDS)), ZoneId.systemDefault());
		    
}

}
