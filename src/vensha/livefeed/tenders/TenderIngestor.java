package vensha.livefeed.tenders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import vensha.livefeed.AbstractDataIngestor;
import vensha.livefeed.Entity;
import vensha.livefeed.EntityManager;

public class TenderIngestor extends AbstractDataIngestor {
private static final String BASE_URL = "http://tenders.gov.in/innerpage.asp?choice=ot1";

public TenderIngestor() {
	super(EntityManager.GOV_TENDERS);
}

@Override
public List<Entity> ingest(String resources, boolean useFileMode) throws Exception {
	List<Entity> list = new ArrayList<>();
	Document doc = Jsoup.connect("http://tenders.gov.in/innerpage.asp?choice=ot1").timeout(50 * 1000).get();  
	Elements pages = doc.select("a[href~=ot1]");
    int totalPages = Integer.parseInt(pages.get(pages.size()-2).text());;
    int page = 1;
    while (page < totalPages) {
    	 Iterator<Element> rows = doc.select("table.table").select("tr[align]").iterator();
    	 while(rows.hasNext()) {
    		 Tender t = new Tender();
    		 Iterator<Element> columns = rows.next().select("td").iterator();
    		 t.seqNumber = columns.next().text();
    		 t.expiryDate = columns.next().text();
    		 Element desc = columns.next();
    		 List<TextNode> texts = desc.textNodes();
    		 t.desc = desc.text();
    		 t.reference = texts.get(texts.size()-1).text();
    		 if (t.reference == null || t.reference.length() <= 2) {
    			 t.reference = t.seqNumber;
    		 }
    		 t.issuer = columns.next().text();
    		 if (!isDuplicate(t)) {
    			 list.add(t);
    		 }
    	 }
    	 page++;
    	 doc = Jsoup.connect("http://tenders.gov.in/innerpage.asp?choice=ot1&page=" + page).timeout(50 * 1000).get();  
    }
	
	
	return list;
}

public static void main(String[] args) throws Exception {
	TenderIngestor ingestor = new TenderIngestor();
	ingestor.ingest("na", false);
}

}
