package vensha.livefeed.rly;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import vensha.livefeed.destinations.Destination;
import vensha.livefeed.destinations.DestinationFactory;
import vensha.utils.LogManager;

public class TrainStatusManager {
private Cache<String, TrainStatus> cache_;
private static TrainStatusManager instance_ = new TrainStatusManager();
private boolean atMostOnce_ = false;
private static List<TrainStatus> cachedList_;

private TrainStatusManager() {
	cache_ = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.DAYS).build();
	if (atMostOnce_)
		LogManager.log("Using 'At Most Once' symantics to push messages");
	else
		LogManager.log("Using 'At Least Once' symantics to push messages");
}

public static TrainStatusManager getInstance() {
	return instance_;
}

public void setStatusList(List<TrainStatus> list) {
	synchronized (cachedList_) {
		cachedList_ = list;
	}
}

public void sendUpdates() {
	synchronized (cachedList_) {
		LogManager.log("Number of train status updates=" + cachedList_.size());
		if (cachedList_.size() == 0) return;
		Destination sd = DestinationFactory.getDestination("rlyfeed");
		for (TrainStatus ts : cachedList_) {
			if (atMostOnce_) {
				if (cache_.getIfPresent(ts.getId()) == null) { //new status
					sd.send(ts);
					cache_.put(ts.getId(), ts);
				}
			} else {
				sd.send(ts);
			}
		}
		sd.close();
	}
}

}
