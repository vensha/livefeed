package vensha.livefeed.tenders;

import vensha.livefeed.Entity;

public class Tender extends Entity {
public String seqNumber;
public String expiryDate;
public String desc;
public String reference;
public String issuer;

@Override
public String getId() {
	return reference;
}

}
