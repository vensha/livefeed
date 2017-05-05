package vensha.livefeed;

import java.util.List;

public interface DataIngestor {
public List<Entity> ingest(String jsFile, boolean useFileMode) throws Exception ;

}
