package vensha.livefeed.rly;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import vensha.livefeed.AbstractDataIngestor;
import vensha.livefeed.Entity;
import vensha.livefeed.EntityManager;
import vensha.livefeed.DataFetcher;
import vensha.livefeed.utils.LogManager;

public class RlyDataIngestor extends AbstractDataIngestor {

public RlyDataIngestor() {
	super(EntityManager.RLY_FEED);
}

@Override
public List<Entity> ingest(String res, boolean useFileMode) throws Exception {
	List<String> contentFiles = null;
	if (useFileMode == false) { // Live mode
		DataFetcher df = new DataFetcher();
		boolean error = df.fetch(res);
		if (error)
			throw new Exception("Error fetching data");
		contentFiles = df.getContentFiles();
		LogManager.log("done");
	} else {
		contentFiles = new ArrayList<>();
		contentFiles.add("/tmp/rly_cancelled.html");
		contentFiles.add("/tmp/rly_rescheduled.html");
		contentFiles.add("/tmp/rly_diverted.html");

	}

	if (contentFiles.size() != 3) {
		LogManager.log("Error:Failed fetching data.");
		return new ArrayList<Entity>();
	}

	List<Entity> statusList = new ArrayList<>();
	handleCancellations(contentFiles.get(0), statusList);
	handleReschedules(contentFiles.get(1), statusList);
	handleDiversions(contentFiles.get(2), statusList);

	return statusList;


}

private void handleCancellations(String file, List<Entity> list) throws IOException {
	LogManager.log("Parsing cancellations");
	Document doc = Jsoup.parse(new File(file), "utf-8");
	Iterator<Element> rows = doc.select("table.allCancelledTrnTbl tr").iterator();
	if (!rows.hasNext()) {
		LogManager.log("No cancellations found");
		return;
	}
	rows.next();
	while (rows.hasNext()) {
		TrainStatus ts = new TrainStatus();
		ts.eventType = TrainStatus.FULLY_CANCELLED;
		Element row = rows.next();
		Iterator<Element> cols = row.select("td").iterator();
		if (cols.hasNext() == false)
			continue;
		ts.trainNo = cols.next().text();
		ts.trainName = cols.next().text();

		ts.startDate = cols.next().text();
		ts.trainType = cols.next().text();
		ts.trainSource = cols.next().text();
		ts.trainDestination = cols.next().text();
		if (cols.hasNext()) {
			ts.eventType = TrainStatus.PARTIALLY_CANCELLED;
			ts.cancelledFrom = cols.next().text();
			ts.cancelledTo = cols.next().text();
		}
		if (isDuplicate(ts) == false) {
			list.add(ts);
		}
	}
	LogManager.log("Done parsing cancellations");
}

private void handleReschedules(String file, List<Entity> list) throws IOException {
	LogManager.log("Parsing reschedules");
	Document doc = Jsoup.parse(new File(file), "utf-8");
	Iterator<Element> rows = doc.select("table.allCancelledTrnTbl tr").iterator();
	while (rows.hasNext()) {

		Element row = rows.next();

		Elements cols = row.select("td");
		if (cols.size() == 8 && cols.get(6).text().contains("hrs")) {
			TrainStatus ts = new TrainStatus();
			ts.eventType = TrainStatus.RESCHEDULED;
			ts.trainNo = cols.get(0).text();
			ts.trainName = cols.get(1).text();
			ts.startDate = cols.get(2).text();
			ts.trainType = cols.get(3).text();
			ts.trainSource = cols.get(4).text();
			ts.trainDestination = cols.get(5).text();
			ts.rescheduledBy = cols.get(6).text();
			ts.rescheduledTime = cols.get(7).text();

			if (isDuplicate(ts) == false) {
				list.add(ts);
			}

		}
	}
	LogManager.log("done Parsing reschedules");
}

private void handleDiversions(String file, List<Entity> list) throws IOException {
	LogManager.log("Parsing diversions");
	Document doc = Jsoup.parse(new File(file), "utf-8");
	Iterator<Element> rows = doc.select("table.allCancelledTrnTbl tr").iterator();
	boolean isDiverted = false;
	while (rows.hasNext()) {
		Element row = rows.next();
		Elements thCols = row.select("th");
		if (thCols.size() == 8 && thCols.get(7).text().equals("Diverted to")) {
			isDiverted = true;
			continue;
		}
		if (isDiverted) {
			Elements cols = row.select("td");
			TrainStatus ts = new TrainStatus();
			ts.eventType = TrainStatus.DIVERTED;
			ts.trainNo = cols.get(0).text();
			ts.trainName = cols.get(1).text();
			ts.startDate = cols.get(2).text();
			ts.trainType = cols.get(3).text();
			ts.trainSource = cols.get(4).text();
			ts.trainDestination = cols.get(5).text();
			ts.divertedFrom = cols.get(6).text();
			ts.divertedTo = cols.get(7).text();

			if (isDuplicate(ts) == false) {
				list.add(ts);
			}
		}

	}
	LogManager.log("done Parsing diversions");

}

}
