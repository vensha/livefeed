package vensha.livefeed.hn;

import java.util.Map;

import vensha.livefeed.Entity;

public class HNItem extends Entity {
public Map<String,Object> item;

@Override
public String getId() {
	//return ""+item.hashCode();
	return (String) item.get("id");
}

@Override
public String toString() {
	return item.toString();
}

}
