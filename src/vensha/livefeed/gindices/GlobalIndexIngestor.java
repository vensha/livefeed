package vensha.livefeed.gindices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import vensha.livefeed.AbstractDataIngestor;
import vensha.livefeed.Entity;
import vensha.livefeed.EntityManager;
import vensha.livefeed.HeadlessScraper;
import vensha.livefeed.utils.LogManager;

public class GlobalIndexIngestor extends AbstractDataIngestor {

public GlobalIndexIngestor() {
	super(EntityManager.GLOBAL_INDICES);
}

@Override
public List<Entity> ingest(String jsFile, boolean useFileMode) throws Exception {
	List<String> contentFiles = null;
	if (useFileMode == false) { // Live mode, we wanna scrape
		HeadlessScraper scrapper = new HeadlessScraper();
		boolean error = scrapper.scrape(jsFile);
		if (error)
			throw new Exception("Error ingesting global indices data");
		contentFiles = scrapper.getContentFiles();
	} else {
		contentFiles = new ArrayList<>();
		contentFiles.add("/tmp/gi.html");
	}

	if (contentFiles.size() != 1) {
		LogManager.log("Error: Failed to ingest global indices data");
		return new ArrayList<Entity>();
	}
	List<Entity> indices = parseIndices(contentFiles.get(0));
	
	return indices;

}

private static List<Entity> parseIndices(String file) throws IOException {
	List<Entity> list = new ArrayList<>();
	Document doc = Jsoup.parse(new File(file), "utf-8");
	Iterator<Element> tables = doc.select("table[tablesorter]").iterator();

	boolean flag = true;
	while (tables.hasNext() && flag == true) {

		Element table = tables.next();
		// System.out.println(table);
		Iterator<Element> trs = table.select("tr").iterator();
		trs.next();
		while (trs.hasNext()) {
			Index idx = new Index();
			Element tr = trs.next();
			Iterator<Element> cols = tr.select("td").iterator();
			Element span = cols.next().select("span").first();
			idx.country = span.attr("title");
			idx.index = cols.next().text();
			idx.last = Double.parseDouble(cols.next().text().replace(",", ""));
			idx.high = Double.parseDouble(cols.next().text().replace(",", ""));
			idx.low = Double.parseDouble(cols.next().text().replace(",", ""));
			idx.change = cols.next().text();
			idx.percentChange = cols.next().text();
			idx.lastUpdated = cols.next().text();

			list.add(idx);
			// System.out.println(idx.country + ":" + idx.index + ":" +
			// idx.last);
		}

	}
	return list;
}

public static void main(String[] args) throws Exception {
	GlobalIndexIngestor s = new GlobalIndexIngestor();
	List<Entity> list = s.parseIndices("/tmp/gi.html");
	System.out.println(list.size());
}

}
