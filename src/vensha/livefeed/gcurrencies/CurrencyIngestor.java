package vensha.livefeed.gcurrencies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import vensha.livefeed.AbstractDataIngestor;
import vensha.livefeed.Entity;
import vensha.livefeed.EntityManager;
import vensha.livefeed.HeadlessScraper;
import vensha.livefeed.utils.LogManager;

public class CurrencyIngestor extends AbstractDataIngestor {

public CurrencyIngestor() {
	super(EntityManager.GLOBAL_CURRENCIES);
}

@Override
public List<Entity> ingest(String jsFile, boolean useFileMode) throws Exception {
	List<String> contentFiles = null;
	if (useFileMode == false) { // Live mode, we wanna scrape
		HeadlessScraper scrapper = new HeadlessScraper();
		boolean error = scrapper.scrape(jsFile);
		if (error)
			throw new Exception("Error fethcing currencies");
		contentFiles = scrapper.getContentFiles();
	} else {
		contentFiles = new ArrayList<>();
		contentFiles.add("/tmp/gc_price.html");
		contentFiles.add("/tmp/gc_perf.html");
		contentFiles.add("/tmp/gc_tech.html");
	}

	if (contentFiles.size() != 3) {
		LogManager.log("Error:Failed to fetch currencies.");
		return new ArrayList<Entity>();
	}

	Map<String, Currency> currencyMap = parse(contentFiles);
	return new ArrayList<Entity>(currencyMap.values());
}

public static Map<String, Currency> parse(List<String> contentFiles) throws IOException {
	Map<String, Currency> currencyMap = parsePrices(contentFiles.get(0));
	parsePerformances(contentFiles.get(1), currencyMap);
	parseTechnicalIndicators(contentFiles.get(2), currencyMap);

	return currencyMap;
}

private static Map<String, Currency> parsePrices(String file) throws IOException {
	Map<String, Currency> currencyMap = new HashMap<>();
	Document doc = Jsoup.parse(new File(file), "utf-8");
	Iterator<Element> rows = doc.select("table#cr1").first().select("tr").iterator();
	rows.next();
	while (rows.hasNext()) {
		Currency curr = new Currency();
		Element row = rows.next();
		// System.out.println(row);
		Iterator<Element> cols = row.select("td:not(.icon)").iterator();
		curr.pair = cols.next().text();
		curr.bid = Double.parseDouble(cols.next().text().replaceAll(",", ""));
		curr.ask = Double.parseDouble(cols.next().text().replaceAll(",", ""));
		curr.open = Double.parseDouble(cols.next().text().replaceAll(",", ""));
		curr.high = Double.parseDouble(cols.next().text().replaceAll(",", ""));
		curr.low = Double.parseDouble(cols.next().text().replaceAll(",", ""));
		curr.change = cols.next().text();
		curr.percentChange = cols.next().text();
		curr.lastUpdated = cols.next().text();

		currencyMap.put(curr.pair, curr);
	}

	return currencyMap;
}

private static void parsePerformances(String file, Map<String, Currency> currencyMap) throws IOException {
	Document doc = Jsoup.parse(new File(file), "utf-8");
	Iterator<Element> rows = doc.select("table#marketsPerformance").first().select("tr").iterator();
	rows.next();
	while (rows.hasNext()) {
		Element row = rows.next();
		// System.out.println(row);
		Iterator<Element> cols = row.select("td:not(.icon)").iterator();
		String pair = cols.next().text();
		Currency curr = currencyMap.get(pair);

		curr.day_perf = cols.next().text();
		curr.week_pref = cols.next().text();
		curr.month_perf = cols.next().text();
		curr.ytd_perf = cols.next().text();
		curr.year_perf = cols.next().text();
		curr.three_year_perf = cols.next().text();

	}

}

private static void parseTechnicalIndicators(String file, Map<String, Currency> currencyMap) throws IOException {
	Document doc = Jsoup.parse(new File(file), "utf-8");
	Iterator<Element> rows = doc.select("table#marketsTechnical").first().select("tr").iterator();
	rows.next();
	while (rows.hasNext()) {
		Element row = rows.next();
		// System.out.println(row);
		Iterator<Element> cols = row.select("td:not(.icon)").iterator();
		String pair = cols.next().text();
		Currency curr = currencyMap.get(pair);

		curr.hour_indicator = cols.next().text();
		curr.day_indicator = cols.next().text();
		curr.month_indicator = cols.next().text();

	}

}
public static void main(String[] args) throws Exception {
	new CurrencyIngestor().ingest("null", true);

}

}
